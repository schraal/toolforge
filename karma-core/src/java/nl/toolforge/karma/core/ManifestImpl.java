package nl.toolforge.karma.core;

import nl.toolforge.karma.core.prefs.Preferences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

/**
 * Implementation for a manifest.
 *
 * @see nl.toolforge.karma.core.Manifest
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ManifestImpl implements Manifest {

	private static Log logger = LogFactory.getLog(ManifestImpl.class);

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
  public ManifestImpl(String manifestName) {

    if ((manifestName == null) || (manifestName.length() == 0)) {
      // Most probably a programmers' mistake, so a runtime exception
      //
      throw new KarmaRuntimeException("Manifest name should not be null or an empty string.");
    }
    this.manifestName = manifestName;
    this.modules = new ModuleMap();
  }

  public final ModuleMap getModules() {
    return modules;
  }

  /**
   * Adds a <code>Module</code> instance to this manifest. Not the same as {@link #createModule} !
   */
  public final void addModule(Module module) {
    modules.add(module);
  }

  public Module getModule(String moduleName) throws ManifestException {

    if (modules.containsKey(moduleName)) {
      return (Module) modules.get(moduleName);
    } else {
      throw new ManifestException(ManifestException.NO_SUCH_MODULE, new Object[]{moduleName});
    }
  }

  public final Module createModule(String moduleName, String locationAlias) throws KarmaException {
    return createModule(Module.SOURCE_MODULE, moduleName, locationAlias, false);
  }

	public final Module createModule(String moduleName, String locationAlias, boolean include) throws KarmaException {
    return createModule(Module.SOURCE_MODULE, moduleName, locationAlias, include);
  }

  public final Module createModule(int typeIdentifier, String moduleName, String locationAlias) throws KarmaException {
    return createModule(typeIdentifier, moduleName, locationAlias, false);
  }

  public final synchronized Module createModule(int typeIdentifier, String moduleName, String locationAlias, boolean include) throws KarmaException {

		Module module = ModuleFactory.getInstance().createModule(typeIdentifier, moduleName, locationAlias);

    addModule(module);

    if (include) {

      try {
        flush(module);
      } catch (IOException i) {
        throw new KarmaException(KarmaException.MANIFEST_FLUSH_ERROR, i);
      }
			logger.info("Module " + module.getName() + " has been added to manifest " + getName());
    }

    return module;
  }

  public final File getPath() {

    return new File("");
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

  private void flush(Module module) throws IOException {

    if (module instanceof SourceModule) {
			Element moduleElement = root.createElement(SourceModule.ELEMENT_NAME);
			moduleElement.setAttribute(SourceModule.NAME_ATTRIBUTE, module.getName());
			moduleElement.setAttribute(SourceModule.LOCATION_ATTRIBUTE, module.getLocation().getId());
		}

		// Serialize DOM
		//
    throw new KarmaRuntimeException("Has to be implemented");
  }

  public boolean isLocal(Module module) {

    // TODO throw an exception if this manifest doesn't have this module in it.
    //
    try {
      File manifestDirectory = new File(Preferences.getInstance().getDevelopmentHome(), getName());
      File moduleDirectory = new File(manifestDirectory, module.getName());

      return moduleDirectory.exists();

    } catch (KarmaException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    return false;
  }

  public File getLocalPath() throws KarmaException {
    return new File(Preferences.getInstance().getDevelopmentHome(), getName());
  }
}
