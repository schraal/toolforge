package nl.toolforge.karma.core.manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestParser {

  private static Log logger = LogFactory.getLog(ManifestParser.class);

  private static Collection list = null;

  private ClassLoader loader = null;

  private String name = null;

  public ManifestParser() {}

  public ManifestParser(ClassLoader loader) {
    this();
    this.loader = loader;
  }

  private ClassLoader getClassLoader() {
    return loader;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void includeManifest() throws ManifestException {

    Digester digester = new Digester();

    digester.addObjectCreate("manifest", "nl.toolforge.karma.core.manifest.Manifest");
    digester.addSetProperties("manifest", new String[] {"name", "version"}, new String[] {"name", "version"});

    digester.addCallMethod("manifest", "setDescription", 1); // <description>
    digester.addCallParam("manifest/description", 0);

    // Object create rule for <sourcemodule>
    //
    digester.addFactoryCreate("manifest/modules/module", "nl.toolforge.karma.core.manifest.ModuleDescriptorCreationFactory");

    digester.addSetProperties("manifest/modules/module", "version", "version");
    digester.addSetProperties("manifest/modules/module", "development-line", "developmentLine");

    // Each module-descriptor is added to the manifest.
    //
    digester.addSetNext("manifest/modules/module", "addDescriptor", "nl.toolforge.karma.core.manifest.ModuleDescriptor");

    digester.addObjectCreate("manifest/include-manifest", "nl.toolforge.karma.core.manifest.ManifestParser");
    digester.addSetProperties("manifest/include-manifest", "name", "name");
    digester.addSetNext("manifest/include-manifest", "includeManifest", "nl.toolforge.karma.core.manifest.ManifestParser");

    try {
      list.add((Manifest) digester.parse(getManifestFileAsStream(getName())));
    } catch (IOException e) {
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR);
    } catch (SAXException e) {
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR);
    }
  }

  public Collection getFullManifest() {
    return list;
  }

  /**
   * Wordt aangeroepen als een nieuw manifest moet worden geladen ...
   *
   * @param rootManifest
   */
  public void compileManifests(Manifest rootManifest) {
    list = new ArrayList();

    list.add(rootManifest);
  }

  /*
  * Retrieves the physical manifest file from disk or from the classpath.
  */
  public InputStream getManifestFileAsStream(String id) throws ManifestException {

    String pathToFile = "";

    try {
      String fileName = (id.endsWith(".xml") ? id : id.concat(".xml"));

      if (fileName.endsWith(File.separator)) {
        fileName = fileName.substring(0, fileName.length() - 1);
      }
      if (fileName.lastIndexOf(File.separator) > 0) {
        pathToFile = fileName.substring(0, fileName.lastIndexOf(File.separator));
      }
      fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

      if (getClassLoader() == null) {
        logger.debug("Loading manifest " + fileName + " from file-system ...");
        return new FileInputStream(new File("/tmp/").getPath() + File.separator + fileName);
      } else {
        logger.debug("Loading manifest " + fileName + " from classpath ...");

        InputStream inputStream = getClassLoader().getResourceAsStream(pathToFile + File.separator + fileName);

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
