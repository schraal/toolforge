package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.vc.VersionExtractor;
import org.netbeans.lib.cvsclient.command.log.LogInformation;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class CVSVersionExtractor implements VersionExtractor {

  private CommandContext context = null;

  /**
   * Constructs an extractor for a CVS repository. The command context is passed as a parameter.
   *
   * @param context
   */
  public CVSVersionExtractor(CommandContext context) {
      this.context = context;
  }

  /**
   * <p>See {@link VersionExtractor#getNextVersion}.
   *
   * <p>Connects to the correct CVS repository and determines the next version in the branch (if applicable, otherwise
   * it is the trunk) for the module. This is done by quering <code>module.info</code>.
   *
   * @param module The next version number <code>module</code>.
   *
   * @return The next version for <code>module</code>.
   * @throws KarmaException TODO complete when implementation is ready.
   */
  public String getNextVersion(Module module) throws KarmaException {

    if (module instanceof SourceModule) {
       if (((SourceModule) module).hasModuleInfo()) {
         throw new KarmaException(KarmaException.NO_MODULE_INFO, new Object[]{module.getName()});
       }
    }

    CVSRunner runner = null;
    try {
      runner = (CVSRunner) context.getRunner(module);
    } catch (KarmaException e) {
      throw new CommandException(null); // TODO to be defined (errorcode).
    }

    LogInformation logInformation = runner.log(module);

    return "0-1";
  }
}
