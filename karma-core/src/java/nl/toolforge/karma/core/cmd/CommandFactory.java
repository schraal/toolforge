/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.cmd;

import net.sf.sillyexceptions.OutOfTheBlueException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.toolforge.karma.core.KarmaRuntimeException;

//import net.sf.sillyexceptions.OutOfTheBlueException;

/**
 * This factory is the single resource of Command objects. <code>KarmaRuntimeException</code>s are thrown when
 * something fails.
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public final class CommandFactory {

  // Singleton
  //
  private static CommandFactory factory = null;

  // Reference ro all command names
  //
  private static Set commandNames = null;

  private static Map commandsByName = null;
  private static Map commandsByAlias = null;

  /**
   * Only static methods
   */
  private CommandFactory() throws CommandLoadException {
    init();
  }

  private synchronized void init() throws CommandLoadException {

    CommandDescriptorMap descriptors = CommandLoader.getInstance().load();

    commandsByName = new TreeMap();
    commandsByAlias = new TreeMap();
    commandNames = new HashSet();

    // Store all commands by name in a hash
    // Create a set of all command names.
    //
    for (Iterator i = descriptors.values().iterator(); i.hasNext();) {
      CommandDescriptor descriptor = (CommandDescriptor) i.next();

      commandsByName.put(descriptor.getName(), descriptor);
      commandNames.add(descriptor.getName());

      for (Iterator j = descriptor.getAliasList().iterator(); j.hasNext();) {
        String alias = (String) j.next();
        commandsByAlias.put(alias, descriptor);
        commandNames.add(alias);
      }
    }
  }

  /**
   * Gets the singleton <code>CommandFactory</code>.
   *
   * @return The singleton <code>CommandFactory</code> instance.
   */
  public static CommandFactory getInstance() throws CommandLoadException {
    if (factory == null) {
      factory = new CommandFactory();
    }
    return factory;
  }

  /**
   * Retrieves the correct <code>Command</code>-instance, by looking up the implementation class through
   * <code>commandLine</code>.
   *
   * @param commandLineString Command arguments (e.g. <code>select-manifest -m karma-1.0</code>).
   * @return The implementation of a <code>Command</code> object.
   * @throws CommandException When a correct command could not be constructed. See
   *                          {@link CommandException#INVALID_COMMAND}.
   */
  public Command getCommand(String commandLineString) throws CommandException {

    String commandName = null;
    commandLineString = commandLineString.trim();

    if (commandLineString.indexOf(' ') > 0) {
      commandName = commandLineString.substring(0, commandLineString.indexOf(' '));
    } else {
      commandName = commandLineString;
    }

    char[] chars = commandLineString.substring(commandName.length()).toCharArray();

    List commandOptionsList = new ArrayList();

    int j = 0;
    String part = null;

    while (j < chars.length) {

      // Go to the next part while we are a 'space' (chr(32))
      //
      while (chars[j] == ' ') {
        j++;
      }

      part = "";
      if (chars[j] != '"') {
        // Start of options or 'normal' arguments.
        //
        part += chars[j];
        j++;

        while ((j < chars.length) && (chars[j] != ' ') && (chars[j] != '\"')) {
          // Continue until a space or \" is reached.
          //
          part += chars[j];
          j++;
        }

      } else if (chars[j] == '"') {
        // Begin van een argument dat bestaat uit een block data, gedemarkeerd door dubbele quotes, escaped dubbel
        // quote wordt eruit gevist.
        //
        part += chars[j];
        j++;

        while (j < chars.length) {

          if (chars[j] == '"') {
            if (chars[j-1] != '\\') {
              // End of demarkated piece of text.
              //
              j++;
              break;
            }
          }

          // doorlopen totdat een '"' is bereikt, behalve \" (escaped).
          part += chars[j];
          j++;
        }
        // We have reached a '"', which must be added.
        //
        part += '"';
        j++;
      }

      if (part.startsWith("\"") && part.endsWith("\"")) {
        part = part.substring(1, part.length() - 1).trim();
      }

      commandOptionsList.add(part);
    }

    String[] commandOptions = (String[]) commandOptionsList.toArray(new String[commandOptionsList.size()]);

    return getCommand(commandName, commandOptions);
  }

  public Command getCommand(String commandName, String[] arguments) throws CommandException {

    Command cmd = null;

    if (isCommand(commandName)) {

      CommandDescriptor descriptor = null;
      try {
        descriptor = getCommandDescriptor(commandName);

        // Construct the command implementation, with the default constructor
        //
        Class implementingClass = null;
        try {
          implementingClass = Class.forName(descriptor.getClassName());
        } catch (ClassNotFoundException c) {
          throw new CommandException(CommandException.NO_IMPLEMENTING_CLASS, new Object[]{descriptor.getClassName()});
        }

        Constructor defaultConstructor = implementingClass.getConstructor(new Class[]{CommandDescriptor.class});
        cmd = (Command) defaultConstructor.newInstance(new Object[]{descriptor});

        // Parse the command line options.
        //
        CommandLineParser parser = new PosixParser();

        Options parserOptions = descriptor.getOptions();

//        if (parserOptions.getOptions().size() == 0 && arguments.length > 0) {
//          // The issue is that this is 1. not an exception and 2. no mechanism to propagate this back in a nice way.
//          throw new CommandException(CommandException.NO_OPTIONS_REQUIRED);
//        }

        cmd.setCommandLine(parser.parse(parserOptions, arguments));

      } catch (NoSuchMethodException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (SecurityException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (InstantiationException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (IllegalAccessException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (IllegalArgumentException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (InvocationTargetException e) {
        throw new KarmaRuntimeException(e.getLocalizedMessage(), e);
      } catch (ParseException e) {
        if (e instanceof MissingOptionException) {
          throw new CommandException(e, CommandException.MISSING_OPTION, new Object[]{arguments});
        }
        if (e instanceof UnrecognizedOptionException) {
          throw new CommandException(e, CommandException.INVALID_OPTION, new Object[]{arguments});
        }
        if (e instanceof MissingArgumentException) {
          throw new CommandException(e, CommandException.MISSING_ARGUMENT, new Object[]{arguments});
        }
      }
      return cmd;
    }
    // At this point, we have no command
    //
    throw new CommandException(CommandException.UNKNOWN_COMMAND, new Object[]{commandName});
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
   * parameter). Returns <code>null</code> if the descriptor could not be found.
   */
  public CommandDescriptor getCommandDescriptor(String commandId) {

    try {
      init(); //this fixes the bug where the previous value of an option is returned
    } catch (CommandLoadException cle) {
      //this can not happen, since the init has already been called in the constructor
      //and it was successful then.
      throw new OutOfTheBlueException("Tried to reload the commands, but failed. This is strange because they have been loaded earlier with success");
    }

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
