/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.console;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;

import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;

import nl.toolforge.core.util.lang.NiftyStringUtils;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;

public final class CommandRenderer {

  public static final int COMMAND_FILL = 40;
  public static final int OPTION_FILL = 50;

  public static String commands = null;

  private static Map helpCache = null;

  private CommandRenderer() {}

  /**
   * Provides a <code>String</code> view on <code>command</code>.
   *
   * @param commandName The name (or an alias) for the command.
   * @return A <code>String</code> with help info for the command.
   */
  public static String renderCommand(String commandName) throws CommandLoadException, CommandException {

    if (helpCache == null) {
      helpCache = new Hashtable();
    } else {
      if (helpCache.containsKey(commandName)) {
        return (String) helpCache.get(commandName);
      }
    }

    CommandDescriptor descriptor = CommandFactory.getInstance().getCommandDescriptor(commandName);

    if (descriptor == null) {
      throw new CommandException(CommandException.UNKNOWN_COMMAND, new Object[]{commandName});
    }

    Collection optionsCollection = descriptor.getOptions().getOptions();
    Option[] options = (Option[]) optionsCollection.toArray(new Option[optionsCollection.size()]);

    String cachedItem = printCommand(descriptor, options, true, true).toString();
    helpCache.put(commandName, cachedItem);

    return cachedItem;
  }

  /**
   * Provides a <code>String</code> view on all commands, nicely rendered.
   *
   * @param commandDescriptors
   *   A <code>Collection</code> with all command descriptors for the commands applicable to the Karma runtime.
   *
   * @return
   *   A <code>String</code> with all command usages, rendered much like what a shell (like <code>sh</code>) would
   *   provide.
   */
  public static String renderedCommands(Collection commandDescriptors) {

    if (commands == null) {

      StringBuffer buffer = new StringBuffer();

      for (Iterator i = commandDescriptors.iterator(); i.hasNext();) {

        CommandDescriptor descriptor = (CommandDescriptor) i.next();

        Collection optionsCollection = descriptor.getOptions().getOptions();
        Option[] options = (Option[]) optionsCollection.toArray(new Option[optionsCollection.size()]);

        buffer.append(printCommand(descriptor, options, false, false));
      }
      commands = buffer.toString();
    }

    return commands;
  }

  private static StringBuffer printCommand(CommandDescriptor descriptor, Option[] options, boolean showOptions, boolean showHelp) {

    StringBuffer buffer = new StringBuffer();
    String commandNameAlias = descriptor.getName()+" ("+descriptor.getAlias()+")";

    buffer.
        append( commandNameAlias ).
        append( StringUtils.repeat(" ", COMMAND_FILL - commandNameAlias.length()) );
    if (!showOptions) {
      buffer.append( descriptor.getDescription() );
    }
    buffer.append( "\n" );

    if (showOptions) {
      for (int j = 0; j < options.length; j++) {

        Option o = options[j];

        String leftPadding = "   ";

        buffer.
            append(leftPadding).
            append("-" + o.getOpt()).
            append(", --" + o.getLongOpt());

        String args = "";
        if (o.hasArg()) {
          args = " <".concat(o.getArgName()).concat(">");
        }

        // todo when the commands are described with too much of text, then FILL will run out of count ...
        //
        buffer.append(args.concat(StringUtils.repeat(" ", OPTION_FILL - (o.getLongOpt() + args).length())));
        buffer.append(leftPadding);
        if (!o.isRequired()) {
          buffer.append("(Optional) ");
        }
        buffer.append(o.getDescription());
        buffer.append("\n");
      }
    }

    if (showHelp) {
      buffer.append("\n");

      String trimmed = NiftyStringUtils.deleteWhiteSpaceExceptOne(descriptor.getHelp());
      String[] split = NiftyStringUtils.split(trimmed, " ", 120);
      String joined = StringUtils.join(split, "\n");

      buffer.append(joined);
      buffer.append("\n");
    }

    return buffer;
  }
}
