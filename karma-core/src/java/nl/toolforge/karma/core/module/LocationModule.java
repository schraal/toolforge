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
package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.VersionControlException;

import java.util.Set;

/**
 * 
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class LocationModule extends AdminModule {

  public LocationModule(String moduleName, Location location) {
    super(moduleName, location);
  }


  public ModuleLayoutTemplate getLayoutTemplate() {
   throw new KarmaRuntimeException("To be implemented ...");
  }


  public void createRemote(Authenticator authenticator, String createComment) throws VersionControlException, AuthenticationException {

  }











  // Should be removed after Module is better ......




  public Type getType() {
    return null;
  }

  public DevelopmentLine getPatchLine() {
    return null;
  }

  public void markPatchLine(boolean mark) {

  }

  public boolean hasPatchLine() {
    return false;
  }

  public boolean hasDevelopmentLine() {
    return false;
  }

  public void markDevelopmentLine(boolean mark) {

  }

  public Version getVersion() {
    return null;
  }

  public String getVersionAsString() {
    return null;
  }

  public boolean hasVersion() {
    return false;
  }

  public Set getDependencies() {
    return null;
  }

  public void create() {

  }

  public void createRemote(String createComment) throws VersionControlException {

  }
}
