package nl.toolforge.karma.cli;

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
  

  public static void main(String[] args) {

    try {
      if (args.length == 0) {

        System.out.println("\nCommand-line is missing ...\n\n");
        System.out.println("CLI usage :\n\n");

        System.out.println("karma <command-line-string>              Runs the command that is passed as a command-line-string");

        System.exit(1);
      }

      CLI cli = new CLI();

      try {
        cli.runCommand(args);
      } catch (CommandException e) {

        System.out.println("\n" + "[ karma ] " + e.getMessage());

        System.exit(1);
      }
      System.exit(0);
    } catch (Exception e) {
      logger.error("Exception caught by CLI catch-all.", e);
      System.out.println("Exception caught by CLI catch-all. Message: " + e.getMessage() + "\n");
      System.exit(1);
    }
  }

  private void runCommand(String[] arguments) throws CommandException {

    boolean updateStores = false;

    String[] actuals = null;
    String commandName = null;
    int index = 0;

    // Filter out the optional '-u' and the command name
    //
    if (arguments.length > 0) {
      if (arguments[0].equals("-u")) {
        updateStores = true;
        actuals = new String[arguments.length - 2];
        commandName = arguments[1];
        index = 2;
      } else {
        actuals = new String[arguments.length - 1];
        commandName = arguments[0];
        index = 1;
      }
    }

    int j = 0;

    for (int i = index; i < arguments.length; i++) {
      actuals[j] = arguments[i];
      j++;
    }

    // todo WorkingContext should be initializing the logging system. Some other way.

    WorkingContext workingContext =
        new WorkingContext(Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT_PREFERENCE, WorkingContext.DEFAULT));

//    File configurationFile = Karma.getConfigurationFile(workingContext);
    WorkingContextConfiguration configuration = new WorkingContextConfiguration(workingContext);




//
    // todo : moet nog anders, alhoewel het inmiddels beter is.
    //
//    ManifestStore manifestStore = null;
//    LocationStore locationStore  = null;
    try {
      configuration.load();
//      //
//      manifestStore = new ManifestStore(workingContext);
//      configuration.getManifestStoreLocation().setWorkingContext(workingContext);
//      manifestStore.setLocation(configuration.getManifestStoreLocation());
//      manifestStore.setModuleName("manifests");
//      workingContext.setManifestStore(manifestStore);
//      //
//      locationStore = new LocationStore(workingContext);
//      configuration.getLocationStoreLocation().setWorkingContext(workingContext);
//      locationStore.setLocation(configuration.getLocationStoreLocation());
//      locationStore.setModuleName("locations");
//      workingContext.setLocationStore(locationStore);

    } catch (WorkingContextException e) {
      //
    } catch (RuntimeException r) {
      //
    }

    // todo het feit dat dit eerst moet sucked.
    //
    workingContext.configure(configuration);






    System.out.println("[ karma ] Checking command ...");

    Command command = null;
    try {
      CommandFactory factory = CommandFactory.getInstance();
      command = factory.getCommand(commandName, actuals);
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
  }
}
