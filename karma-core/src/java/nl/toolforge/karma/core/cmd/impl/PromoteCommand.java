package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.QueryCommandResponse;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.VersionExtractor;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;

/**
 * Implementation of the 'codeline freeze' concept. Karma increases a modules' version (using whichever pattern is
 * defined for it), thus allowing for a freeze. Development can commence immediately on the module. In that sense, it
 * is not a freeze, just a tiny hick-up in the development process, as modules are generally small in nature.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class PromoteCommand extends DefaultCommand {

	private Version newVersion = null;

	public PromoteCommand(CommandDescriptor descriptor) {
		super(descriptor);
	}

	/**
	 * Promotes a module to the next version number in the branch it is active in within the active manifest.
	 */
	public void execute(CommandResponseHandler handler) {
    try {

      String moduleName = getCommandLine().getOptionValue("m");
      // todo : module should be in working state !!!!!
      //


      SourceModule module = (SourceModule) getContext().getCurrent().getModule(moduleName);

      if (!module.hasVersion()) {
        // todo can be replaced with the stuff that determines if a module is in working state
        //
        throw new CommandException(CommandException.MODULE_WITHOUT_VERSION);
      }

      // TODO extractor impl should be obtained from karma.properties or Preferences to enable configurable stuff.
      //
      VersionExtractor extractor = CVSVersionExtractor.getInstance();

      Version nextVersion = extractor.getNextVersion(module);

      Runner runner = getContext().getRunner(module);
      runner.tag(module, nextVersion);

      this.newVersion = nextVersion;

      new QueryCommandResponse();
    } catch (Exception e) {
      //todo proper exception handling
      e.printStackTrace();
    }
	}

	/**
	 * Returns the new version number for the module, or <code>null</code> when no version number could be set.
	 *
	 * @return The new version number for the module, or <code>null</code> when no version number could be set.
	 */
	protected final Version getNewVersion() {
		return newVersion;
	}
}

