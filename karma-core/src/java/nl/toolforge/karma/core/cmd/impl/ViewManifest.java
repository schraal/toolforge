package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import nl.toolforge.karma.core.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This command gets the active manifest and presents it in the UI. UI implementations are responsible for the rendering
 * part.
 */
public class ViewManifest extends DefaultCommand {

  Log logger = LogFactory.getLog(ViewManifest.class);

  private List renderedList = null;
  private CommandResponse commandResponse = new ActionCommandResponse();

  public ViewManifest(CommandDescriptor descriptor) {
    super(descriptor);

    renderedList = new ArrayList();
  }

  public void execute() throws CommandException {

    // todo most of it is actually part of cli meuk ...

    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }
    Manifest manifest = getContext().getCurrent();

    Map sourceModules = manifest.getAllModules();

    for (Iterator i = sourceModules.values().iterator(); i.hasNext();) {

      SourceModule module = (SourceModule) i.next();

      String[] moduleData = new String[6];
      moduleData[0] = module.getName();

      try {
        moduleData[1] = "(N/A)"; // Version as specified in manifest (for STATIC modules)
        moduleData[2] = "(N/A)"; // Newest version as available on the server
        if (module.hasVersion()) {
          moduleData[1] = module.getVersionAsString();
          moduleData[2] = "(" + (CVSVersionExtractor.getInstance().getLastVersion(module)).getVersionNumber() + ")";
        } else {
          Version localVersion = (CVSVersionExtractor.getInstance().getLocalVersion(manifest, module));
          moduleData[1] = (localVersion == null ? "   " : localVersion.getVersionNumber());
          moduleData[2] = "(" + (CVSVersionExtractor.getInstance().getLastVersion(module)).getVersionNumber() + ")";
        }

      } catch (VersionControlException v) {
        // Version for the module is non-existing in the repository.
        //
        throw new CommandException(v.getErrorCode(), v.getMessageArguments());
      }
      moduleData[3] = (module.getDevelopmentLine() == null ? "N/A" : module.getDevelopmentLine().getName());
//      moduleData[4] = manifest.getLocalState(module).toString();
      moduleData[4] = module.getStateAsString();
      moduleData[5] = module.getLocation().getId();

      renderedList.add(moduleData);
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
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
