package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.ErrorCode;
import org.apache.tools.ant.BuildException;

/**
 * A <code>CommandMessage</code> for Ant build stuff.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class AntErrorMessage extends ErrorMessage {

  public AntErrorMessage(BuildException b) {
    // todo Hmm ...
    super("ANT ERROR : " + b.getMessage());
  }

}
