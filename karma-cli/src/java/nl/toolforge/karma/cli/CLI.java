package nl.toolforge.karma.cli;

import java.io.File;

import nl.toolforge.karma.cli.cmd.CLICommandResponseHandler;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import nl.toolforge.karma.core.boot.WorkingContextException;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.prefs.Preferences;

/**
 * The command-line-interface for Karma. This class runs one command, then quits (gracefully hopefully). All arguments
 * are considered a command-line that will be interpreted by the <code>CommandContext</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CLI {

  private static Log logger = LogFactory.getLog(CLI.class);


  /**
   *
   * @param args  The working context (0), whether to update (1) and the command
   *              plus his options (3 ...).
   */
  public void runCli(String[] args) {
    boolean updateStores = new Boolean(args[1]).booleanValue();

    //compose the command line to be passeg to the getCommand method.
    String commandLine = args[2];
    for (int i = 3; i < args.length; i++) {
      if (args[i].indexOf(" ") != -1) {
        //put quotes around arguments that contain spaces
        commandLine += " \""+ args[i] +"\"";
      } else {
        commandLine += " " + args[i];
      }
    }
    logger.debug("runCli - commandline has become: "+commandLine);

    // todo WorkingContext should be initializing the logging system. Some other way.
    WorkingContext workingContext;
    if ( args[0] == null || args[0].equals("") ) {
      workingContext = new WorkingContext(Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT_PREFERENCE, WorkingContext.DEFAULT));
    } else {
      workingContext = new WorkingContext(args[0]);
    }
    WorkingContextConfiguration configuration = new WorkingContextConfiguration(workingContext);

    try {
      configuration.load();
    } catch (WorkingContextException e) {
      //
    }
    workingContext.configure(configuration);

    System.out.println("[ karma ] Checking command ...");

    try {
      Command command = null;
      try {
        CommandFactory factory = CommandFactory.getInstance();
        command = factory.getCommand(commandLine);
      } catch (CommandLoadException e) {
        throw new CommandException(e.getErrorCode(),  e.getMessageArguments());
      }

      System.out.println("[ karma ] Command `" + command.getName() + "` ok !");
      System.out.println("[ karma ] Working context : " + workingContext.getName());

      CommandContext commandContext = new CommandContext(workingContext);
      try {
        commandContext.init(new CLICommandResponseHandler(), updateStores);
      }  catch (CommandException e) {
        System.out.println("\n" + "[ karma ] " + e.getMessage());
      }

      commandContext.execute(command);
    } catch (CommandException e) {

      System.out.println("\n" + "[ karma ] " + e.getMessage());

      System.exit(1);
    } catch (Throwable e) {
      logger.fatal("Exception caught by CLI catch-all. ", e);
      
      String logfile = System.getProperty("karma.home", System.getProperty("user.home")) + File.separator + "logs" + File.separator + "karma-default.log";
      
      System.out.println("Something went BOOM inside of Karma.");
      System.out.println("Details: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
      System.out.println("See the log file (" + logfile + ") for more information.");
      System.out.println("Please report recurring problems to the Karma developers (http://sourceforge.net/tracker/?group_id=98766).");
      System.out.println("We apologize for the inconvenience.");
      System.out.println();
      
      System.exit(1);
    }
    
    System.exit(0);
  }

}
