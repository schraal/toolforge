package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.cvs.CVSException;
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import org.netbeans.lib.cvsclient.CVSRoot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
public abstract class AbstractManifest implements Manifest {

  private static Log logger = LogFactory.getLog(AbstractManifest.class);
  protected ModuleFactory moduleFactory = ModuleFactory.getInstance();

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
  public AbstractManifest(String name) {

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
  public final String getName() {
    return name;
  }

  public final void setType(String type) {
    // Just here for compatibility.
  }

  public abstract String getType();

  /**
   * Gets a manifests' version (the &lt;version&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' version.
   */
  public final String getVersion() {
    return version;
  }

  /**
   * Sets the manifests' version. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> while parsing the manifest XML file.
   *
   * @param version The manifests' version (&lt;version&gt;-attribute); may be <code>null</code>.
   */
  public final void setVersion(String version) {
    this.version = version;
  }

  public final String getDescription() {
    return description;
  }

  public final void setDescription(String description) {
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
  public final void load(ClassLoader loader) throws LocationException, ManifestException {

    setClassLoader(loader);
    load(env);
  }

  private void setClassLoader(ClassLoader loader) {
    AbstractManifest.loader = loader;
  }

  public final ClassLoader getClassLoader() {
    return loader;
  }

  public final LocalEnvironment getLocalEnvironment() {
    return env;
  }

  public void setLocalEnvironment(LocalEnvironment env) {
    AbstractManifest.env = env;
  }

  /**
   * Loads the manifest from disk. It uses a
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> to parse the manifest XML.
   *
   * @throws ManifestException When loading the manifest failed.
   */
  public final void load(LocalEnvironment env) throws ManifestException {

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
//      e.printStackTrace();
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException, that one should be propagated
        //
        ManifestException m = (ManifestException) e.getException();
        throw new ManifestException(m.getErrorCode(), m.getMessageArguments());
      } else if (e.getException() instanceof LocationException) {
        LocationException m = (LocationException) e.getException();
        throw new ManifestException(m.getErrorCode(), m.getMessageArguments());
      } else {
        throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{this.getName()});
      }
    }

    copyToThis((AbstractManifest) manifest);

    // As a final step, check all modules in the manifest and remove modules that are not in the manifest anymore
    // but still on disk.

    // todo should notify the user (responselistener)
    //

