package nl.toolforge.karma.core.scm.digester;

import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;
import nl.toolforge.karma.core.scm.ModuleDependency;

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
    dependency.setGroupId(attributes.getValue("groupId"));
    dependency.setArtifactId(attributes.getValue("artifactId"));
    dependency.setId(attributes.getValue("id"));
    dependency.setVersion(attributes.getValue("version"));
    dependency.setJar(attributes.getValue("jar"));

    return dependency;
  }
}
