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

import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;

/**
 * Default stuff for a command. Provides the datastructure and some helper methods to implementing commands.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class DefaultCommand implements Command {

  private static final Log logger = LogFactory.getLog(DefaultCommand.class);

	private CommandContext contextRef = null;

	private CommandLine commandLine = null;
	private String name = null;
	private String alias = null;
	private String description = null;
	private String helpText = null;

  private CommandResponseListener responseListener = null;

  /**
	 * Creates a command by initializing the command through its <code>CommandDescriptor</code>.
	 *
	 * @param descriptor The command descriptor instance containing the basic information for this command
	 */
	public DefaultCommand(CommandDescriptor descriptor) {

		if (descriptor == null) {
			throw new IllegalArgumentException("Command descriptor cannot be null.");
		}
		name = descriptor.getName();
		alias = descriptor.getAlias();
		description = descriptor.getDescription();
		helpText = descriptor.getHelp();
	}

	/**
	 * Sets the command context for this command. The command needs the command context during
	 * the executing phase.
	 *
	 * @param contextRef The <code>CommandContext</code> for this command.
	 */
	public final void setContext(CommandContext contextRef) {
    if (this.contextRef != null) {
      throw new IllegalStateException("context is already set");
    }
		this.contextRef = contextRef;
	}

	/**
	 * Gets a command's name.
	 *
	 * @return A command's name as a <code>String</code>.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets a command's alias; the shortcut name for the command. This alias could be a comma-separated String of
   * aliasses, all of which are valid aliasses.
	 *
	 * @return A command's alias as a <code>String</code>.
	 */
	public final String getAlias() {
		return alias;
	}

  /**
   * Helper to get the current <code>WorkingContext</code>.
   *
   * @return The current <code>WorkingContext</code>.
   */
  public final WorkingContext getWorkingContext() {
    return getContext().getWorkingContext();
  }

	/**
	 * Gets a localized version of a command's description.
	 *
	 * @return A command's description as a <code>String</code>.
	 */
	public final String getDescription() {
		return description;
	}

	public final void setCommandLine(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * Gets the parsed command line for this command. This command line can be queried by commands to check if options
	 * had been set, or to retrieve application data.
	 *
	 * @return A command line instance.
	 */
	public final CommandLine getCommandLine() {
		return commandLine;
	}

  public final void registerCommandResponseListener(CommandResponseListener responseListener) {
    this.responseListener = responseListener;
    CommandResponse commandResponse = getCommandResponse();
    if (commandResponse != null) {
      commandResponse.addCommandResponseListener(responseListener);
    } else {
      logger.error("getCommandResponse() returned 'null' for command '"+getName()+"'.");
    }
  }

  public final void deregisterCommandResponseListener(CommandResponseListener responseListener) {
    CommandResponse commandResponse = getCommandResponse();
    if (commandResponse != null) {
      getCommandResponse().removeCommandReponseListener(responseListener);
    } else {
      logger.error("getCommandResponse() returned 'null' for command '"+getName()+"'.");
    }
  }

  /**
   * Gets the response listener object for this command.
   *
   * @return The response listener object for this command.
   */
  public final CommandResponseListener getResponseListener() {
    return responseListener;
  }

	/**
	 * Accessor method for the commands' {@link CommandContext}.
	 *
	 * @return The commands' command context.
	 */
	public final CommandContext getContext() {
		return contextRef;
	}

	/**
	 * A commands help text. Can be overridden for commands that have not provided xml data for the
	 * <code>&lt;help&gt;</code>-element.
	 *
	 * @return Help text for this command.
	 */
	public String getHelp() {
		return helpText;
	}

	/**
	 * Helper method to get a resource bundle for frontend messages for commands.
	 *
	 * @return The <code>ResourceBundle</code> for the current locale for frontend messages.
	 */
	protected final ResourceBundle getFrontendMessages() {
		return BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);
	}

  /**
   * Override to clean up stuff.
   */
  public void cleanUp() {
    // Nothing
  }
}
