package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.cli.Options;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandDescriptor {

  // todo ideally we want an approach like Options in here; each method we call delivers an even better Command.
  // call it a CommandBuilder.
  //


  /** Maps to the &lt;command name="update-module"&gt; */
  public static final String UPDATE_MODULE_COMMAND = "update-module";
  /** Maps to the &lt;command name="select-manifest"&gt; */
  public static final String SELECT_MANIFEST_COMMAND = "select-manifest";

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
	 * @param name        The name of the command (the <code>name</code> attribute of the &lt;command&gt;-element).
	 * @param alias       The alias of the command (the <code>alias</code> attribute of the &lt;command&gt;-element).
	 * @param commandImpl The <code>Class</code> implementing the command's behavior.
	 */
	public CommandDescriptor(String name, String alias, String commandImpl) throws KarmaException {

		this.name = name;
		this.alias = alias;

		try {
			this.commandImpl = Class.forName(commandImpl);
		} catch (ClassNotFoundException c) {
			throw new KarmaRuntimeException("Implementation class for " + name + " not found.");
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

	/**
	 * @param options The name of the command (the lt;options&gt;-child-element attribute of the &lt;command&gt;-element).
	 */
	public void setOptions(Options options) {

		if (options == null) {
			throw new IllegalArgumentException("When using this setter, provide a non-null Options object.");
		}

		this.options = options;
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

	/**
	 * Commands are equal when their names are equal.
	 *
	 * @param o The object instance that should be compared with <code>this</code>.
	 * @return <code>true</code> if this command descriptor is equal to <code>o</code> or <code>null</code> when
	 *   <code>o</code> is not a <code>CommandDescriptor</code> instance or when it is not the same object.
	 */
	public boolean equals(Object o) {

		if (o instanceof CommandDescriptor) {
			if (this.getName().equals(((CommandDescriptor) o).getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		// Volgens mij gaat het volgende hartstikke fout !
		//
		return name.hashCode();
	}

}


