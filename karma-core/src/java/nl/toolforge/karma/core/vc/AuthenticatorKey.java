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
public final class AuthenticatorKey {

  private String workingContext = null;
  private String locationId = null;

  /**
   * Creates a new authenticator key. References to <code>locations.xml</code>.
   * @param workingContext The name of the working context.
   * @param locationId The id of the location.
   * @throws IllegalArgumentException When the parameters are empty or null. 
   */
  public AuthenticatorKey(String workingContext, String locationId) {

    if ("".equals(workingContext) || "".equals(locationId) || workingContext == null || locationId == null) {
      throw new IllegalArgumentException("Composite key : workingContext and locationId cannot be null or empty.");
    }

    this.workingContext = workingContext;
    this.locationId = locationId;
  }

  public int hashCode() {
    return workingContext.hashCode() + locationId.hashCode();
  }

  public boolean equals(Object obj) {

    if (obj instanceof AuthenticatorKey) {
      return
          ((AuthenticatorKey) obj).workingContext.equals(workingContext) &&
          ((AuthenticatorKey) obj).locationId.equals(locationId);
    } else {
      return false;
    }
  }

  public String toString() {
    return "[wc:" + workingContext + ", id:" + locationId + "]";
  }

}
