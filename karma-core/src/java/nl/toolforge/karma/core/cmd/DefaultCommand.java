package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.Options;

import java.util.Map;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public abstract class DefaultCommand implements Command {

  private CommandDescriptor descriptor = null;
  private CommandContext contextRef = null;

  public final CommandDescriptor getDescriptor() {
    return this.descriptor;
  }

  /**
   * Creates a command by initializing the command through its <code>CommandDescriptor</code>.
   *
   * @param descriptor The command descriptor instance containing the basic information for this command
   */
  public DefaultCommand(CommandDescriptor descriptor) {

    if (descriptor == null) {
      throw new IllegalArgumentException("Command descriptor cannot be null.");
    }
    this.descriptor = descriptor;
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
    return descriptor.getName();
  }

  /**
   * Gets a command's alias; the shortcut name for the command.
   *
   * @return A command's alias as a <code>String</code>.
   */
  public final String getAlias() {
    return descriptor.getAlias();
  }

  /**
   * Gets a localized version of a command's description.
   *
   * @return A command's description as a <code>String</code>.
   */
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
    return contextRef;
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
