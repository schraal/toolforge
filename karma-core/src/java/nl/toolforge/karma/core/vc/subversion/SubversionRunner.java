package nl.toolforge.karma.core.vc.subversion;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.cvs.CVSException;

import java.io.File;

/**
 * // TODO Lots of todo's, subversion implementation
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public final class SubversionRunner implements Runner {

	public SubversionRunner(Location location) throws CVSException {

	}

  public CommandResponse create(Module module, Location location) {
    return null;
  }

	public CommandResponse add(Module module, String fileName) {
		return null;
	}

	public CommandResponse execute(Command command) {
		return null;
	}

	public CommandResponse checkout(Module module, File checkoutDirectory) {
		return null;
	}

  public CommandResponse checkout(Module module) {
    return null;
  }

	public CommandResponse update(Module module) {
		return null;
	}

	public CommandResponse commit(ManagedFile file) {
		return null;
	}

	public CommandResponse commit(Module module) {
		return null;
	}

	public CommandResponse branch(Module module, SymbolicName branch) {
		return null;
	}

	public CommandResponse tag(Module module, SymbolicName tag) {
		return null;
	}
}