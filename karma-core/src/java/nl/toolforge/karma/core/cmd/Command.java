package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
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
	 * Default filename for the command descriptor file
	 */
	public static final String DEFAULT_COMMAND_FILE = "commands.xml";

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
