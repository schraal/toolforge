package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.Options;

import java.util.Map;

/**
 *
 * @author D.A. Smedes
 */
public abstract class DefaultCommand implements Command {

	private CommandDescriptor descriptor = null;

	public DefaultCommand() {
	}

	/**
	 * Creates a command using its mandatory fields.
	 *
	 * @param descriptor The command descriptor instance containing the basic information on this command
	 */
	public DefaultCommand(CommandDescriptor descriptor) throws KarmaException {
		this.descriptor = descriptor;
	}

	public String getName() {
		return descriptor.getName();
	}

	public String getAlias() {
		return descriptor.getAlias();
	}

	public String getDescription() {
		return descriptor.getDescription();
	}

	public Options getOptions() {
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
	public Map getDependencies() {
		return descriptor.getDependencies();
	}

	public String getHelp() {
		return descriptor.getHelp();
	}

	public void validate() throws KarmaException {
        throw new KarmaException(ErrorCode.CORE_NOT_IMPLEMENTED);
	}

	public final CommandResponse execute() throws KarmaException {
		return executeCommand();
	}

	/**
	 * See {@link #execute}. Implementations must implement this method to get something out of the command.
	 *
	 * @return Command response object, containing whatever happened during execution of the command.
	 *
	 * @throws KarmaException
	 */
	public abstract CommandResponse executeCommand() throws KarmaException;
}
