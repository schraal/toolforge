package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandResponse;
import nl.toolforge.karma.core.vc.VersionExtractor;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public class PromoteCommand extends DefaultCommand {

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
    VersionExtractor extractor = new CVSVersionExtractor(getContext());
    String nextVersion = extractor.getNextVersion(getContext().getCurrent().getModule(moduleName));

    return new SimpleCommandResponse();
  }
}
