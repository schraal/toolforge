package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.CommandLine;

/**
 * <p>A <code>Command</code> is an executable operation in Karma. Commands perform actions as per user requests.
 *
 * <p>Karma commands are based on the <code>Option</code> class in the
 * <a href="http://jakarta.apache.org/commons/cli/apidocs/org/apache/commons/cli/Option.html">Apache</code> package.
 * Karma wraps an <code>Option</code> and features to it.
 *
 * @author D.A. Smedes
 * @author W.M. Oosterom
 *
 * @version $Id$
 */
public interface Command {

	/** Dependency type for a CVS version control system */
	public static final String DEPENDENCY_CVS = "cvs";

	/** Dependency type for a Subversion version control system */
	public static final String DEPENDENCY_SUBVERSION = "subversion";

	/** Dependency type for a library module (mainly a Java jar-file) */
	public static final String DEPENDENCY_MODULE_TYPE_LIB = "lib";

	/** Dependency type for a source module */
	public static final String DEPENDENCY_MODULE_TYPE_SOURCE = "source";

	/** Default filename for the command descriptor file */
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
	 * The Java class implementing the commands' behavior.
	 *
	 * @return The Java class implementing the commands' behavior.
	 * @throws KarmaException If the implementation class is not found, the error code will be set to :
	 *                        {@link KarmaException#COMMAND_IMPLEMENTATION_CLASS_NOT_FOUND}
	 */
	public Class getImplementation() throws KarmaException;

	/**
	 * Gets a <code>Map</code> containing type/name-pairs of dependencies for this module. Dependencies can be
	 * manifold. There can be a dependency on a version control system, or a specific module type. These dependencies
	 * affect what commands can and cannot do in some cases.
	 *
	 * @return
	 */
//	public Map getDependencies();

	/**
	 * The command's help text. Help text can be unlimited. Use <code>HTML</code> for formatting.
	 */
	public String getHelp();

	/**
	 * Executes the command and captures its results in a <code>CommandResponse</code> object for further reference.
	 *
	 * @return The command's output, captured in an object structure.
	 * @throws KarmaException To be documented.
	 */
	public CommandResponse execute() throws KarmaException;

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
}
