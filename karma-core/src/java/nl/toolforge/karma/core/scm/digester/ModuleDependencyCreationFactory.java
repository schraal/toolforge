package nl.toolforge.karma.core.scm.digester;

import nl.toolforge.karma.core.scm.ModuleDependency;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Creation factory for {@link ModuleDependency} instances.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleDependencyCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    ModuleDependency dependency = new ModuleDependency();
    dependency.setModule(attributes.getValue("module"));
    dependency.setLibModule(attributes.getValue("libModule"));
    dependency.setGroupId(attributes.getValue("groupId"));
    dependency.setArtifactId(attributes.getValue("artifactId"));
    dependency.setId(attributes.getValue("id"));
    dependency.setVersion(attributes.getValue("version"));
    dependency.setJar(attributes.getValue("jar"));
    if (attributes.getValue("package") != null) {
      dependency.setPackage(new Boolean(attributes.getValue("package")).booleanValue());
    }

    return dependency;
  }
}
