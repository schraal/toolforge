package nl.toolforge.karma.core;

import nl.toolforge.karma.core.prefs.Preferences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

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

//      try {
			flush(module);
//      } catch (IOException i) {
//        throw new ManifestException(ManifestException.MANIFEST_FLUSH_ERROR, i);
//      }
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

	private void flush(Module module) throws ManifestException {

		if (module instanceof SourceModule) {
			Element moduleElement = root.createElement(SourceModule.ELEMENT_NAME);
			moduleElement.setAttribute(SourceModule.NAME_ATTRIBUTE, module.getName());
			moduleElement.setAttribute(SourceModule.LOCATION_ATTRIBUTE, module.getLocation().getId());
		}

//		catch (IOException i) {
//        throw new ManifestException(ManifestException.MANIFEST_FLUSH_ERROR, i);
//      }

		// Serialize DOM
		//
		throw new KarmaRuntimeException("Has to be implemented");
	}

	public boolean isLocal(Module module) {

		File moduleDirectory = new File(new File(env.getDevelopmentHome(), getName()), module.getName());
		return moduleDirectory.exists();
	}

	public File getLocalPath() throws ManifestException {

		File file = null;
		try {
			file = new File(env.getDevelopmentHome(), getName());
		} catch (Exception e) {
			throw new ManifestException(ManifestException.INVALID_LOCAL_PATH, new Object[]{getName()});
		}

		return file;
	}
}
