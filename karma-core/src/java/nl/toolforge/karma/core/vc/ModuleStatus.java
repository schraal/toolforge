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
package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Version;

/**
 * <p>Each module exists on a number of places. Virtually, they appear as a <code>Module</code> instance in Karma. The
 * status of a module at any given point in time is determined by a number of variables:
 *
 * <ul>
 *   <li/>Latest remote version; this is the latest version of the module in the version control system as determined
 *        by the <code>PromoteCommand</code> command. The version is relative to the development line for the module
 *        (which could be a patch line for <code>STATIC</code> modules in a <code>ReleaseManifest</code>.
 *   <li/>The local version; this is the version of the locally checked out module. For CVS, this is the so-called
 *        sticky-tag.
 *   <li/>The next version of the module; this is the version when the module will be promoted again.
 *   <li/>The manifest version of the module; this is the version as specified in the &lt;version&gt;-attribute of a
 *        module in the manifest.
 *   <li/>Existence of a module in a version control repository; one can specify a module in a manifest with a certain
 *        location, but if the module doesn't exist in the version control repository (as identified by that location),
 *        Karma needs to know that.
 * </ul>
 *
 * <p>This class contains the full status of a module, as specified by the above variables.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface ModuleStatus {

  public Version getNextVersion();

  public Version getLastVersion();

  public Version getLocalVersion() throws VersionControlException;

  public void setExistsInRepository(boolean exists);

  public boolean existsInRepository();

}
