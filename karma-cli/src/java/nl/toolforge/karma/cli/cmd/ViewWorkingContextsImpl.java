package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.impl.ViewWorkingContexts;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

/**
 * Views all working contexts in tabular format.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ViewWorkingContextsImpl extends ViewWorkingContexts {

  private CommandResponse response = new CommandResponse();

  public ViewWorkingContextsImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    StringBuffer buffer = new StringBuffer();

    buffer.append("\n");
    String header = "Available working contexts:";
    buffer.append(header + "\n");
    buffer.append(StringUtils.repeat("_", header.length()) + "\n\n");

    for (Iterator i = getWorkingContexts().iterator(); i.hasNext();) {
      buffer.append((String) i.next() + "\n");
    }

    response.addEvent(new MessageEvent(new SimpleMessage(buffer.toString())));
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
