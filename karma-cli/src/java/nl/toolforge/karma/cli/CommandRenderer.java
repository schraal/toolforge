package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Iterator;

public class CommandRenderer {

  public static String commands = null;

  private CommandRenderer() {}

  /**
   * Provides a String view on all commands, nicely rendered.
   *
   * @param commandDescriptors A <code>Collection</code> with all command descriptors for the commands applicable to the
   *   Karma runtime.
   * @return
   */
  public static String renderedCommands(Collection commandDescriptors) {

    if (commands == null) {

      StringBuffer buffer = new StringBuffer();

      for (Iterator i = commandDescriptors.iterator(); i.hasNext();) {

        CommandDescriptor descriptor = (CommandDescriptor) i.next();

        Collection optionsCollection = descriptor.getOptions().getOptions();
        Option[] options = (Option[]) optionsCollection.toArray(new Option[optionsCollection.size()]);

        //
        // Render all options
        //

        // Command name + alias
        //
        buffer.append("\n");
        buffer.
            append(descriptor.getName()).
            append(" (").append(descriptor.getAlias()).append(")").
            append("\n");

        for (int j = 0; j < options.length; j++) {

          Option o = options[j];

          int FILL = 50; // as a constant ...
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
          buffer.
              append(args.concat(StringUtils.repeat(" ", FILL - (o.getLongOpt() + args).length()))).
              append(leftPadding).append(o.getDescription()).
              append("\n");
        }
      }

      commands = buffer.toString();
    }

    return commands;
  }
}
