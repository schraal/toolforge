package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;

/**
 * <p>
 * </p>
 *
 * <p>Check the <a href="package-summary.html">package documentation</a> for more information on the concepts behind
 * Karma.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Manifest {

  public static final String HISTORY_KEY = "manifest.history.last";

  private static Log logger = LogFactory.getLog(Manifest.class);
  private ModuleFactory moduleFactory = ModuleFactory.getInstance();

  private static ClassLoader loader = null; // Should be static, to support manifest includes
  private static LocalEnvironment env = null; // The current active environment for the user.
  private static Collection duplicates = null; // To detect duplicate included manifests.

  private Collection childManifests = new ArrayList();

  private Map moduleCache = null; // See #getAllModules()

  private String name = null;
  private String version = null;
  private String description = null;

  private Map modules = null;

  /**
   * Constructs a manifest instance; <code>name</code> is mandatory.
   */
  public Manifest(String name) {

    if ("".equals(name) || name == null) {
      throw new IllegalArgumentException("Manifest name cannot be empty or null.");
    }
    this.name = name;

    modules = new Hashtable();
  }

  /**
   * Gets a manifests' name (the &lt;name&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets a manifests' version (the &lt;version&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' version.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the manifests' version. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> while parsing the manifest XML file.
   *
   * @param version The manifests' version (&lt;version&gt;-attribute); may be <code>null</code>.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Loads a manifest using the <code>loader</code> as the resource-pool for manifest files.
   *
   * @see #load(LocalEnvironment)
   *
   * @param loader The classloader to use to locate manifest files.
   *
   * @throws LocationException
   * @throws ManifestException When loading the manifest failed.
   */
  public void load(ClassLoader loader) throws LocationException, ManifestException {

    setClassLoader(loader);
    load(env);
  }

  private void setClassLoader(ClassLoader loader) {
    Manifest.loader = loader;
  }

  public ClassLoader getClassLoader() {
    return loader;
  }

  public LocalEnvironment getLocalEnvironment() {
    return env;
  }

  public void setLocalEnvironment(LocalEnvironment env) {
    Manifest.env = env;
  }

  /**
   * Loads the manifest from disk. It uses a
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> to parse the manifest XML.
   *
   * @throws LocationException
   * @throws ManifestException When loading the manifest failed.
   */
  public void load(LocalEnvironment env) throws LocationException, ManifestException {

    (duplicates = new ArrayList()).add(this.getName()); // Add the root manifest.

    if (getClassLoader() == null && env == null) {
      throw new NullPointerException(
          "WARNING, you're probably running a unit test. Use 'load(ClassLoader)'.");
    }
    setLocalEnvironment(env);

    URL rules = this.getClass().getClassLoader().getResource("manifest-rules.xml");
    Digester digester = DigesterLoader.createDigester(rules);

    Manifest manifest = null;
    try {
      manifest = (Manifest) digester.parse(getManifestFileAsStream(getName()));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{manifest.getName()});
      }
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException, that one should be propagated
        //
        throw new ManifestException(((ManifestException) e.getException()).getErrorCode());
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest.getName()});
    }

    copyToThis(manifest);
  }

  /**
   * Makes a 'deep' copy of <code>manifest</code> into <code>this</code>. <code>manifest</code> was generated by
   * Digester, but they have to be copied to <code>this</code>, because that's the <code>Manifest</code> instance that
   * is returned by {@link #load(LocalEnvironment)}.
   */
  private void copyToThis(Manifest manifest) throws LocationException, ManifestException {

   // Copy all loaded data into this instance
    //
    setDescription(manifest.getDescription());
    setVersion(manifest.getVersion());
    copyIncludes(manifest.getIncludes());
    copyModules(manifest.getModulesForManifest());

  }

  private void copyIncludes(Collection includedManifests) {
    for (Iterator i = includedManifests.iterator(); i.hasNext();) {
      childManifests.add((Manifest) i.next());
    }
  }

  private void copyModules(Map modules) {
    getModulesForManifest().putAll(modules);
  }

  /**
   * Includes another manifest in this manifest and links them as 'parent-child'. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a>
   *
   * @param child
   * @throws ManifestException
   */
  public void includeManifest(Manifest child) throws ManifestException {

    if (duplicates.contains(child.getName())) {
      throw new ManifestException(ManifestException.MANIFEST_NAME_RECURSION, new Object[]{child.getName()});
    }
    duplicates.add(child.getName());

    URL rules = this.getClass().getClassLoader().getResource("manifest-rules.xml");
    Digester digester = DigesterLoader.createDigester(rules);

    Manifest manifest = null;
    try {
      manifest = (Manifest) digester.parse(getManifestFileAsStream(child.getName()));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{manifest.getName()});
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest.getName()});
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException
        //
        throw new ManifestException(((ManifestException) e.getException()).getErrorCode());
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest.getName()});
    }

    link(manifest);
  }

  /**
   * Links the manifest to this manifest to retain the full tree view of included manifests for a manifest.
   *
   * @param manifest The manifest that should be linked to its parent.
   */
  private void link(Manifest manifest) {
    childManifests.add(manifest);
  }

  /**
   * Adds a module to this manifest. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> during the {@link #load(LocalEnvironment)}-process.
   *
   * @param descriptor The object representing a &lt;module&gt;-elemeent from a manifest XML file.
   * @throws LocationException When an invalid location was passed with <code>descriptor</code>. This occurs when no
   *   location-id has been identified in the <code>locations.xml</code>-file in the manifest-store.
   * @throws ManifestException
   */
  public void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException {

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
  }

  /**
   * Gets all modules defined in this manifest (excluding includedManifests).
   *
   * @see #getAllModules()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public Map getModulesForManifest() {
    return modules;
  }

  /**
   * Gets all modules defined in this manifest including all modules for all child manifests.
   *
   * @see #getModulesForManifest()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public Map getAllModules() {

    if (moduleCache == null) {
      moduleCache = new HashMap();

      for (Iterator i = childManifests.iterator(); i.hasNext();) {
        moduleCache.putAll(((Manifest) i.next()).getAllModules());
      }
      moduleCache.putAll(getModulesForManifest());
    }
    return moduleCache;
  }

  /**
   * Counts all modules for a manifest, also counting all modules of all included manifests. This method thus counts
   * all child manifests as well.
   *
   * @return The total number of modules in this manifest (inlcuding all included manifests).
   */
  public int size() {

    int total = getModulesForManifest().size();

    for (Iterator i = childManifests.iterator(); i.hasNext();) {
      total += ((Manifest) i.next()).size();
    }

    return total;
  }

  public Module getModule(String moduleName) throws ManifestException {

    if (getModulesForManifest().containsKey(moduleName)) {
      return (Module) getModulesForManifest().get(moduleName);
    } else {
      throw new ManifestException(ManifestException.MODULE_NOT_FOUND, new Object[]{moduleName});
    }
  }

  public boolean isLocal(Module module) {

    File moduleDirectory = null;
    try {
      if (getClassLoader() == null) {
        moduleDirectory = new File(new File(env.getDevelopmentHome(), getName()), module.getName());
      } else {
        return false;
      }
    } catch (KarmaException e) {
      return false;
    }
    return moduleDirectory.exists();
  }

  public boolean isLocal() {

    for (Iterator i = getAllModules().values().iterator(); i.hasNext();) {

      Module m = (Module) i.next();

      // If we stumble upon a non local module, return false
      if (!this.isLocal(m)) {
        return false;
      }
    }

    return true;
  }

  public File getDirectory() throws ManifestException {

    File file = null;
    try {
      file = new File(env.getDevelopmentHome(), getName());
    } catch (Exception e) {
      throw new ManifestException(ManifestException.INVALID_LOCAL_PATH, new Object[]{getName()});
    }

    return file;
  }

  /**
   * Retrieves all included manifests.
   *
   * @return A <code>Collection</code> of <code>Manifest</code> instances, or an empty collection if no included
   *   manifests are available.
   */
  public Collection getIncludes() {
    return childManifests;
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

  /**
   * Saves the manifest to disk, including all its included manifests.
   */
  public void save() throws ManifestException {
    // todo this requires a manifest to maintain a map of which module belongs to which manifest ...
  }

  /**
   * A manifest is equal to another manifest if their names are equal.
   *
   * @param o A <code>Manifest</code> instance.
   */
  public boolean equals(Object o) {

    if (o instanceof Manifest) {
      if (getName().equals(((Manifest) o).getName())) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Local helper method to get the manifest file from the correct resource path.
   */
  private InputStream getManifestFileAsStream(String id) throws ManifestException {

    try {
      String fileName = (id.endsWith(".xml") ? id : id.concat(".xml"));

      if (fileName.endsWith(File.separator)) {
        fileName = fileName.substring(0, fileName.length() - 1);
      }
      fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

      if (getClassLoader() == null) {
        logger.debug(
            "Loading manifest " + fileName + " from " + getLocalEnvironment().getManifestStore().getPath() + File.separator + fileName);
        FileInputStream fis = null;
        return new FileInputStream(getLocalEnvironment().getManifestStore().getPath() + File.separator + fileName);
      } else {
        logger.debug("Loading manifest " + fileName + " from classpath ...");

        InputStream inputStream = getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
          throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
        }
        return inputStream;
      }
    } catch (FileNotFoundException f) {
      throw new ManifestException(f, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    } catch (NullPointerException n) {
      throw new ManifestException(n, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    } catch (KarmaException e) {
      throw new ManifestException(e.getErrorCode(), e.getMessageArguments());
    }
  }
}
