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
import java.util.StringTokenizer;
import java.util.TreeMap;

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
  private CommandFactory() {
    init();
  }

  private synchronized void init() {

    Set descriptors = CommandLoader.getInstance().load();
    commandsByName = new TreeMap();
    commandsByAlias = new TreeMap();
    commandNames = new HashSet();

    // Store all commands by name in a hash
    // Create a set of all command names.
    //
    for (Iterator i = descriptors.iterator(); i.hasNext();) {
      CommandDescriptor descriptor = (CommandDescriptor) i.next();

      commandsByName.put(descriptor.getName(), descriptor);
			commandNames.add(descriptor.getName());

			// An alias may consist of several aliases. When we encounter a space character, split the alias
			// up into several parts, otherwise simply add the descriptor as such.
			if( descriptor.getAlias().indexOf(" ") != -1) {

				StringTokenizer tokenizer = new StringTokenizer(descriptor.getAlias(), " ");
				String alias = "";
				while( tokenizer.hasMoreTokens()) {
					alias = tokenizer.nextToken();
					commandsByAlias.put(alias, descriptor);
      		commandNames.add(alias);
				}
			} else {
				commandsByAlias.put(descriptor.getAlias(), descriptor);
				commandNames.add(descriptor.getAlias());
			}

    }
  }

  /**
   * Gets the singleton <code>CommandFactory</code>.
   */
  public static CommandFactory getInstance() {
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
   *                          {@link CommandException#INVALID_COMMAND}.
   */
  public Command getCommand(String commandLineString) throws CommandException {

    // Extract the command name from the command line string
    //

    String commandName = null;

    if (commandLineString.indexOf(' ') > 0) {
      commandName = commandLineString.substring(0, commandLineString.indexOf(' '));
    } else {
      commandName = commandLineString.trim();
    }

    // todo wordt dit niet gesupport door commons-cli ?
    //

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

        while ((j < chars.length) && (chars[j] != ' ')) {
          // doorlopen totdat een spatie is bereikt
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
        cmd.setCommandLine(parser.parse(parserOptions, commandOptions));

      } catch (NoSuchMethodException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (SecurityException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (InstantiationException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (IllegalAccessException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (IllegalArgumentException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (InvocationTargetException e) {
        throw new CommandException(e, CommandException.INVALID_COMMAND, new Object[]{commandLineString});
      } catch (ParseException e) {
        if (e instanceof MissingOptionException) {
          throw new CommandException(e, CommandException.MISSING_OPTION, new Object[]{commandLineString});
        }
        if (e instanceof UnrecognizedOptionException) {
          throw new CommandException(e, CommandException.INVALID_OPTION, new Object[]{commandLineString});
        }
        if (e instanceof MissingArgumentException) {
          throw new CommandException(e, CommandException.MISSING_ARGUMENT, new Object[]{commandLineString});
        }
      }
      return cmd;
    }
    // At this point, we have no command
    //
    throw new CommandException(CommandException.INVALID_COMMAND, new Object[]{commandLineString});
  }

//  public Command getParallelClone(Command command) {
//    CommandDescriptor descriptor = command.getDescriptor();
//    try {
//      Constructor defaultConstructor = null;
//      defaultConstructor = descriptor.getImplementation().getConstructor(new Class[]{CommandDescriptor.class});
//      Command parallelClone = (Command) defaultConstructor.newInstance(new Object[]{descriptor});
//      return parallelClone;
//    } catch (NoSuchMethodException e) {
//      throw new KarmaRuntimeException(e);
//    } catch (IllegalAccessException e) {
//      throw new KarmaRuntimeException(e);
//    } catch (InvocationTargetException e) {
//      throw new KarmaRuntimeException(e);
//    } catch (InstantiationException e) {
//      throw new KarmaRuntimeException(e);
//    }
//  }

//  public Command getParallelClone(Command command) {
//     CommandDescriptor descriptor = command.getDescriptor();
//     try {
//       Constructor defaultConstructor = null;
//       defaultConstructor = descriptor.getImplementation().getConstructor(new Class[]{CommandDescriptor.class});
//       Command parallelClone = (Command) defaultConstructor.newInstance(new Object[]{descriptor});
//       return parallelClone;
//     } catch (NoSuchMethodException e) {
//       throw new KarmaRuntimeException(e);
//     } catch (IllegalAccessException e) {
//       throw new KarmaRuntimeException(e);
//     } catch (InvocationTargetException e) {
//       throw new KarmaRuntimeException(e);
//     } catch (InstantiationException e) {
//       throw new KarmaRuntimeException(e);
//     }
//   }

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

    init(); //

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
