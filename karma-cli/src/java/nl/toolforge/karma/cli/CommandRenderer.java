package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Iterator;

public class CommandRenderer {

	public CommandRenderer() {}

	public StringBuffer renderedCommands(Collection commandDescriptors) {

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

				int FILL = 30; // as a constant ...
        String leftPadding = "   ";

				buffer.
					append(leftPadding).
					append("-" + o.getOpt()).
					append(", --" + o.getLongOpt());

				String args = "";
				if (o.hasArg()) {
					args = " <".concat(o.getArgName()).concat(">");
				}

				buffer.
					append(args.concat(StringUtils.repeat(" ", 30 - (o.getLongOpt() + args).length()))).
					append(leftPadding).append(o.getDescription()).
					append("\n");
			}
    }

		return buffer;
	}
}
