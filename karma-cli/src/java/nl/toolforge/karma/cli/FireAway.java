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

      System.out.println("\nYou are almost there ...\n\n");
      System.out.println("Please start Karma in one of the following ways :\n\n");

      System.out.println("karma console [-w <working-context>]     Starts the Karma console.");
      System.out.println("karma <command-line-string>              Runs the command that is passed as a command-line-string");

      System.exit(1);

    } else {

      if (CONSOLE.equals(args[0])) {

        String[] consoleArgs = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
          consoleArgs[i-1] = args[i];
        }
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
}
