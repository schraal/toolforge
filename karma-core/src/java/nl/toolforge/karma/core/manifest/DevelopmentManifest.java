package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.LocationException;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class DevelopmentManifest extends AbstractManifest {

  public DevelopmentManifest(String name) {
    super(name);
  }

  public String getType() {
    return Manifest.DEVELOPMENT_MANIFEST;
  }


  /**
   * <p>Adds a module to the <code>DevelopmentManifest</code>. The module is matched with This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> during the
   * {@link #load(nl.toolforge.karma.core.LocalEnvironment)}-process.
   *
   * <p>This method also checks if the module is available locally. If so, the module will be matched with the module
   * on disk to check if they are equal. This is to ensure that a changed manifest-definition is reflected on disk. If
   * the manifest shows another module (which is in fact determined by its location), the version on disk will be
   * removed.
   *
   * @param descriptor The object representing a &lt;module&gt;-elemeent from a manifest XML file.
   * @throws nl.toolforge.karma.core.location.LocationException When an invalid location was passed with <code>descriptor</code>. This occurs when no
   *   location-id has been identified in the <code>locations.xml</code>-file in the manifest-store.
   * @throws ManifestException
   */
  public synchronized final void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException {

//    throw new KarmaRuntimeException("Must check and warn for 'old' modules not in manifest, but still on local disk.");

    // todo duidelijk beschrijven hoe het state mechanisme wordt aangestuurd door dit ding.
    //

    Module module = moduleFactory.create(descriptor);

    if (((SourceModule)module).hasVersion()) {
      module.setState(Module.STATIC);
    } else {
      if (!isLocal(module)) {
        module.setState(Module.DYNAMIC);
      } else {
        module.setState(getLocalState(module));
      }
    }

    try {
      if (getLocalEnvironment() != null) {
        if (module instanceof SourceModule) {
          File manifestDirectory = new File(getLocalEnvironment().getDevelopmentHome(), getName());
          ((SourceModule) module).setBaseDir(new File(manifestDirectory, module.getName()));
        }
      }
    } catch(Exception e) {
      // Basically, if we can't do this, we have nothing ... really a RuntimeException
      //
      throw new KarmaRuntimeException("Could not set base directory for module " + module.getName());
    }

    if (getModulesForManifest().containsKey(module.getName())) {
      throw new ManifestException(ManifestException.DUPLICATE_MODULE, new Object[]{module.getName(), getName()});
    }
    getModulesForManifest().put(module.getName(), module);

    // As a last check, determine if an 'old' local version is available.
    //
    removeLocal((SourceModule) module);
  }

  /**
   * A <code>Module</code> can be in different states as defined in {@link Module}. This methods sets
   * the state of the module in its current context of the manifest.
   *
   * @param module
   * @param state The (new) state of the module.
   */
  public final synchronized void setState(Module module, Module.State state) throws ManifestException {

    if (module == null || state == null) {
      throw new IllegalArgumentException("Parameters module and or state cannot be null.");
    }

    if (!getAllModules().keySet().contains(module.getName())) {
      throw new ManifestException(ManifestException.MODULE_NOT_FOUND, new Object[] { module.getName() });
    }

    // The following blocks form a 'transaction'.
    //
    try {

      // Remove old state files ...
      //

      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          if ((name != null) && ((".WORKING".equals(name)) || (".STATIC".equals(name)) || (".DYNAMIC".equals(name)))) {
            return true;
          } else {
            return false;
          }
        }
      };

      String[] stateFiles = new File(getDirectory(), module.getName()).list(filter);

      if (stateFiles != null) {
        for (int i = 0; i < stateFiles.length; i++) {
          new File(new File(getDirectory(), module.getName()), stateFiles[i]).delete();
        }
      }

      File stateFile = new File(new File(getDirectory(), module.getName()), state.getHiddenFileName());
      stateFile.createNewFile();

    } catch (Exception e) {
      throw new ManifestException(ManifestException.STATE_UPDATE_FAILURE, new Object[] { module.getName(), state.toString()});
    }

    // If we were able to create that hidden file, the we'll update the modules' state.
    //
    module.setState(state);
  }

  public final Module.State getLocalState(Module module) {

    if (!isLocal(module)) {
      if (((SourceModule) module).hasVersion()) {
        return Module.STATIC;
      } else {
        return Module.DYNAMIC;
      }
    } else {

      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          if ((name != null) && name.matches(".WORKING|.STATIC|.DYNAMIC")) {
            return true;
          } else {
            return false;
          }
        }
      };

      String[] stateFiles = null;
      try {
        stateFiles = new File(getDirectory(), module.getName()).list(filter);
      } catch (ManifestException e) {
        throw new KarmaRuntimeException(e.getErrorMessage());
      }

      if (stateFiles == null || stateFiles.length == 0 ) {
        if (((SourceModule) module).hasVersion()) {
          return Module.STATIC;
        } else {
          return Module.DYNAMIC;
        }
      }

      if (stateFiles.length > 0 && ".WORKING".equals(stateFiles[0])) {
        return Module.WORKING;
      }
      return Module.DYNAMIC;
    }
  }

}
