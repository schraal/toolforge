package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Lists all manifests in the working contexts' location store.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ListLocations extends DefaultCommand {

  private CommandResponse response = new CommandResponse();

  public ListLocations(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    Map locations = null;
    try {
      locations = getWorkingContext().getLocationLoader().getLocations();
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    StringBuffer buffer = new StringBuffer();

    buffer.append("| Id" + StringUtils.repeat(" ", 11) + " |");
    buffer.append(" Type" + StringUtils.repeat(" ", 5) + " |");
    buffer.append(" Connection String" + StringUtils.repeat(" ", 51) + " |\n");

    buffer.append("|" + StringUtils.repeat("_", 98) + "|\n");

    for (Iterator i = locations.values().iterator(); i.hasNext();) {

      Location location = (Location) i.next();

      buffer.append("| " + location.getId() + StringUtils.repeat(" ", 14 - location.getId().length()));
      buffer.append("| " + location.getType() + StringUtils.repeat(" ", 10 - location.getType().toString().length()));
      buffer.append("| " + location.toString() + StringUtils.repeat(" ", 69 - location.toString().length()) + "|\n");
    }

    response.addEvent(new MessageEvent(new SimpleMessage(buffer.toString())));

  }

  public CommandResponse getCommandResponse() {
    return response;
  }

}
