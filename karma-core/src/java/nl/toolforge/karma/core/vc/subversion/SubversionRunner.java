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
package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSException;

import java.io.File;

/**
 * // TODO Lots of todo's, subversion implementation
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public final class SubversionRunner implements Runner {

  public SubversionRunner(Location location) throws CVSException {

  }

  public void setCommandResponse(CommandResponse response) {

  }

  public void create(Module module, String comment, ModuleLayoutTemplate template) throws VersionControlException {

  }

  public void add(Module module, File[] files, File[] dirs) throws VersionControlException {

  }

  public void add(Module module, String[] files, String[] dirs) throws VersionControlException {

  }

  public void checkout(Module module) throws VersionControlException {

  }

  public void checkout(Module module, Version version) throws VersionControlException {

  }

  public void checkout(Module module, DevelopmentLine developmentLine, Version version) throws VersionControlException {

  }

  public void update(Module module) throws VersionControlException {

  }

  public void update(Module module, Version version) throws VersionControlException {

  }

  public void promote(Module module, String comment, Version version) throws VersionControlException {

  }

  public boolean existsInRepository(Module module) {
    return false;
  }

  public boolean hasPatchLine(Module module) {
    return false;
  }

  public void createPatchLine(Module module) throws VersionControlException {

  }
}