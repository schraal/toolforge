package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.VersionExtractor;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.model.MainLine;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.admin.Entry;

import java.io.File;
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
public final class CVSVersionExtractor implements VersionExtractor {

  private static CVSVersionExtractor instance = null;

  public static CVSVersionExtractor getInstance() {

    if (instance == null) {
      return new CVSVersionExtractor();
    }
    return instance;
  }

  private CVSVersionExtractor() {
  }

  /**
   * <p>See {@link VersionExtractor#getNextVersion}.
   * <p/>
   * <p>Connects to the correct CVS repository and determines the next version in the branch (if applicable, otherwise
   * it is the trunk) for the module. This is done by quering <code>module.info</code>.
   *
   * @param module The next version number <code>module</code>.
   * @return The next version for <code>module</code>.
   * @throws CVSException When the version is not found for the module in the repository.
   */
  private static synchronized Version getNext(SourceModule module) throws CVSException {

    List matchingList = collectVersions(module);

    if (matchingList.size() == 0) {
      return Version.INITIAL_VERSION;
//      throw new CVSException(CVSException.VERSION_NOT_FOUND, new Object[] { module.getVersionAsString(), module.getName() });
    }

    Version lastMatch = (Version) matchingList.get(matchingList.size() - 1);
    lastMatch.setDigit(lastMatch.getLastDigitIndex(), lastMatch.getLastDigit() + 1);

    return lastMatch;
  }

  public Version getNextVersion(SourceModule module) throws CVSException {
    return getNext(module);
  }

  /**
   * <p>Connects to the correct CVS repository and determines the last version in the branch (if applicable, otherwise
   * it is the trunk) for the module. This is done by quering <code>module.info</code>.
   *
   * @param module The next version number <code>module</code>.
   * @return The next version for <code>module</code> or <code>null</code> when none is found.
   * @throws VersionControlException TODO complete when implementation is ready.
   */
  private static synchronized Version getLast(Module module) throws VersionControlException {

    List matchingList = collectVersions(module);

    if (matchingList.size() == 0) {
      // todo replace by CVSRuntimeException with ErrorCode instance.
      //
      throw new KarmaRuntimeException(
          "Module " + module.getName() +
          " is invalid in repository " + module.getLocation().getId() +
          "; no version info available.");
    }

    Version lastMatch = (Version) matchingList.get(matchingList.size() - 1);
    lastMatch.setDigit(lastMatch.getLastDigitIndex(), lastMatch.getLastDigit());

    return lastMatch;
  }

  public Version getLastVersion(Module module) throws VersionControlException {
    return getLast(module);
  }

  /**
   * Checks the modules' local version. This is the version
   *
   * @return Could be <code>null</code> if there is no local version data found.
   */
  public Version getLocalVersion(Manifest manifest, Module module) throws VersionControlException {

    StandardAdminHandler handler = new StandardAdminHandler();

    try {
      Entry[] entries = handler.getEntriesAsArray(new File(manifest.getDirectory(), module.getName()));

      Entry moduleInfo = null;
      for (int i = 0; i < entries.length; i++) {

        if (entries[i].getName().equals(SourceModule.MODULE_INFO)) {
          moduleInfo = entries[i];
        }
      }
      try {
        if (moduleInfo == null || moduleInfo.getTag() == null || moduleInfo.getTag().matches(DevelopmentLine.DEVELOPMENT_LINE_PATTERN_STRING)) {
          // We have the HEAD of a DevelopmentLine.
          //
          return null;
        }
        return new Version(moduleInfo.getTag().substring(moduleInfo.getTag().indexOf("_") + 1));
      } catch (Exception e) {
        throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
      }

    } catch (IOException e) {
      throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
    } catch (ManifestException e) {
      throw new CVSException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  /**
   * If the local checkout has the correct tag (Module.getPatchLine()), this method returns <code>true</code>.
   * 
   * @param manifest
   * @param module
   * @return
   * @throws VersionControlException
   */
  public boolean isOnPatchLine(Manifest manifest, Module module) throws VersionControlException {

    StandardAdminHandler handler = new StandardAdminHandler();

    try {
      Entry[] entries = handler.getEntriesAsArray(new File(manifest.getDirectory(), module.getName()));

      Entry moduleInfo = null;
      for (int i = 0; i < entries.length; i++) {

        if (entries[i].getName().equals(SourceModule.MODULE_INFO)) {
          moduleInfo = entries[i];
        }
      }
      try {
        if (moduleInfo == null || moduleInfo.getTag() == null) {
          return false;
        }
        return moduleInfo.getTag().matches(((SourceModule) module).getPatchLine().getName());
      } catch (Exception e) {
        throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
      }

    } catch (IOException e) {
      throw new CVSException(CVSException.LOCAL_MODULE_ERROR, new Object[]{module.getName()});
    } catch (ManifestException e) {
      throw new CVSException(e.getErrorCode(), e.getMessageArguments());
    }
  }


  private static List collectVersions(Module module) throws CVSException {

    // todo : this method should probably check if the module is at all compliant with the standards set by Karma.
    // for instance, are the basic version info and branch available ???
    //

    // Step 1 : get all symbolicnames that apply to the correct pattern
    //
    //
    List matchingList = new ArrayList();

    CVSRunner runner = null;

    runner = new CVSRunner(module.getLocation(), new File("")); // baseLocation doesn't matter ...

    LogInformation logInformation = runner.log(module);
    Collection currentVersions = logInformation.getAllSymbolicNames();


    Pattern pattern = null;
    if (((SourceModule) module).hasPatchLine()) {
      // We are working on a branch
      //
      String branchName = ((SourceModule) module).getPatchLine().getName();

      pattern = Pattern.compile(branchName.concat("_").concat(Version.VERSION_PATTERN_STRING));
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
          matchingList.add(new Version(s.substring(s.lastIndexOf("_") + 1)));
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

}
