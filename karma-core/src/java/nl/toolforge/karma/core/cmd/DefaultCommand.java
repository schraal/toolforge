package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.bundle.BundleCache;
import org.apache.commons.cli.CommandLine;

import java.util.ResourceBundle;

/**
 * Default stuff for a command. Provides the datastructure and some helper methods to implementing commands.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class DefaultCommand implements Command {

	private CommandContext contextRef = null;

	// Data fields
	//

	private CommandLine commandLine = null;
	private String name = null;
	private String alias = null;
	private String description = null;
	private String helpText = null;
	private Class implementation = null;

//  public final CommandDescriptor getDescriptor() {
//    return this.descriptor;
//  }

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
		implementation = descriptor.getImplementation();
	}

	/**
	 * Sets the command context for this command. The command needs the command context during
	 * the executing phase.
	 *
	 * @param contextRef The <code>CommandContext</code> for this command.
	 */
	public final void setContext(CommandContext contextRef) {
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
	 * Gets a command's alias; the shortcut name for the command.
	 *
	 * @return A command's alias as a <code>String</code>.
	 */
	public final String getAlias() {
		return alias;
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

//  public final Options getOptions() {
//    return descriptor.getOptions();
//  }

	public final Class getImplementation() {
		return implementation;
	}

	/**
	 * Gets all dependencies for this command. This implementation calls its internal <code>CommandDescriptor</code>s'
	 * {@link CommandDescriptor#getDependencies} method.
	 *
	 * @return A <code>Map</code> containing all dependencies as name-value pairs (both are <code>String</code>s).
	 */
//  public final Map getDependencies() {
//    return dependencies;
//  }

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
	 * See {@link #execute}. Implementations must implement this method to get something out of the command.
	 *
	 * @return Command response object, containing whatever happened during execution of the command.
	 * @throws KarmaException
	 */
	public abstract CommandResponse execute() throws KarmaException;

	/**
	 * Helper method to get a resource bundle for frontend messages for commands.
	 *
	 * @return The <code>ResourceBundle</code> for the current locale for frontend messages.
	 */
	protected final ResourceBundle getFrontendMessages() {
		return BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);
	}

}
