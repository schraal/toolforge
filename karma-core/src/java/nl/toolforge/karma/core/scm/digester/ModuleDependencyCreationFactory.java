/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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
