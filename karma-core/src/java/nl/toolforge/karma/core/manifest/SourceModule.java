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
package nl.toolforge.karma.core.manifest;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.scm.digester.ModuleDependencyCreationFactory;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see Module
 */
public class SourceModule extends BaseModule {

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code> and <code>location</code>.
   *
   * @param name Mandatory parameter; name of the module.
   * @param location Mandatory parameter; location of the module.
   */
  public SourceModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code>, <code>location</code> and <code>version</code>.
   */
  public SourceModule(String name, Location location, Version version) {

    super(name, location, version);

  }

  public SourceType getSourceType() {
    return new Module.SourceType("src");
  }

  /**
   * See {@link Module#getDependencies}. This implementation throws a <code>KarmaRuntimeException</code> when the
   *  modules' <code>dependencies.xml</code> could not be parsed properly. When no dependencies have been specified, or
   * when the file does not exist, the method returns an empty <code>Set</code>.
   *
   * @return A <code>Set</code> containing {@link nl.toolforge.karma.core.scm.ModuleDependency} instances.
   */
  public Set getDependencies() {

    Set dependencies = new HashSet();

    // Read in the base dependency structure of a Maven project.xml file
    //
    Digester digester = new Digester();

    digester.addObjectCreate("*/dependencies", HashSet.class);
    digester.addFactoryCreate("*/dependency", ModuleDependencyCreationFactory.class);
    digester.addSetNext("*/dependency", "add");

    try {

      dependencies = (Set) digester.parse(new File(getBaseDir(), "dependencies.xml"));

    } catch (IOException e) {
      return new HashSet();
    } catch (SAXException e) {
      throw new KarmaRuntimeException(ManifestException.DEPENDENCY_FILE_LOAD_ERROR, new Object[]{getName()});
    }
    return dependencies;
  }

}
