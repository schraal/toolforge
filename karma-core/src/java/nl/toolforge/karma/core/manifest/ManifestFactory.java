package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.digester.ManifestCreationFactory;
import nl.toolforge.karma.core.manifest.digester.ModuleDescriptorCreationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Factory class to create {@link Manifest} instances.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestFactory {

  private static Log logger = LogFactory.getLog(ManifestFactory.class);

  private static ManifestFactory instance = null;

  private ManifestFactory() {
  }

  /**
   * Get the factory instance class.
   *
   * @return The singleton factory class.
   */
  public static ManifestFactory getInstance() {

    if (instance == null) {
      instance = new ManifestFactory();
    }
    return instance;
  }

  /**
   * Creates a manifest by
   * @param manifestName
   * @return
   * @throws ManifestException
   */
  public Manifest createManifest(String manifestName) throws ManifestException {

    Digester digester = new Digester();

    // The <manifest>-element, to get to the correct manifest type.
    //
    digester.addFactoryCreate("manifest", ManifestCreationFactory.class);
    digester.addSetProperties("manifest");

    Manifest manifest = null;

    try {
      manifest = (Manifest) digester.parse(getManifestFileAsStream(manifestName));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{manifest.getName()});
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifest.getName()});
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException, that one should be propagated
        //
        ManifestException m = (ManifestException) e.getException();
        throw new ManifestException(m.getErrorCode(), m.getMessageArguments());
      } else if (e.getException() instanceof LocationException) {
        LocationException m = (LocationException) e.getException();
        throw new ManifestException(m, m.getErrorCode(), m.getMessageArguments());
      } else {
        throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{manifestName});
      }
    }

    manifest.load();

    return manifest;
  }

  /**
   * Parses a manifest file with name <code>&lt;name&gt;+.xml</code>.
   *
   * @param name
   * @return
   * @throws ManifestException
   */
  public Manifest parse(String name) throws ManifestException {

    try {
      return (Manifest) getDigester().parse(getManifestFileAsStream(name));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{name});
      }
      throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{name});
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
        throw new ManifestException(e, ManifestException.MANIFEST_LOAD_ERROR, new Object[]{name});
      }
    }
  }

  private Digester getDigester() {

    Digester digester = new Digester();

    // The <manifest>-element
    //
    digester.addFactoryCreate("manifest", ManifestCreationFactory.class);
    digester.addSetProperties("manifest");

    // All <module>-elements
    //
    digester.addFactoryCreate("*/module", ModuleDescriptorCreationFactory.class);
    digester.addSetProperties("*/module");
    digester.addSetNext("*/module", "addModule", "nl.toolforge.karma.core.manifest.ModuleDescriptor");

    // All <include-manifest>-elements
    //
    digester.addObjectCreate("*/include-manifest", "nl.toolforge.karma.core.manifest.ManifestDescriptor");
    digester.addSetProperties("*/include-manifest");
    digester.addSetNext("*/include-manifest", "includeManifest");

    return digester;
  }

  private InputStream getManifestFileAsStream(String id) throws ManifestException {

    try {
      String fileName = (id.endsWith(".xml") ? id : id.concat(".xml"));

      if (fileName.endsWith(File.separator)) {
        fileName = fileName.substring(0, fileName.length() - 1);
      }
      fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

      logger.debug("Loading manifest " + fileName + " from " + LocalEnvironment.getManifestStore().getPath() + File.separator + fileName);

      return new FileInputStream(LocalEnvironment.getManifestStore().getPath() + File.separator + fileName);

    } catch (FileNotFoundException f) {
      throw new ManifestException(f, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    } catch (NullPointerException n) {
      throw new ManifestException(n, ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[]{id});
    }
  }

}
