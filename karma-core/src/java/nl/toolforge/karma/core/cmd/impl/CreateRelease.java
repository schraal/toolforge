package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.LocalEnvironment;

import java.io.File;

/**
 * Creates a release manifest, based on the current (development) manifest, by checking all the latest versions
 * of modules and adding them
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreateRelease extends DefaultCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateRelease(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String manifestName = getCommandLine().getOptionValue("m");

    if (manifestName == null) {

      // The option '-m' is optional, so we use the current manifest
      //
      if (!getContext().isManifestLoaded()) {
        throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
      }

      Manifest currentManifest = getContext().getCurrentManifest();

      if (!currentManifest.getType().equals(Manifest.DEVELOPMENT_MANIFEST)) {
        throw new CommandException(ManifestException.NOT_A_DEVELOPMENT_MANIFEST, new Object[] {});
      }

//      String fileName =

//      File releaseManifestFile = new File(LocalEnvironment.getManifestStore();





    }


//    } catch (ManifestException m) {
//      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
//    }

    // Check if the current manifest is a release manifest
    //

    // Collect a structure of all modules with their latest versions (snapshot at this time).
    //

    // Create an XML document, representing the released manifest

    // The release manifest cannot have the same name as any other manifest in the manifest-store.
    //

    // Included manifests :
    //
    // - if the included manifest

  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
