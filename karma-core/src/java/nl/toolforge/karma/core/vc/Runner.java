package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

/**
 * This interface defines methods for runner classes that perform actions on a physical version control system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Runner {

	// TODO runner.log should be defined in this interface. Right now, only impl. is in CVSRUnner.

  public void setCommandResponse(CommandResponse response);

	/**
	 * Creates a module in a version control repository.
	 */
	public void create(Module module, ModuleLayoutTemplate template) throws VersionControlException;

	/**
	 * Adds a file to the version control system. If the file does not exist, the file will be created.
	 *
	 * @param module   The module that contains the file (or will contain the file).
	 * @param files The filenames that should be added to the version control system repository.
	 * @param dirs The directory-paths that should be added to the version control system repository.
	 */
	public void add(Module module, String[] files, String[] dirs) throws VersionControlException;

  /**
	 * Checks out a module from a version control system.
	 *
	 * @param module
	 * @throws VersionControlException When the module does not exist in the repository.
	 */
	public void checkout(Module module) throws VersionControlException;

	/**
	 * Checks out a module from a version control system.
	 *
	 * @param module
	 * @param version
	 * @throws VersionControlException When the module does not exist in the repository or when the symbolic name is
	 *                                 not attached to the module in the repository.
	 */
	public void checkout(Module module, Version version) throws VersionControlException;


	/**
	 * Updates an already checked out module on a user's harddisk.
	 *
	 * @param module
	 * @throws VersionControlException When an error occurred executing the command on the repository.
	 */
	public void update(Module module) throws VersionControlException;

	/**
	 * Updates an already checked out module on a user's harddisk.
	 *
	 * @param module
	 * @param version
	 * @throws VersionControlException When the module does not exist in the repository or when the symbolic name is
	 *                                 not attached to the module in the repository.
	 */
	public void update(Module module, Version version) throws VersionControlException;

	/**
	 * Commits a change to <code>file</code> to the version control system.
	 *
	 * @param file    The file that should be committed.
	 * @param message The commit message for the file.
	 */
	public void commit(ManagedFile file, String message) throws VersionControlException;

	/**
	 * Commits all changes on <code>ManagedFile</code> instances for this
	 * <code>module</code>.
	 *
	 * @param module  The module for which all changes should be committed.
	 * @param message The commit message for the module..
	 */
	public void commit(Module module, String message) throws VersionControlException;

	/**
	 * Creates a branch on the module.
	 *
	 * @param module
	 * @param branch A symbolic name, representing the branch name.
	 */
	public void branch(Module module, SymbolicName branch) throws VersionControlException;

	public void promote(Module module, Version version) throws VersionControlException;

  /**
   * Checks if a module exists in the repository. The module should contain the <code>module.info</code> file.
   * @param module
   * @return <code>true</code> if the module exists, <code>false</code> otherwise.
   */
  // todo some of the methods could be moved to a helper class that performs checks like existsInRepository. Much clearer and nicer.
  public boolean existsInRepository(Module module);

}
