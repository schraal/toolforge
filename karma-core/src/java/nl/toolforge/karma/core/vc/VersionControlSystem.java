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

import nl.toolforge.karma.core.location.BaseLocation;
import nl.toolforge.karma.core.location.LocationType;

/**
 * <p>A reference for a VCS (Version Control System). Everybody knows what a version
 * control system is (otherwise you are not entitled to use this codebase anyway ...),
 * so I'll stick to this message as a documentation snippet for this interface.
 *
 * <p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class VersionControlSystem extends BaseLocation {

  private String host = null;
  private String username = null;
  private String protocol = null;
  private int port = -1;
  private String repository = null;

  private String offset;

  public VersionControlSystem(String id, LocationType type) {
    super(id, type);
  }

  public abstract Authenticator authenticate() throws AuthenticationException;

  public void setHost(String host) {

    if ((host == null) || (host.length() == 0)) {
      throw new IllegalArgumentException("Host cannot be null.");
    }
    this.host = host;
  }


  public String getHost() {
    return host;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }


  public String getProtocol() {
    return protocol;
  }


  public void setPort(int port) {
    this.port = port;
  }


  public int getPort() {
    return port;
  }


  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getModuleOffset() {
    if ("".equals(offset)) {
      return null;
    }
    return offset;
  }

  public void setRepository(String repository) {

    if ((repository == null) || (repository.length() == 0)) {
      throw new IllegalArgumentException("Repository cannot be null.");
    }

    this.repository = repository;
  }


  public String getRepository() {
    return repository;
  }


  public void setUsername(String username) {

    if ((username == null) || (username.length() == 0)) {
      throw new IllegalArgumentException("Username cannot be null.");
    }
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
