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

import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import org.apache.commons.cli.CommandLine;

/**
 * <p>A <code>Command</code> is an executable operation in Karma. Commands perform actions as per user requests.
 * <p/>
 * <p>Karma commands are based on the <code>Option</code> class in the
 * <a href="http://jakarta.apache.org/commons/cli/apidocs/org/apache/commons/cli/Option.html">Apache</code> package.
 * Karma wraps an <code>Option</code> and features to it.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 * @version $Id$
 */
public interface Command {

  /**
	 * Gets the normal name of this command (its full name).
	 *
	 * @return The normal name of this command.
	 */
	public String getName();

	/**
	 * Gets the alias for this command (its short name).
	 *
	 * @return The alias for this command.
	 */
	public String getAlias();

	/**
	 * Gets this command's descriptive text. This is merely a one liner for the command, providing its very existence.
	 *
	 * @return The command's descriptive text.
	 */
	public String getDescription();

	/**
	 * The command's help text. Help text can be unlimited. Use <code>HTML</code> for formatting.
	 */
	public String getHelp();

	/**
	 * Executes the command.
	 *
	 * @throws CommandException When execution failed. This exception catches all underlying exceptions and rethrows them
   *         as a CommandException, except for <code>RuntimeException</code>s.
	 */
	public void execute() throws CommandException;

  /**
   * Called after {@link #execute}. Implementations can use this method to clean up resources and the like.
   */
  public void cleanUp();

	/**
	 * Stores a reference to a <code>CommandContext</code>.
	 *
	 * @param context An initialized command context.
	 */
	public void setContext(CommandContext context);

	public void setCommandLine(CommandLine commandLine);

	/**
	 * Gets the parsed command line for this command. This command line can be queried by commands to check if options
	 * had been set, or to retrieve application data.
	 *
	 * @return A command line instance.
	 */
	public CommandLine getCommandLine();

  /**
   * Register the given <code>CommandResponseListener</code> as the handler for the command responses.
   *
   * @param responseListener
   */
  public void registerCommandResponseListener(CommandResponseListener responseListener);

  /**
   * Deregister the <code>CommandResponseListener</code>.
   */
  public void deregisterCommandResponseListener(CommandResponseListener responseListener);

  /**
   * Return the specific <code>CommandResponse</code> object that this command uses.
   *
   * @return CommandResponse
   */
  public CommandResponse getCommandResponse();
}
