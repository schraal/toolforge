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



/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Authenticator {

  private String workingContext = null;
  private String id = null;
  private String username = null;
  private String password = null;

  public Authenticator(String id) {
    setId(id);
  }

  /**
   * Empty constructor (generally only used by Digesters).
   */
  public Authenticator() {
    // Empty
  }

  public String getWorkingContext() {
    return workingContext;
  }

  public void setWorkingContext(String workingContext) {

    if ("".equals(workingContext) || workingContext == null) {
      throw new IllegalArgumentException(
          "The `working-context`-attribute for an authenticator cannot be null or an empty string.");
    }
    this.workingContext = workingContext;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    if ("".equals(id) || id == null) {
      throw new IllegalArgumentException(
          "The `id`-attribute for an authenticator cannot be null or an empty string.");
    }
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the password (encrypted) or an empty String if the password is null.
   *
   * @return
   */
  public String getPassword() {
    return (password == null ? "" : password);
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthenticatorKey getAuthenticatorKey() {
    return new AuthenticatorKey(workingContext, id);
  }
}