    // todo should move all modules to a 'modules' sub-directory, for better scanning purposes.
//    Collection modules = getAllModules().values();
//    for (Iterator i = modules.iterator(); i.hasNext();) {
//      Module module = (Module) i.next();
//
//    }
  }

  /**
   * Makes a 'deep' copy of <code>manifest</code> into <code>this</code>. <code>manifest</code> was generated by
   * Digester, but they have to be copied to <code>this</code>, because that's the <code>AbstractManifest</code> instance that
   * is returned by {@link #load(LocalEnvironment)}.
   */
  private void copyToThis(AbstractManifest manifest) {

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
   * Includes another manifest in the manifest and links them as 'parent-child'. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a>
   *
   * @param child
   * @throws ManifestException
   */
  public final void includeManifest(ManifestDescriptor child) throws ManifestException {

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
//      e.printStackTrace();
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException, that one should be propagated
        //
        ManifestException m = (ManifestException) e.getException();
        throw new ManifestException(m.getErrorCode(), m.getMessageArguments());
      } else if (e.getException() instanceof LocationException) {
        LocationException m = (LocationException) e.getException();
        throw new ManifestException(m.getErrorCode(), m.getMessageArguments());
      } else {
        throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{getName()});
      }
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
  public abstract void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException;

  /**
   * Gets all modules defined in this manifest (excluding includedManifests).
   *
   * @see #getAllModules()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public final Map getModulesForManifest() {
    return modules;
  }

  /**
   * Gets all modules defined in this manifest including all modules for all child manifests.
   *
   * @see #getModulesForManifest()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public final Map getAllModules() {

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
  public final int size() {

    int total = getModulesForManifest().size();

    for (Iterator i = childManifests.iterator(); i.hasNext();) {
      total += ((AbstractManifest) i.next()).size();
    }

    return total;
  }

  public final Module getModule(String moduleName) throws ManifestException {

    if (getModulesForManifest().containsKey(moduleName)) {
      return (Module) getModulesForManifest().get(moduleName);
    } else {
      throw new ManifestException(ManifestException.MODULE_NOT_FOUND, new Object[]{moduleName});
    }
  }

  public final boolean isLocal(Module module) {

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

  public final boolean isLocal() {

    for (Iterator i = getAllModules().values().iterator(); i.hasNext();) {

      Module m = (Module) i.next();

      // If we stumble upon a non local module, return false
      if (!this.isLocal(m)) {
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

  /**
   * Retrieves all included manifests.
   *
   * @return A <code>Collection</code> of <code>AbstractManifest</code> instances, or an empty collection if no included
   *   manifests are available.
   */
  public final Collection getIncludes() {
    return childManifests;
  }


  /**
   * A <code>Module</code> can be in different states as defined in {@link Module}. This methods sets
   * the state of the module in its current context of the manifest.
   *
   * @param module
   * @param state The (new) state of the module.
   */
  public abstract void setState(Module module, Module.State state) throws ManifestException;

  public abstract Module.State getLocalState(Module module);

  /**
   * Saves the manifest to disk, including all its included manifests.
   */
  public void save() throws ManifestException {
    // todo this requires a manifest to maintain a map of which module belongs to which manifest ...
  }

  /**
   * A manifest is equal to another manifest if their names are equal.
   *
   * @param o A <code>AbstractManifest</code> instance.
   */
  public final boolean equals(Object o) {

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

  /**
   * <p>Determines the correct artifact name for <code>module</code>. The artifact-name is determined as follows:
   *
   * <ul>
   *   <li/>If the state of the module is <code>WORKING</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_WORKING.jar</code>.
   *   <li/>If the state of the module is <code>DYNAMIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_&lt;latest-versions&gt;.jar</code>.
   *   <li/>If the state of the module is <code>STATIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_&lt;version&gt;.jar</code>.
   * </ul>
   *
   * <p>The extension if <code>.war</code> if the module is a <code>webapp</code>-module.
   *
   * @param module A <code>SourceModule</code> instance.
   * @return The artifact-name as determined the way as described above.
   * @throws ManifestException
   */
  public final String resolveArchiveName(Module module) throws ManifestException {

    String jar = module.getName() + "_";

    // todo introduce a method to determine if a module is webapp-module; maybe its own class.
    //
    String extension;
    if (module.getName().startsWith(Module.WEBAPP_PREFIX)) {
      extension = ".war";
    } else if (module.getName().startsWith(Module.EAPP_PREFIX)) {
      extension = ".ear";
    } else {
      extension = ".jar";
    }

    try {
      if (((SourceModule) module).getState().equals(Module.WORKING)) {
        jar += Module.WORKING.toString();
      } else if (((SourceModule) module).getState().equals(Module.DYNAMIC)) {
        jar += (CVSVersionExtractor.getInstance().getLocalVersion(this, module));
      } else { // STATIC module
        jar += ((SourceModule) module).getVersionAsString();
      }
      jar += extension;
    } catch (VersionControlException v) {
      throw new ManifestException(v.getErrorCode(), v.getMessageArguments());
    }

    return jar;
  }

  /**
   *
   *
   * @param module
   * @return Interdependencies for <code>module</code> or an empty <code>Collection</code>.
   */
  public final Collection getModuleInterdependencies(Module module) throws ManifestException {

    Collection deps = (Collection) getInterdependencies().get(module.getName());

    return (deps == null ? new HashSet() : deps);
  }

  /**
   * <p>Calculates interdepencies between modules in the manifest; interdependencies are inverse relationships
   * between a module and other modules (being <code>SourceModule</code> instances).
   *
   * <p>If a module <code>B</code> has a dependency on module <code>A</code>, then this method will return a map, with
   * a key <code>A</code> and its value a <code>Collection</code> of interdependencies (in this case, <code>B</code>).
   *
   * @return
   */
  public final Map getInterdependencies() throws ManifestException {

    Map interDependencies = new Hashtable();

    // Interdependencies can only be determined if the module has been checked out locally ...
    //

    Map allModules = getAllModules();

    for (Iterator i = allModules.keySet().iterator(); i.hasNext();) {

      Module module = (Module) allModules.get((String) i.next());

      if (isLocal(module)) {

        // Does the module have 'module'-deps ?
        //

        Set moduleDependencies = null;
        moduleDependencies = ((SourceModule) module).getDependencies();

        // Iterate over all dependencies. If it is a module dep, check if we already have an
        // entry in the interdep-collection; create one when necessary.
        //
        for (Iterator j = moduleDependencies.iterator(); j.hasNext();) {
          ModuleDependency moduleDependency = (ModuleDependency) j.next();
          if (moduleDependency.isModuleDependency()) {

            // Check if a key for the module dep already exists.
            //
            if (interDependencies.containsKey(moduleDependency.getModule())) {

              // If so, get the corresponding collection and add the module to the collection.
              //
              Collection col = (Collection) interDependencies.get(moduleDependency.getModule());
              col.add(module);
            } else {
              // For the dependency, no entry exists, so we create one.
              //
              Collection col = new HashSet();
              col.add(module);
              // todo TEST (!) if the mechanism works for 'duplicate' keys.
              interDependencies.put(moduleDependency.getModule(), col);
            }
          }
        }
      } else {
        // todo else what ???
      }
    }

    return interDependencies;
  }

  /**
   * <p>Checks is <code>module</code> should be removed locally. This can - e.g. - happen if the module was checked out
   * from a location elsewhere and the module with the same name but with a different location has been defined in the
   * manifest, before the module was cleaned locally.
   *
   * <p>This method only supports CVS.
   *
   * @param module
   * @return <code>true</code> if a local version has been removed or <code>false</code> if nothing has been removed.
   */
  protected boolean removeLocal(SourceModule module) {

    // todo this method is not abstract ! handles CVS only.

    if (isLocal(module)) {

      File rootFile = new File(module.getBaseDir(), "CVS/Root");


      String cvsRootString = null;
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rootFile)));

        cvsRootString = in.readLine();
        in.close();

      } catch (FileNotFoundException e) {
        throw new KarmaRuntimeException("Panic ! CVS/Root file missing for module " + module.getName());
      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }

      CVSRoot cvsRoot = CVSRoot.parse(cvsRootString);

      Location loc = module.getLocation();
      try {
        if (loc instanceof CVSLocationImpl) {
          if (!cvsRoot.toString().equals(((CVSLocationImpl)loc).getCVSRootAsString())) {
            FileUtils.deleteDirectory(module.getBaseDir());
            logger.info("Mismatch between local module and definition in manifest solved (manifest:" + this.getName() + ", module:" + module.getName() + ")");
          }

          return true;
        }
      } catch (CVSException e) {
        e.printStackTrace();
        // todo excception handling
      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }

    }
    return false;
  }

}
