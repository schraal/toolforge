package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleComparator;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    Manifest manifest = getContext().getCurrentManifest();

    List sourceModules = new ArrayList();

    // Transform and sort
    //

    // todo hmm kan dit niet simpeler ?
    //
    Collection c = manifest.getAllModules().values();

    for (Iterator i = c.iterator(); i.hasNext();) {
      sourceModules.add(i.next());
    }

    Collections.sort(sourceModules, new ModuleComparator());


    for (Iterator i = sourceModules.iterator(); i.hasNext();) {

      SourceModule module = (SourceModule) i.next();

      String[] moduleData = new String[7];
      moduleData[0] = module.getName();

      boolean existsInRepository = false;
      try {
        Runner runner = RunnerFactory.getRunner(module.getLocation(), new File(""));
        existsInRepository = runner.existsInRepository(module);
      } catch (VersionControlException v) {
        // Version for the module is non-existing in the repository.
        //
        throw new CommandException(v.getErrorCode(), v.getMessageArguments());
      }

      try {

        if (module.getState().equals(Module.WORKING)) {
          moduleData[1] = "   ";
        } else {
          Version localVersion = (CVSVersionExtractor.getInstance().getLocalVersion(manifest, module));
          moduleData[1] = (localVersion == null ? "   " : localVersion.getVersionNumber());
        }

        if (existsInRepository) {
          moduleData[2] = "(" + (CVSVersionExtractor.getInstance().getLastVersion(module)).getVersionNumber() + ")";
        } else {
          moduleData[2] = "";
        }

      } catch (VersionControlException v) {
        // Version for the module is non-existing in the repository.
        //
        throw new CommandException(v.getErrorCode(), v.getMessageArguments());
      }

      if (module.getState().equals(Module.STATIC)) {
        moduleData[3] = "(" + module.getVersionAsString() + ")";
      } else {
        moduleData[3] = "";
      }
      moduleData[4] = (module.hasPatchLine() ? "!!!" : "");

      if (existsInRepository) {
        moduleData[5] = module.getStateAsString();
        moduleData[6] = module.getLocation().getId();
      } else {
        moduleData[5] = "";
        moduleData[6] = "** Not in repository **";
      }
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
