package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.*;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This command gets the active manifest and presents it in the UI. UI implementations are responsible for the rendering
 * part.
 */
public class ViewManifest extends DefaultCommand {

	private List renderedList = null;

	public ViewManifest(CommandDescriptor descriptor) throws ManifestException {
		super(descriptor);

		renderedList = new ArrayList();
	}

	public CommandResponse execute() throws VersionControlException, CommandException {

		if (!getContext().isManifestLoaded()) {
      throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
    }
    Manifest manifest = getContext().getCurrent();

    ModuleMap sourceModules = manifest.getModules().getSourceModules();

		for (Iterator i = sourceModules.values().iterator(); i.hasNext();) {

			SourceModule module = (SourceModule) i.next();

			String[] moduleData = new String[5];
			moduleData[0] = module.getName();
      moduleData[1] = module.getVersionAsString();
			moduleData[2] = (module.getDevelopmentLine() == null ? "N/A" : module.getDevelopmentLine().getName());
			moduleData[3] = module.getStateAsString();
			moduleData[4] = module.getLocation().getId();

			renderedList.add(moduleData);
		}

		return null; // Leave this repsonse the UI class implementing the rendering. Nothing to report.
	}

	/**
	 * <p>Returns the contents of the manifest in a two-dimensional <code>String[]</code> data-structure, for easy
	 * reference. The contents of this structure can be queried through <code>renderedList.get(i)</code>. This call
	 * retrieves a <code>String[]</code> with the most important data items for each module.
	 *
	 * @return A <code>List</code> containing <code>String[]</code> instances.
	 */
	protected List getData() {
		return renderedList;
	}
}
