package nl.toolforge.karma.core.location;

import org.netbeans.lib.cvsclient.connection.StandardScrambler;
import org.netbeans.lib.cvsclient.connection.Scrambler;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class PasswordScrambler {

  private PasswordScrambler() {}

  /**
   * Scramblers passwords.
   * 
   * @param password
   * @return
   */
  public static String scramble(String password) {

    Scrambler scrambler = StandardScrambler.getInstance();

    return scrambler.scramble(password);
  }
}
