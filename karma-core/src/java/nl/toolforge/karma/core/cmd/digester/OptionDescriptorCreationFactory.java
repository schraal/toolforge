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
package nl.toolforge.karma.core.cmd.digester;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.cli.Option;
import org.xml.sax.Attributes;

/**
 * Creation factory to be able to create {@link nl.toolforge.karma.core.cmd.CommandDescriptor} instances.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class OptionDescriptorCreationFactory extends AbstractObjectCreationFactory {

  public Object createObject(Attributes attributes) throws Exception {

    String opt = attributes.getValue("opt");
    String description = attributes.getValue("description");
    String longOpt = attributes.getValue("longOpt");
    boolean hasArgs = ("true".equals(attributes.getValue("hasArgs")) ? true : false);

    Option option = new Option(opt, longOpt, hasArgs, description);

    boolean required = ("true".equals(attributes.getValue("required")) ? true : false);
    option.setRequired(required);

    return option;
  }
}
