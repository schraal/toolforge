/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Patch;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.PatchLine;
import nl.toolforge.karma.core.vc.model.MainLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.log.LogInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class CVSModuleStatus implements ModuleStatus {

  private static final Log logger = LogFactory.getLog(Utils.class);

  private boolean existsInRepository = false;

  private List matchingList = null;

  private Module module = null;
  private LogInformation logInfo = null;
  private boolean connectionFailure = false;
  private boolean authenticationFailure = false;
  private boolean internalError = false;

  /**
   * Creates a ModuleStatus instance with the LogInformation object that was generated by the <code>cvs log</code>
   * command.
   *
   * @param module The module.
   * @param logInfo A Netbeans LogInformation object.
   */
  public CVSModuleStatus(Module module, LogInformation logInfo) {
    this.module = module;
    setLogInformation(logInfo);
  }

  public CVSModuleStatus(Module module) {
    this.module = module;
  }

  public void setLogInformation(Object logInfo) {
    this.logInfo = (LogInformation) logInfo;
    matchingList = collectVersions(module);
  }

  /**
   * Returns a Version instance representing the next possible version (major or patch) for the module.
   *
   * @return
   */
  public Version getNextVersion() {

    if (matchingList.size() == 0) {

      // If the module is in a ReleaseManifest and has a PatchLine already, we can savely
      // return the initial patch level for the module.
      //
      // todo hmm (see sourceforge issue 1019628). Not totally convinced. What if the module doesn't have a patchline ?
      // todo there is something to this logic.

      if (module.hasPatchLine()) {
        return module.getVersion().createPatch(Patch.INITIAL_PATCH);
      }

      return null;
    }

    Version nextVersion = null;

    try {
      nextVersion = (Version) ((Version) matchingList.get(matchingList.size() - 1)).clone();
    } catch (CloneNotSupportedException e) {
      throw new KarmaRuntimeException(e.getMessage(), e);
    }

    nextVersion.increase();

    return nextVersion;
  }

  public Version getNextMajorVersion() {
    if (matchingList.size() == 0) {
      return null;
    }

    Version nextVersion = null;

    try {
      nextVersion = (Version) ((Version) matchingList.get(matchingList.size() - 1)).clone();
    } catch (CloneNotSupportedException e) {
      throw new KarmaRuntimeException(e.getMessage(), e);
    }

    nextVersion.increaseMajor();

    return nextVersion;
  }

  /**
   * The latest promoted version of the module in the version control system for the branch.
   *
   * @return
   */
  public Version getLastVersion() {

    if (matchingList.size() == 0) {

      if (module.hasPatchLine()) {
        return module.getVersion();
      } else {
        return null;
      }
    }
    return (Version) matchingList.get(matchingList.size() - 1);
  }

  /**
   * Gets the local version of the module, which is determined by parsing the <code>CVS/Entries</code> file.
   *
   * @return The version of the local checkout or <code>null</code> when the HEAD of the developmentline is local.
   * @throws CVSException
   */
  public Version getLocalVersion() throws CVSException {
    logger.debug("Retrieving local version for module: "+module.getName());
    StandardAdminHandler handler = new StandardAdminHandler();
    Version localVersion = null;

    try {
      Entry[] entries = handler.getEntriesAsArray(module.getBaseDir());

      Entry moduleDescriptor = null;

      int i = 0;
      while (i < entries.length) {
        if (entries[i].getName().equals(Module.MODULE_DESCRIPTOR)) {
          moduleDescriptor = entries[i];
          break;
        }
        i++;
      }
      try {

        if (moduleDescriptor == null || moduleDescriptor.getTag() == null || moduleDescriptor.getTag().matches(DevelopmentLine.DEVELOPMENT_LINE_PATTERN_STRING)) {
          logger.debug("We're on the head. Return null.");
          // We have the HEAD of a DevelopmentLine.
          //
          return null;
        }
        if (moduleDescriptor.getTag().startsWith(PatchLine.NAME_PREFIX)) {
          localVersion = new Patch(moduleDescriptor.getTag().substring(moduleDescriptor.getTag().indexOf("_") + 1));
          logger.debug("PatchLine. Return "+localVersion);
        } else {
          localVersion = new Version(moduleDescriptor.getTag().substring(moduleDescriptor.getTag().indexOf("_") + 1));
          logger.debug("MainLine. Return "+localVersion);
        }

      } catch (Exception e) {
        throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
      }

    } catch (IOException e) {
      throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
    }
    return localVersion;
  }

  public void setExistsInRepository(boolean exists) {
    existsInRepository = exists;
  }

  public boolean existsInRepository() {
    return existsInRepository;
  }

  private List collectVersions(Module module) {

    if (logInfo == null) {
      return new ArrayList();
    }

    // Step 1 : get all symbolicnames that apply to the correct pattern
    //
    //
    List matchingList = new ArrayList();

    Collection currentVersions = logInfo.getAllSymbolicNames();

    Pattern pattern = null;

    if (module.hasPatchLine()) {

      // We are working on the PatchLine of a module.
      //
      pattern = Pattern.compile(((PatchLine) module.getPatchLine()).getMatchingPattern());
    } else {
      // We are doing MAINLINE development.
      //
      pattern = Pattern.compile(MainLine.NAME_PREFIX.concat("_").concat(Version.VERSION_PATTERN_STRING));
    }

    // Collect all applicable symbolic names.
    //
    for (Iterator it = currentVersions.iterator(); it.hasNext();) {

      String s = ((LogInformation.SymName) it.next()).getName();

      Matcher matcher = pattern.matcher(s);
      if (matcher.matches()) {

        try {
          if (module.hasPatchLine()) {
            matchingList.add(new Patch(s.substring(s.lastIndexOf("_") + 1)));
          } else {
            matchingList.add(new Version(s.substring(s.lastIndexOf("_") + 1)));
          }
        } catch (PatternSyntaxException p) {
          // Ignore, just no match ...
        }
      }
    }

    // Step 2 : Sort them, so the last one is on top.
    //
    Collections.sort(matchingList);

    return matchingList;
  }

  public boolean connectionFailure() {
    return connectionFailure;
  }

  public boolean authenticationFailure() {
    return authenticationFailure;
  }

  public boolean internalError() {
    return internalError;
  }

  /**
   * Sets the indication that a connection failure to the remote CVS repository has occurred.
   */
  public void setConnnectionFailure() {
    connectionFailure = true;
  }

  public void setAuthenticationFailure() {
    authenticationFailure = true;
  }

  public void setInternalError() {
    internalError = true;
  }
}
