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
package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.history.ModuleHistory;
import nl.toolforge.karma.core.history.ModuleHistoryEvent;
import nl.toolforge.karma.core.history.ModuleHistoryException;
import nl.toolforge.karma.core.history.ModuleHistoryFactory;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Renders the contents of the <code>history.xml</code> to the console.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ViewModuleHistory extends DefaultCommand {

  private static final Log logger = LogFactory.getLog(ViewModuleHistory.class);

  private CommandResponse commandResponse = new CommandResponse();

  public ViewModuleHistory(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {


    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

//    SuccessMessage message = new SuccessMessage("Checking manifest status, please wait ...");
//    commandResponse.addMessage(message);

    String moduleName = getCommandLine().getOptionValue("m");
    Module module = null;
    try {
      module = getContext().getCurrentManifest().getModule(moduleName);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(),e.getMessageArguments());
    }

    if (!getContext().getCurrentManifest().isLocal(module)) {
      throw new CommandException(CommandException.MODULE_NOT_LOCAL, new Object[]{module.getName()});      
    }

    ModuleHistoryFactory factory =
        ModuleHistoryFactory.getInstance(getContext().getCurrentManifest().getBaseDirectory());

    ModuleHistory history = null;
    try {
      history = factory.getModuleHistory(module);
    } catch (ModuleHistoryException e) {
      logger.error(e.getMessage());
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    StringBuffer buffer = new StringBuffer();
    buffer.append("\n");

    String header = "Module history for module : " + module.getName();
    buffer.append(header + "\n\n\n");

    String h1 = "Type";
    String h2 = "Author";
    String h3 = "Version";
    String h4 = "Timestamp";
    String h5 = "Comment";

    final int MAX_DATETIME = 30;
    final int MAX_COMMENT = 60;

    buffer.append(h1 + StringUtils.repeat(" ", 20 - h1.length()) + "| ");
    buffer.append(h2 + StringUtils.repeat(" ", 15 - h2.length()) + "| ");
    buffer.append(h3 + StringUtils.repeat(" ", 10 - h3.length()) + "| ");
    buffer.append(h4 + StringUtils.repeat(" ", MAX_DATETIME - h4.length()) + "| ");
    buffer.append(h5 + StringUtils.repeat(" ", MAX_COMMENT - h5.length()) + "| ");
    buffer.append("\n");
    buffer.append(StringUtils.repeat("_", 144));
    buffer.append("\n\n");

    List events = history.getEvents();

    for (Iterator i = events.iterator(); i.hasNext();) {

      ModuleHistoryEvent event = (ModuleHistoryEvent) i.next();

      buffer.append(event.getType() + StringUtils.repeat(" ",  20 - event.getType().length()) + "| ");
      buffer.append(event.getAuthor() + StringUtils.repeat(" ",  15 - event.getAuthor().length()) + "| ");
      buffer.append(event.getVersion().getVersionNumber() + StringUtils.repeat(" ",  10 - event.getVersion().getVersionNumber().length()) + "| ");
      buffer.append(event.getDatetime() + StringUtils.repeat(" ",  MAX_DATETIME - event.getDatetime().toString().length()) + "| ");

      String comment = null;
      if (event.getComment().length() > MAX_COMMENT) {
        comment = event.getComment().substring(0, MAX_COMMENT);
      } else {
        comment = event.getComment();
      }

      buffer.append(comment + StringUtils.repeat(" ",  MAX_COMMENT - comment.length()) + "|\n");
    }

    commandResponse.addEvent(new MessageEvent(new SimpleMessage(buffer.toString())));
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}
