package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.LocalEnvironment;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

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

  private static Log logger = LogFactory.getLog(Manifest.class);
  private ModuleFactory moduleFactory = ModuleFactory.getInstance();

  private static ClassLoader loader = null; // Should be static, to support manifest includes

  private LocalEnvironment env = null; // The current active environment for the user.

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
    this.loader = loader;
  }

  public ClassLoader getClassLoader() {
    return loader;
  }

  public LocalEnvironment getLocalEnvironment() {
    return env;
  }

  public void setLocalEnvironment(LocalEnvironment env) {
    this.env = env;
  }

  /**
   * Loads the manifest from disk. It uses a
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> to parse the manifest XML.
   *
   * @throws LocationException
   * @throws ManifestException When loading the manifest failed.
   */
  public void load(LocalEnvironment env) throws LocationException, ManifestException {

    if (getClassLoader() == null && env == null) {
      throw new NullPointerException(
          "WARNING, you're probably running a unit test. Use 'load(ClassLoader)'.");
    }
    setLocalEnvironment(env);

    Digester digester = new Digester();

    URL rules = this.getClass().getClassLoader().getResource("manifest-rules.xml");
    digester = DigesterLoader.createDigester(rules);

    Manifest manifest = null;
    try {
      manifest = (Manifest) digester.parse(getManifestFileAsStream(getName()));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND);
      }
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException, that one should be propagated
        //
        throw new ManifestException(((ManifestException) e.getException()).getErrorCode());
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR);
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

    Digester digester = new Digester();

    URL rules = this.getClass().getClassLoader().getResource("manifest-rules.xml");
    digester = DigesterLoader.createDigester(rules);

    Manifest manifest = null;
    try {
      manifest = (Manifest) digester.parse(getManifestFileAsStream(child.getName()));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND);
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR);
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException
        //
        throw new ManifestException(((ManifestException) e.getException()).getErrorCode());
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR);
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

    Module module = moduleFactory.create(descriptor);

    if (getModulesForManifest().containsKey(module.getName())) {
      throw new ManifestException(ManifestException.DUPLICATE_MODULE);
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

//    File moduleDirectory = null;
//    try {
//      moduleDirectory = new File(new File(env.getDevelopmentHome(), getName()), module.getName());
//    } catch (KarmaException e) {
//      return false;
//    }
//    return moduleDirectory.exists();
    return false;
  }

  public boolean isLocal() {

//    for (Iterator i = modules.values().iterator(); i.hasNext();) {
//
//      Module m = (Module) i.next();
//
//      if (!m.getModuleDirectory().exists()) {
//        return false;
//      }
//    }

    return true;
  }

  public File getDirectory() {

    File file = null;
    try {
//      file = new File(env.getDevelopmentHome(), getName());
    } catch (Exception e) {
//      throw new ManifestException(ManifestException.INVALID_LOCAL_PATH, new Object[]{getName()});
    }

    return file;
  }


  // todo uitwerken !!!
  public String getDependencies(Module module) {

    // 1. Get the correct module type. If type == maven, get project.xml, else get module.xml.
    //
    StringBuffer buf = new StringBuffer();
//
//    DependencyReader reader = new MavenDependencyReader();
//    dependencies = reader.parse(new File(getModuleDirectory(), "project.xml"));
//
//    for (Iterator i = dependencies.iterator(); i.hasNext();) {
//      Dependency dep = (Dependency) i.next();
//      String jarDir =
//        MavenEnvironment.getMavenRepository() + File.separator +
//        dep.getArtifactDirectory() + File.separator +
//        "jars" + File.separator +
//        dep.getArtifactId();
//      if (dep.getVersion() != null) {
//        jarDir += "-" + dep.getVersion();
//      }
//      jarDir += ".jar";
//
//      buf.append(jarDir);
//      if (i.hasNext()) {
//        // Separator char for classpath parts
//        //
//        buf.append(":");
//      }
//    }

    return buf.toString();
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
   * Saves the manifest to disk, including all its included manifests.
   */
  public void save() throws ManifestException {
    // todo this requires a manifest to maintain a map of which module belongs to which manifest ...
  }

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
//      if (fileName.lastIndexOf(File.separator) > 0) {
//        path = fileName.substring(0, fileName.lastIndexOf(File.separator));
//      }
      fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

      if (getClassLoader() == null) {
        logger.debug("Loading manifest " + fileName + " from file-system ...");
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
    }
  }
}