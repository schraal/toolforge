package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.cli.Options;

import java.util.Map;

/**
 *
 * @author D.A. Smedes
 */
public abstract class DefaultCommand implements Command {

	private CommandDescriptor descriptor = null;
	private CommandContext ctx = null;

	public DefaultCommand() {}

	/**
	 * Sets the command context for this command. The command needs the command context during
	 * the executing phase.
	 *
	 * @param context The <code>CommandContext</code> for this command.
	 */
	public final void setContext(CommandContext context) {
		this.ctx = context;
	}

	/**
	 * Creates a command using its mandatory fields.
	 *
	 * @param descriptor The command descriptor instance containing the basic information on this command
	 */
//	public DefaultCommand(CommandDescriptor descriptor) throws KarmaException {
//		this.descriptor = descriptor;
//	}

	public final String getName() {
		return descriptor.getName();
	}

	public final String getAlias() {
		return descriptor.getAlias();
	}

	public final String getDescription() {
		return descriptor.getDescription();
	}

	public final Options getOptions() {
		return descriptor.getOptions();
	}

	public Class getImplementation() {
		return descriptor.getImplementation();
	}

	/**
	 * Gets all dependencies for this command. This implementation calls its internal <code>CommandDescriptor</code>s'
	 * {@link CommandDescriptor#getDependencies} method.
	 *
	 * @return A <code>Map</code> containing all dependencies as name-value pairs (both are <code>String</code>s).
	 */
	public final Map getDependencies() {
		return descriptor.getDependencies();
	}

	/**
	 * Accessor method for the commands' {@link CommandContext}.
	 *
	 * @return The commands' command context.
	 */
	public final CommandContext getContext() {
		return ctx;
	}

	/**
	 * A commands help text. Can be overridden for commands that have not provided xml data for the
	 * <code>&lt;help&gt;</code>-element.
	 *
	 * @return
	 */
	public String getHelp() {
		return descriptor.getHelp();
	}

	public void validate() throws KarmaException {
		throw new KarmaException(KarmaException.NOT_IMPLEMENTED);
	}

	public abstract CommandResponse execute() throws KarmaException;

	/**
	 * See {@link #execute}. Implementations must implement this method to get something out of the command.
	 *
	 * @return Command response object, containing whatever happened during execution of the command.
	 *
	 * @throws KarmaException
	 */
	//public abstract CommandResponse executeCommand() throws KarmaException;
}
