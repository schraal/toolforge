package nl.toolforge.karma.core;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 */
public class VersionCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    String value = attributes.getValue("value");

    return new Version(value);
  }
}
