package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.impl.ViewManifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ViewManifestImpl extends ViewManifest {

	public ViewManifestImpl(CommandDescriptor descriptor) throws ManifestException {
		super(descriptor);
	}

	/**
	 * Shows the contents using simple rendering.
	 */
	public void execute() throws CommandException {

		super.execute(); // Ignore the response from the superclass

		List renderedData = getData();

		// -- formatting manifest data

		StringBuffer buffer = new StringBuffer();
		buffer.append("\n");

		String h1 = "MODULE-NAME";
		String h2 = "VERSION";
		String h3 = "DEVELOPMENT-LINE"; // Branch
		String h4 = "STATE";
		String h5 = "LOCATION";

		buffer.append(h1 + StringUtils.repeat(" ", 20 - h1.length()) + "| ");
		buffer.append(h2 + StringUtils.repeat(" ", 20 - h2.length()) + "| ");
		buffer.append(h3 + StringUtils.repeat(" ", 30 - h3.length()) + "| ");
		buffer.append(h4 + StringUtils.repeat(" ", 20 - h4.length()) + "| ");
		buffer.append(h5 + StringUtils.repeat(" ", 20 - h5.length()) + "|\n");
		buffer.append(StringUtils.repeat("_", h5.length() + 110));
		buffer.append("\n");

		for (Iterator i = renderedData.iterator(); i.hasNext();) {

			String[] data = (String[]) i.next();
			buffer.append(data[0] + StringUtils.repeat(" ", 20 - data[0].length()) + "| ");
			buffer.append(data[1] + " " + data[2] + StringUtils.repeat(" ", 20 - data[1].length() - data[2].length() - 1) + "| ");
			buffer.append(data[3] + StringUtils.repeat(" ", 30 - data[3].length()) + "| ");
			buffer.append(data[4] + StringUtils.repeat(" ", 20 - data[4].length()) + "| ");
			buffer.append(data[5] + StringUtils.repeat(" ", 20 - data[5].length()) + "|\n");
		}

		// -- end of formatting

		CommandResponse response = new QueryCommandResponse();
		SimpleCommandMessage message = new SimpleCommandMessage(buffer.toString());
		response.addMessage(message);
	}
}
