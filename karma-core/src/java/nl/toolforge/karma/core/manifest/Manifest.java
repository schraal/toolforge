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

import nl.toolforge.karma.core.module.Module;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Manifest {

  public static final String DEVELOPMENT_MANIFEST = "development";
  public static final String RELEASE_MANIFEST = "release";

  public static final String HISTORY_KEY = "manifest.history.last";

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public String getName();

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public String getVersion();

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Map getAllModules();

  /**
   * The base location of the manifest within the current working context. This location is used extensively by Karma
   * as the base directory to checkout projects and build modules.
   *
   * @return A <code>File</code> reference to the manifest base directory.
   */
  public File getBaseDirectory();

  /**
   * The <code>build</code> default child directory of the {@link #getBaseDirectory}. This location stores a
   * manifests' build output.
   *
   * @return The <code>build</code> default child directory of the {@link #getBaseDirectory}.
   */
  public File getBuildBaseDirectory();

  /**
   * The <code>reports</code> default child directory of the {@link #getBaseDirectory}. This location stores
   * the output of commands that generate reports.
   *
   * @return The <code>reports</code> default child directory of the {@link #getBaseDirectory}.
   */
  public File getReportsBaseDirectory();

  /**
   * The <code>modules</code> default child directory of the {@link #getBaseDirectory}. This location stores a
   * manifests' modules.
   *
   * @return The <code>modules</code> default child directory of the {@link #getBaseDirectory}.
   */
  public File getModuleBaseDirectory();

  /**
   * The base location of a temp directory the manifest within the current working context. This location is used
   * extensively by Karma as a temporary location and should not be removed for as long as the manifest has a presence
   * on disk.
   *
   * @return A <code>File</code> reference to the <code>{@link #getBaseDirectory()} + "/tmp"</code>
   */
  public File getTempDirectory();

  public void setState(Module module, Module.State state);

  public boolean isLocal(Module module);

  public Module.State getState(Module module);


  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
//  public String resolveArchiveName(Module module) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Module getModule(String moduleName) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Collection getModuleInterdependencies(Module module) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Map getInterdependencies() throws ManifestException;


  /**
   * Returns a collection containing all of a manifests' included manifests.
   *
   * @return Collection with Manifest instances.
   */
  public Collection getIncludes();

  public String getType();
}
