package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.ErrorCode;

/**
 * Thrown when
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DependencyException extends Exception {

  public static final ErrorCode DUPLICATE_ARTIFACT_VERSION = new ErrorCode("DEP-00001");
  
}
