package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Iterator;

/**
 * Implementation for a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see nl.toolforge.karma.core.Manifest
 */
public class ManifestImpl implements Manifest {

  private static Log logger = LogFactory.getLog(ManifestImpl.class);

  private LocalEnvironment env = null;
  private String manifestName = null;

  private ModuleMap modules = null;

  // Contains this manifest as a DOM
  //
  private Document root = null;

  /**
   * Constructs a manifest instance with name <code>manifestName</code>.
   *
   * @param manifestName The name of the manifest, which is the <code>name</code>-attribute from the
   *                     <code>manifest</code>-element, when parsed from a manifest XML file.
   */
  public ManifestImpl(LocalEnvironment env, String manifestName) {

    if ((manifestName == null) || (manifestName.length() == 0)) {
      // Most probably a programmers' mistake, so a runtime exception
      //
      throw new KarmaRuntimeException("Manifest name should not be null or an empty string.");
    }
    this.env = env;
    this.manifestName = manifestName;
    this.modules = new ModuleMap();
  }

  public final ModuleMap getModules() {
    return modules;
  }

  /**
   * Adds a <code>Module</code> instance to this manifest.
   */
//	public final void addModule(Module module) {
//		modules.add(module);
//	}

  public Module getModule(String moduleName) throws ManifestException {

    if (modules.containsKey(moduleName)) {
      return (Module) modules.get(moduleName);
    } else {
      throw new ManifestException(ManifestException.NO_SUCH_MODULE, new Object[]{moduleName});
    }
  }

  public final void addModule(ModuleDescriptor descriptor) throws ManifestException {

    Module module = null;
    if (descriptor instanceof MavenModuleDescriptor) {
      module = new MavenModule((MavenModuleDescriptor) descriptor, this.getDirectory());
    } else if (descriptor instanceof SourceModuleDescriptor) {
      // Note the sequence in this if-loop.
      //
      module = new SourceModule((SourceModuleDescriptor) descriptor, this.getDirectory());
    } else if (descriptor instanceof JarModuleDescriptor) {
      module = new JarModule((JarModuleDescriptor) descriptor, this.getDirectory());
    }
    modules.add(module);
  }

  public int countSourceModules() {
    return getModules().getSourceModules().size();
  }

  public int countAllModules() {
    return getModules().size();
  }

  public int countJarModules() {
    return getModules().getJarModules().size();
  }

  public String getName() {
    return manifestName;
  }

  public boolean isLocal(Module module) {

    File moduleDirectory = null;
    try {
      moduleDirectory = new File(new File(env.getDevelopmentHome(), getName()), module.getName());
    } catch (KarmaException e) {
      return false;
    }
    return moduleDirectory.exists();
  }

  public boolean isLocal() {

    for (Iterator i = modules.values().iterator(); i.hasNext();) {

      Module m = (Module) i.next();
      
      if (!m.getModuleDirectory().exists()) {
        return false;
      }
    }

    return true;
  }

  public final File getDirectory() throws ManifestException {

    File file = null;
    try {
      file = new File(env.getDevelopmentHome(), getName());
    } catch (Exception e) {
      throw new ManifestException(ManifestException.INVALID_LOCAL_PATH, new Object[]{getName()});
    }

    return file;
  }
}
