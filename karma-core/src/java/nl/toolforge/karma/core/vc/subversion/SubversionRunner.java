package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSException;

import java.io.File;

/**
 * // TODO Lots of todo's, subversion implementation
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public final class SubversionRunner implements Runner {

  public SubversionRunner(Location location) throws CVSException {

  }

  public void setCommandResponse(CommandResponse response) {

  }

  public void create(Module module, String comment, ModuleLayoutTemplate template) throws VersionControlException {

  }

  public void add(Module module, File[] files, File[] dirs) throws VersionControlException {

  }

  public void add(Module module, String[] files, String[] dirs) throws VersionControlException {

  }

  public void checkout(Module module) throws VersionControlException {

  }

  public void checkout(Module module, Version version) throws VersionControlException {

  }

  public void checkout(Module module, DevelopmentLine developmentLine, Version version) throws VersionControlException {

  }

  public void update(Module module) throws VersionControlException {

  }

  public void update(Module module, Version version) throws VersionControlException {

  }

  public void promote(Module module, String comment, Version version) throws VersionControlException {

  }

  public boolean existsInRepository(Module module) {
    return false;
  }

  public boolean hasPatchLine(Module module) {
    return false;
  }

  public void createPatchLine(Module module) throws VersionControlException {

  }
}