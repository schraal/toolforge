package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.Module;

/**
 * This interface defines methods for runner classes that perform actions on a physical version control system.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public interface Runner {

	/**
	 * Checks out a module from a version control system.
	 *
	 * @param module
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse checkout(Module module);

	/**
	 * Updates an already checked out module on a user's harddisk.
	 *
	 * @param module
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse update(Module module);

	/**
	 * Commits a change to <code>file</code> to the version control system.
	 *
	 * @param file The file that should be committed.
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse commit(ManagedFile file);

	/**
	 * Commits all changes on <code>ManagedFile</code> instances for this
	 * <code>module</code>.
	 *
	 * @param module The module for which all changes should be committed.
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse commit(Module module);

}
