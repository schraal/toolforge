package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;
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
	public CommandResponse execute() throws KarmaException {

		String moduleName = getCommandLine().getOptionValue("m");

		// TODO extractor impl should be obtained from karma.properties or Preferences.
		//
		VersionExtractor extractor = CVSVersionExtractor.getInstance();

		Module module = getContext().getCurrent().getModule(moduleName);

		Version nextVersion = extractor.getNextVersion(module);

		Runner runner = getContext().getRunner(module);
		runner.tag(module, nextVersion);

		this.newVersion = nextVersion;

		return new SimpleCommandResponse();
	}

	/**
	 * Returns the new version number for the module, or null when no version number could be set.
	 *
	 * @return
	 */
	protected final Version getNewVersion() {
		return newVersion;
	}
}

