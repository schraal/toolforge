package nl.toolforge.karma.core.cmd;

import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.*;

/**
 * This factory is the single resource of Command objects.
 *
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public final class CommandFactory {

  // Singleton
  //
  private static CommandFactory factory = null;

  // Reference to all loaded commands
  //
//  private static Map commands = null;

  // Reference ro all command names
  //
  private static Set commandNames = null;

  private static Map commandsByName = null;
  private static Map commandsByAlias = null;

  /**
   * Only static methods
   */
  private CommandFactory() throws KarmaException {

    Set descriptors = CommandLoader.getInstance().load();
    //commands = new Hashtable();
    commandsByName = new TreeMap();
    commandsByAlias = new TreeMap();

    // Store all commands by name in a hash
    //
    for (Iterator i = descriptors.iterator(); i.hasNext();) {
      CommandDescriptor descriptor = (CommandDescriptor) i.next();

      commandsByName.put(descriptor.getName(), descriptor);
      commandsByAlias.put(descriptor.getAlias(), descriptor);
    }

    // Create a set of all command names.
    //
    commandNames = new HashSet();
    for (Iterator i = descriptors.iterator(); i.hasNext();) {
      CommandDescriptor descriptor = (CommandDescriptor) i.next();
      commandNames.add(descriptor.getName());
      commandNames.add(descriptor.getAlias());
    }
  }


  public static CommandFactory getInstance() throws KarmaException {
    if (factory == null) {
      factory = new CommandFactory();
    }
    return factory;
  }

  /**
   * Retrieves the correct <code>Command</code>-instance, by looking up the implementation class through
   * <code>commandLine</code>.
   *
   * @param commandLineString Command line string (e.g. <code>select-manifest -m karma-1.0</code>).
   * @return The implementation of a <code>Command</code> object.
   * @throws CommandException When a correct command could not be constructed. See
   *         {@link CommandException#INVALID_COMMAND}.
   */
  public Command getCommand(String commandLineString) throws CommandException {

    StringTokenizer tokenizer = new StringTokenizer(commandLineString);

    // Extract the command name from the command line string
    //
    String commandName = null;
    if (tokenizer.hasMoreTokens()) {
      commandName = tokenizer.nextToken();
    }

    // Extract the command line options and arguments from the command line string
    //
    String[] commandOptions = null;
    if (tokenizer.hasMoreTokens()) {
      commandOptions = new String[tokenizer.countTokens()];
      int i = 0;
      while (tokenizer.hasMoreTokens()) {
        commandOptions[i] = tokenizer.nextToken();
        i++;
      }
    }

    Command cmd = null;

    if (isCommand(commandName)) {

      CommandDescriptor descriptor = null;
      try {
        descriptor = getCommandDescriptor(commandName);

        // Construct the command implementation, with the default constructor
        //
        Constructor defaultConstructor =
          descriptor.getImplementation().getConstructor(new Class[]{CommandDescriptor.class});

        cmd = (Command) defaultConstructor.newInstance(new Object[]{descriptor});

        // Parse the command line options.
        //
        CommandLineParser parser = new PosixParser();
        Options parserOptions = descriptor.getOptions();
        CommandLine commandLine = parser.parse(parserOptions, commandOptions);
        parserOptions = null;

        cmd.setCommandLine(commandLine);
//        commandLine = null;

      } catch (NoSuchMethodException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (SecurityException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (InstantiationException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (IllegalAccessException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (IllegalArgumentException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (InvocationTargetException e) {
        throw new CommandException(CommandException.INVALID_COMMAND, e);
      } catch (ParseException e) {

        if (e instanceof MissingOptionException) {
          throw new CommandException(CommandException.MISSING_OPTION, new Object[]{descriptor.getName()}, e);
        }
        if (e instanceof MissingArgumentException) {
          throw new CommandException(CommandException.MISSING_ARGUMENT, e);
        }
      }
      return cmd;
    }
    // At this point, we have no command
    //
    throw new CommandException(CommandException.INVALID_COMMAND);
  }


  /**
   * Checks if some string is a command within this context.
   *
   * @param name
   * @return
   */
  private boolean isCommand(String name) {
    return commandNames.contains(name);
  }

  public Collection getCommands() {
    return commandsByName.values();
  }

  /**
   * Retrieves the correct command descriptor either by name or by alias (whichever is passed as a
   * parameter).
   */
  private CommandDescriptor getCommandDescriptor(String commandId) {

    if (commandsByName.containsKey(commandId)) {
      return (CommandDescriptor) commandsByName.get(commandId);
    } else {
      if (commandsByAlias.containsKey(commandId)) {
        return (CommandDescriptor) commandsByAlias.get(commandId);
      }
    }
    return null;
  }

}