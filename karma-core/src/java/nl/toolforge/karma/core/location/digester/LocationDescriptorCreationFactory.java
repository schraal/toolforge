package nl.toolforge.karma.core.location.digester;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;
import nl.toolforge.karma.core.location.LocationDescriptor;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class LocationDescriptorCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    LocationDescriptor descriptor = new LocationDescriptor();

    descriptor.setId(attributes.getValue("id"));
    descriptor.setType(attributes.getValue("type"));

    return descriptor;
  }
}