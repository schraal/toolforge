package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.cli.ConsoleConfiguration;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.impl.SelectManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class SelectManifestImpl extends SelectManifest {

	private static Log logger = LogFactory.getLog(SelectManifestImpl.class);

  public SelectManifestImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Execute the command in the CLI. When the execution was succesfull, a message is shown on the console.
   *
   */
  public void execute() throws CommandException {

    // Use stuff that's being done in the superclass.
    //
    super.execute(); // Ignore the response from the superclass

		ConsoleConfiguration.setManifest(getContext().getCurrentManifest());

		CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"));
    CommandResponse response = new ActionCommandResponse();
    response.addMessage(message);
  }
}
