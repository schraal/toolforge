package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.vc.Runner;

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

	/**
	 * Creates a <code>UpdateModuleCommand</code> for module <code>module</code> that should be updated. This module
	 * requires an <code>Option</code>
	 *
	 * @param descriptor The command descriptor for this command.
	 */
	public UpdateModuleCommand(CommandDescriptor descriptor) throws CommandException {

		super(descriptor);
	}

	/**
	 * This command will update the module from the version control system. An update is done when
	 * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
	 * is relative to the root directory of the <code>active</code> manifest.
	 *
	 * @throws KarmaException When no manifest is loaded, a {@link nl.toolforge.karma.core.ManifestException#NO_MANIFEST_SELECTED} is thrown. For
	 *                        other errors, a more generic {@link KarmaException} is thrown.
	 */
	public CommandResponse execute() throws KarmaException {

		// A manifest must be present for this command
		//
    if (!getContext().isManifestLoaded()) {
      throw new ManifestException(ManifestException.NO_MANIFEST_SELECTED);
    }

		String moduleName = getCommandLine().getOptionValue("m");

		Module module = getContext().getCurrent().getModule(moduleName);

		Version version = null;
		if (getCommandLine().getOptionValue("v") != null) {
			// The module should be updated to a specific version.
			//
			version = new Version(getCommandLine().getOptionValue("v"));
		}

		Runner runner = getContext().getRunner(module);

		CommandResponse response = null;
		if (getContext().getCurrent().isLocal(module)) {
			response = runner.update(module, version);
		} else {
			response = runner.checkout(module, version);
		}

		// Apparently, no exceptions where thrown, so we can continue
		//

		// ...

		return response;
	}
}
