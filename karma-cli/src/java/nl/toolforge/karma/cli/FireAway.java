package nl.toolforge.karma.cli;

import nl.toolforge.karma.console.KarmaConsole;

/**
 * Bootstrap thing for Karma. If <code>console</code> is passed as the first argument, <code>KarmaConsole</code> will be
 * started with the remaining arguments passed as arguments to the console. Otherwise, <code>CLI</code> is started,
 * which is the 'real' command-line modeof Karma.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class FireAway {

  public static final String CONSOLE = "console";

  public static void main(String[] args) {

    if (args.length == 0) {
      KarmaConsole.main(new String[0]);
    }

//    if (CONSOLE.equals(args[0])) {
    if ("-w".equals(args[0])) {

      String[] consoleArgs = new String[1];
      consoleArgs[0] = args[1];
      // Run the console.
      //
      KarmaConsole.main(consoleArgs);
    } else {
      // Run the command line.
      //
      CLI.main(args);
    }
    System.exit(0);
  }
}
