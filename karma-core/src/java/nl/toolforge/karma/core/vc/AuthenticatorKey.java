package nl.toolforge.karma.core.vc;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class AuthenticatorKey {

  private String workingContext = null;
  private String locationId = null;

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
