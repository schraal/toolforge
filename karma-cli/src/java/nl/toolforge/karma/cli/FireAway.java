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
      printUsage();
    } else {
      //possible option values
      boolean update = false;
      String context = "";

      //check for options
      int tel = 0;
      while ( (tel < args.length) && args[tel].startsWith("-") ) {
        if ( args[tel].equals("-u") ) {
          //update location- and manifest store.
          System.out.println("-u");
          update = true;
          tel += 1;
        } else if ( args[tel].equals("-w") ) {
          //set working context
          System.out.println("-w");
          //check whether there is an argument, which does not start with a '-'.
          if ( (tel+1 < args.length) && !args[tel+1].startsWith("-") ) {
            context = args[tel+1];
            tel += 2;
          } else {
            System.out.println("Missing working context name (option -w).");
            printUsage();
          }
        } else {
          //unknown option
          System.out.println("Unknown option: "+ args[tel]);
          printUsage();
        }
      }

      //check whether to start the console
      if ( tel >= args.length ) {
        //all options parsed, but no command is present
        System.out.println("No command specified.");
        printUsage();
      } else if ( args[tel].equals(CONSOLE) ) {
        //start the console
        KarmaConsole console = new KarmaConsole();
        console.runConsole(new String[]{context, new Boolean(update).toString()});
      } else {
        //start the CLI
        String[] cliArgs = new String[ 2+(args.length-tel) ];
        cliArgs[0] = context;
        cliArgs[1] = new Boolean(update).toString();
        for (int i = 0; i < (args.length-tel); i++) {
          cliArgs[i+2] = args [tel + i];
        }
        CLI cli = new CLI();
        cli.runCli(cliArgs);
      }
    }
  }

  private static void printUsage() {
    System.out.println("usage: karma [options] console | <command> <command options>");
    System.out.println("");
    System.out.println("Options:");
    System.out.println("  -u                         Update the location- and manifest store.");
    System.out.println("  -w <working context>       Load the given working context.");
    System.out.println("");
    System.out.println("console                      Start the Karma console.");
    System.out.println("<command> <command options>  Run the given command with the given options.");
    System.out.println("                             Use the 'help' command for the list of commands.");
    System.out.println("\n\n");
    System.exit(1);
  }

}
