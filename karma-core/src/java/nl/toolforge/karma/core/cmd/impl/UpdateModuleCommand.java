package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;

/**
 * <p>This command updates a module on a developers' local system. When the module has not been updated before, the
 * module will be checked out (this is transparent for the user). The syntax for this command is:
 * <p/>
 * <pre>update-module -m, --module-name &lt;module-name&gt; [ -v &lt;version-number&gt; ]</pre
 * <p/>
 * <p>The <code>m</code> option specifies the module that should be updated. The <code>v</code> option specifies a
 * specific version that should be fetched. The <code>v</code> option implies that the module is updated to
 * <code>STATIC</code> state, regardless of the configuration for the module in the manifest. This state can be revoked
 * by updating the module without specifying the <code>v</code> option. Karma will apply pattern rules to determine the
 * actual symbolic name that is used in the version control system for the version.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class UpdateModuleCommand extends DefaultCommand {

  private CommandResponse response = null;

  /**
   * Creates a <code>UpdateModuleCommand</code> for module <code>module</code> that should be updated. This module
   * requires an <code>Option</code>
   *
   * @param descriptor The command descriptor for this command.
   */
  public UpdateModuleCommand(CommandDescriptor descriptor) throws CommandException {

    super(descriptor);

    response = new ActionCommandResponse();
  }

  /**
   * This command will update the module from the version control system. An update is done when
   * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
   * is relative to the root directory of the <code>active</code> manifest.
   */
  public void execute() throws CommandException {

    String moduleName = "";
    Module module = null;

    // A manifest must be present for this command
    //
    // todo move to aspect; this type of checking can be done by one aspect.
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    moduleName = getCommandLine().getOptionValue("m");
    try {
      module = getContext().getCurrent().getModule(moduleName);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(),e.getMessageArguments());
    }

    Version version = null;
    if (getCommandLine().getOptionValue("v") != null) {
      // The module should be updated to a specific version.
      //
      version = new Version(getCommandLine().getOptionValue("v"));
    } else if (((SourceModule) module).getState().equals(Module.STATIC)) {
      version = ((SourceModule) module).getVersion();
    } else if (((SourceModule) module).getState().equals(Module.DYNAMIC)) {
      // todo CVSVersionExtractor should be retrieved through a Factory.
      //
      try {
        version = CVSVersionExtractor.getInstance().getLastVersion(module);
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }

    try {
      if (version != null && version.equals(CVSVersionExtractor.getInstance().getLocalVersion(getContext().getCurrent(), module))) {
        // todo message to be internationalized.
        //

        // No need to update.
        //
        response.addMessage(
            new SuccessMessage("Module " + module.getName() + " is already up-to-date with version " + version.toString()));

      } else {

        Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getCurrent().getDirectory());
        runner.setCommandResponse(response);

        if (!runner.existsInRepository(module)) {
          throw new CommandException(VersionControlException.MODULE_NOT_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
        }

        runner.checkout(module, version);

        // todo message to be internationalized.
        //
        CommandMessage message = null;
        if (version == null) {
          message = new SuccessMessage("Module " + module.getName() + " updated.");
        } else {
          message = new SuccessMessage("Module " + module.getName() + " updated with version " + version.toString());
        }
        response.addMessage(message);
      }
    } catch (VersionControlException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
