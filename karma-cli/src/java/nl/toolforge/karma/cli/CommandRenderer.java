package nl.toolforge.karma.cli;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandDescriptor;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;

public class CommandRenderer {

	public CommandRenderer() {}

	public StringBuffer renderedCommands(Collection commandDescriptors) {

		StringBuffer buffer = new StringBuffer();
		int k = 1;
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

				int FILL = 15; // as a constant ...
        String PADD = "   ";

				buffer.
					append(PADD).
					append("-" + o.getOpt()).
					append(", --" + o.getLongOpt()).append(StringUtils.repeat(" ", FILL - o.getLongOpt().length())).
					append(PADD).append(o.getDescription()).
					append("\n");
			}
    }

		return buffer;
	}
}
