package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.Options;

import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author D.A. Smedes
 *
 * @version
 */
public final class CommandDescriptor {

	private String name = null;
	private String alias = null;
	private String description = null;
 	private String helpText = null;

	private Options options = null;

	private Class commandImpl = null;

	private Map deps = new Hashtable();

	/**
	 * Creates a command using its mandatory fields.
	 *
	 * @param name    The name of the command (the <code>name</code> attribute of the &lt;command&gt;-element).
	 * @param alias   The alias of the command (the <code>alias</code> attribute of the &lt;command&gt;-element).
	 * @param options The name of the command (the lt;options&gt;-child-element attribute of the &lt;command&gt;-element).
	 * @param commandImpl   The <code>Class</code> implementing the command's behavior.
	 */
	public CommandDescriptor(String name, String alias, Options options, String commandImpl) throws KarmaException {

		this.name = name;
		this.alias = alias;
		this.options = options;

		try {
			this.commandImpl = Class.forName(commandImpl);
		} catch (ClassNotFoundException c) {
			throw new KarmaException(KarmaException.COMMAND_IMPLEMENTATION_CLASS_NOT_FOUND, c);
		}
	}

	public String getName() {
		return this.name;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public Options getOptions() {
		return this.options;
	}

	public Class getImplementation() {
		return this.commandImpl;
	}

	public void setDependencies(Map dependencies) {
		this.deps = dependencies;
	}

	public Map getDependencies() {
		return this.deps;
	}

	/**
	 * Checks if this command has a certain dependency. Merely a helper method to get to the bottom of it more quickly.
	 *
	 * @param key The dependency key. Check the public fields in {@link nl.toolforge.karma.core.cmd.Command} for a list of 'valid' dependencies.

	 * @return <code>true</code> If the command has a dependency, or <code>false</code> if it hasn't.
	 */
	public boolean hasDependency(String key) {
		return deps.containsKey(key);
	}

	public void setHelp(String helpText) {
		this.helpText = helpText;
	}

	public String getHelp() {
		return this.helpText;
	}
}
