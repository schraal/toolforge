package nl.toolforge.karma.core.manifest;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Creation factory to be able to create {@link Manifest} instances. The reason to have this class
 * is that Digester at some point calls <code>hashCode()</code> on newly created <code>Manifest</code> instances
 * and this fails with a <code>NullPointerException</code> if <code>name</code> is still
 * <code>null</code>; Digester somehow calls upon the setName() later on in the process.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    String name = attributes.getValue("name");

    return new Manifest(name);
  }
}
