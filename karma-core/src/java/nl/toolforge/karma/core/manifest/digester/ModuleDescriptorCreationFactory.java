package nl.toolforge.karma.core.manifest.digester;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;

/**
 * Creation factory to be able to create {@link nl.toolforge.karma.core.manifest.ModuleDescriptor} instances. The reason to have this class
 * is that Digester at some point calls <code>hashCode()</code> on newly created <code>ModuleDescriptor</code> instances
 * and this fails with a <code>NullPointerException</code> if <code>name</code> or <code>location</locetion> are still
 * <code>null</code>; Digester somehow calls upon the setName() and setLocation() later on in the process.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleDescriptorCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    String name = attributes.getValue("name");
    String type = attributes.getValue("type");
    String location = attributes.getValue("location");

    ModuleDescriptor descriptor = new ModuleDescriptor(name, type, location);

    return descriptor;
  }
}
