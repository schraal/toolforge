package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Thrown by Karma's build system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class KarmaBuildException extends Exception {

  public KarmaBuildException(Throwable t) {
    super(t);
  }

  public KarmaBuildException(String t) {
    super(t);
  }

  public KarmaBuildException(ErrorCode r) {
    super(r.getErrorMessage());
    System.out.println("Exception needs rework ...");
  }
}
