package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import nl.toolforge.karma.core.vc.VersionControlException;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A <code>ReleaseManifest</code> is created when the Release Manager collects all stable versions of modules.
 * Effectively, the latest promoted version of all modules in.
 *
 *
 * @author W.H. Schraal, D.A. Smedes
 * @version $Id$
 */
public final class ReleaseManifest extends AbstractManifest {

  public ReleaseManifest(String name) {
    super(name);
  }

  public String getType() {
    return Manifest.RELEASE_MANIFEST;
  }

  /**
   * Adds a module to a <code>ReleaseManifest</code>. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> during the
   * {@link #load(nl.toolforge.karma.core.LocalEnvironment)}-process.
   *
   * @param descriptor The object representing a &lt;module&gt;-elemeent from a manifest XML file.
   * @throws nl.toolforge.karma.core.location.LocationException When an invalid location was passed with <code>descriptor</code>. This occurs when no
   *   location-id has been identified in the <code>locations.xml</code>-file in the manifest-store.
   * @throws ManifestException
   */
  public final void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException {

    // todo duidelijk beschrijven hoe het state mechanisme wordt aangestuurd door dit ding.
    //

    Module module = moduleFactory.create(descriptor);

    if (((SourceModule)module).hasVersion()) {
      module.setState(Module.STATIC);
    } else {

      // if (!isLocal(module)


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
      return Module.STATIC;
    } else {
      try {
        if (CVSVersionExtractor.getInstance().isOnPatchLine(this, module)) {
          return Module.WORKING;
        }
      } catch (VersionControlException v) {
        // todo should we throw this exception ????
        return Module.STATIC;
      }

      return Module.STATIC;
    }
  }




}
