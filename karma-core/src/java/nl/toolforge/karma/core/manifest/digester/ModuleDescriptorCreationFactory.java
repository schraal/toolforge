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
package nl.toolforge.karma.core.manifest.digester;

import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

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
