package nl.toolforge.karma.core.manifest.digester;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.DevelopmentManifest;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ReleaseManifest;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Creation factory to be able to create {@link nl.toolforge.karma.core.manifest.Manifest} instances. The reason to have this class
 * is that Digester at some point calls <code>hashCode()</code> on newly created <code>AbstractManifest</code> instances
 * and this fails with a <code>NullPointerException</code> if <code>name</code> is still
 * <code>null</code>; Digester somehow calls upon the setName() later on in the process.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    // todo right now, for <include-manifest>-elements, 

    String name = attributes.getValue("name");
    String type = attributes.getValue("type");

    if (Manifest.DEVELOPMENT_MANIFEST.equals(type)) {
      return new DevelopmentManifest(name);
    }
    if (Manifest.RELEASE_MANIFEST.equals(type)) {
      return new ReleaseManifest(name);
    }
    throw new KarmaRuntimeException("A manifest should be of type 'development' or of type 'release'.");
  }
}
