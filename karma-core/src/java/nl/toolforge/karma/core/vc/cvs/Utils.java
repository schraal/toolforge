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
package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Patch;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.PatchLine;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.model.MainLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public final class Utils {

  private static final Log logger = LogFactory.getLog(Utils.class);

  /**
   * <p>Creates a symbolic name for <code>module</code>, based on <code>version</code> and whether a module has an
   * associated <code>DevelopmentLine</code>.</p>
   *
   * <p>Right now, symbolic name stuff is supported for <code>SourceModule</code>s, so if this method is supplied with
   * another <code>module</code> type, the method returns an empty <code>CVSTag</code>.
   *
   * @param module The module at hand.
   * @param developmentLine The development line to which the version applies.
   * @param version The version (within the modules' development line. <code>version</code> may be <code>null</code> in
   *   which case it is ignored when creating a <code>SymbolicName</code>.
   *
   * @return A symbolic name (tag) as they are used in a CVS repository. An empty CVSTag is returned when another
   *  instance than a {@link SourceModule} is passed as the <code>module</code> parameter.
   */
  public static SymbolicName createSymbolicName(Module module, DevelopmentLine developmentLine, Version version) {

//    if (module instanceof SourceModule) {

    if (version == null) {
      new CVSTag("");
      if (developmentLine != null) {
        return new CVSTag(developmentLine.getName());
      } else {
        // We are using the TRUNK.
        //
        new CVSTag("");
      }
    } else {
      if (developmentLine != null) {

        if (developmentLine instanceof PatchLine) {
          if (!(version instanceof Patch)) {
            throw new KarmaRuntimeException(
                "The provided developmentLine is a PatchLine instance. This implies " +
                "that the provided version should be a Patch instance.");
          }
          return createSymbolicName((Patch)version);
        } else {
          return new CVSTag(developmentLine.getName() + "_" + version.getVersionNumber());
        }
      } else if (version instanceof Patch) {

        // If the version argument is in fact a Patch instance, we have to create something like : PATCHLINE|p_0-0-0-
        //
        return new CVSTag(PatchLine.NAME_PREFIX + PatchLine.PATCH_SEPARATOR + version.getVersionNumber());

      } else {
        return new CVSTag(MainLine.NAME_PREFIX + "_" + version.getVersionNumber());
      }

    }
    return new CVSTag("");
  }

  public static SymbolicName createSymbolicName(Module module,  Version version) {
    return Utils.createSymbolicName(module, null, version);
  }

  public static SymbolicName createSymbolicName(Patch patch) {
    return new CVSTag(PatchLine.NAME_PREFIX + PatchLine.PATCH_SEPARATOR + patch.getVersionNumber());
  }

  public static Version getLastVersion(Module module) throws VersionControlException {

    logger.debug("Getting last version for module : " + module.getName());

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
    return status.getLastVersion();
  }

  public static Version getLocalVersion(Module module) throws VersionControlException {

    logger.debug("Getting local version for module : " + module.getName());

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
    return status.getLocalVersion();
  }

  public static boolean existsInRepository(Module module) throws VersionControlException {

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    //todo refactor this logic.
    //the idea now is that the retrieval of status would have given an exception when
    //the module was not in the repo.
    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));

    return true;
  }


  private static FilenameFilter filter = new FilenameFilter() {
    public boolean accept(File dir, String name) {
      return !name.matches(".cvsignore") && !name.matches("CVS");
    }
  };

  /**
   * Gets all files not known to CVS.
   *
   * @param module
   * @return
   */
  public static Set getAllEntries(Module module) throws IOException {

    //
    // todo UNFINISHED.
    //


    // Define a file filter for everything except:
    // 1. .cvsignore
    // 2. CVS-directories.
    //

    Set newEntries = new HashSet();

    if (module.getBaseDir().exists()) {
      String[] allEntries = (String[]) module.getBaseDir().list(filter);

      Set e = new HashSet();

      for (int i = 0; i < allEntries.length; i++) {

        File file = new File(module.getBaseDir(), allEntries[i]);
        if (file.isDirectory()) {
          addEntries(file, e);
        } else {
         e.add(file);
        }

      }

      StandardAdminHandler handler = new StandardAdminHandler();
      Set cvsFiles = handler.getAllFiles(module.getBaseDir());

      for (Iterator i = e.iterator(); i.hasNext();) {

        File f = (File) i.next();

        if (!cvsFiles.contains(f)) {
          newEntries.add(f);
        }
      }
    }
    return newEntries;
  }

  private static Set addEntries(File dir, Set currentSet) {

    String[] newEntries = (String[]) dir.list(filter);

    for (int i = 0; i < newEntries.length; i++) {

      File entry = new File(dir, newEntries[i]);
      if (entry.isDirectory()) {
        currentSet.addAll(addEntries(entry, currentSet));
      } else {
        currentSet.add(entry);
      }
    }
    return currentSet;
  }

}
