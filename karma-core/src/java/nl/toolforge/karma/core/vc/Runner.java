package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;

/**
 * This interface defines methods for runner classes that perform actions on a physical version control system.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public interface Runner {

	// TODO runner.log should be defined in this interface. Right now, only impl. is in CVSRUnner.

	/**
	 * Creates a module in a version control repository.
	 */
	public CommandResponse create(Module module) throws VersionControlException ;

	/**
	 * Adds a file to the version control system. If the file does not exists, the file will be created.
	 *
	 * @param module The module that contains the file (or will contain the file).
	 * @param fileName The filename of the file that should be added to the version control system repository.
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse add(Module module, String fileName) throws VersionControlException;

	/**
	 * Checks out a module from a version control system.
	 *
	 * @param module
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 * @throws VersionControlException When the module does not exist in the repository.
	 */
	public CommandResponse checkout(Module module) throws VersionControlException;

	/**
	 * Checks out a module from a version control system.
	 *
	 * @param module
	 * @param version
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 * @throws VersionControlException When the module does not exist in the repository or when the symbolic name is
	 *         not attached to the module in the repository.
	 */
	public CommandResponse checkout(Module module, Version version) throws VersionControlException;

	/**
	 * Updates an already checked out module on a user's harddisk.
	 *
	 * @param module
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 * @throws VersionControlException When an error occurred executing the command on the repository.
	 */
	public CommandResponse update(Module module) throws VersionControlException;

	/**
	 * Updates an already checked out module on a user's harddisk.
	 *
	 * @param module
	 * @param version
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 * @throws VersionControlException When the module does not exist in the repository or when the symbolic name is
	 *         not attached to the module in the repository.
	 */
	public CommandResponse update(Module module, Version version) throws VersionControlException;

	/**
	 * Commits a change to <code>file</code> to the version control system.
	 *
	 * @param file The file that should be committed.
	 * @param message The commit message for the file.
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse commit(ManagedFile file, String message) throws VersionControlException;

	/**
	 * Commits all changes on <code>ManagedFile</code> instances for this
	 * <code>module</code>.
	 *
	 * @param module The module for which all changes should be committed.
	 * @param message The commit message for the module..
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>.
	 */
	public CommandResponse commit(Module module, String message) throws VersionControlException;

	/**
	 * Creates a branch on the module.
	 *
	 * @param module
	 * @param branch A symbolic name, representing the branch name.
	 *
	 * @return Response from the version control system wrapped in a <code>CommandResponse</code>
	 */
	public CommandResponse branch(Module module, SymbolicName branch) throws VersionControlException;

	/**
	 * Tags a module with a symbolic name.
	 *
	 * @param module
	 * @param tag
	 * @return
	 */
	public CommandResponse tag(Module module, SymbolicName tag) throws VersionControlException;

}
