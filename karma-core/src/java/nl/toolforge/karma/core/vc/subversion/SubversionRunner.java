package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSException;

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

  public void create(Module module) {
//		return null;
	}

	public void add(Module module, String fileName) throws SVNException {
//		return null;
	}

	public CommandResponse execute(Command command) {
		return null;
	}

	public void checkout(Module module) throws VersionControlException {
//		return checkout(module, null);
	}
	//  public CommandResponse checkout(Module module) throws SVNException {
//    return checkout(module, null);
//  }

	public void checkout(Module module, Version version) throws SVNException {
//		return null;
	}

	public void update(Module module) throws SVNException {
//		return update(module, null);
	}

	public void update(Module module, Version version) throws SVNException {
//		return null;
	}

	public void commit(ManagedFile file, String message) {
//		return null;
	}

	public void commit(Module module, String message) {
//		return null;
	}

	public void branch(Module module, SymbolicName branch) {
//		return null;
	}

	public void tag(Module module, SymbolicName tag) {
//		return null;
	}

	public void tag(Module module, Version version) {
//		return null;
	}
}