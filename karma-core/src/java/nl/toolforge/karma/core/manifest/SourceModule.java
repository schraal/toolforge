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

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.module.ModuleDescriptor;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.SourceModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.FileTemplate;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;
import nl.toolforge.karma.core.vc.cvsimpl.CVSException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.scm.digester.ModuleDependencyCreationFactory;
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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

  public Type getInstanceType() {
    return Module.JAVA_SOURCE_MODULE;
  }

  public ModuleLayoutTemplate getLayoutTemplate() {
    return new SourceModuleLayoutTemplate();
  }

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code>, <code>location</code> and <code>version</code>.
   */
  public SourceModule(String name, Location location, Version version) {
    super(name, location, version);
  }

}
