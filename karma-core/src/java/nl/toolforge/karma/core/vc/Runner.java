package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

import java.io.File;

/**
 * Runner classes are adapters to native commands on a version control system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Runner {

  public void setCommandResponse(CommandResponse response);

	/**
	 * Creates a module in a version control repository, including the directory structure and files as defined by
   * <code>template</code>. The <code>comment</code> is added as a
   * {@link nl.toolforge.karma.core.history.ModuleHistoryEvent}.
   *
   * @throws VersionControlException related to version control operations.
	 */
	public void create(Module module, String comment, ModuleLayoutTemplate template) throws VersionControlException;

	/**
	 * Adds a set of files and/or a set of directories (recursively) to the version control system. Files and directories
   * will be created when they don't exist.
	 *
	 * @param module The module to which the files apply.
	 * @param files The filenames that should be added to the version control system repository.
	 * @param dirs The directory-paths that should be added to the version control system repository.
   *
   * @throws VersionControlException related to version control operations.
	 */
  public void add(Module module, File[] files, File[] dirs) throws VersionControlException;

  /**
   * See {@link #add(Module, File[], File[])}. This method converts the <code>String</code> items to <code>File</code>
   * items.
   *
   * @param module The context module to which the files should be added.
   * @param files Files to be added (each <code>String</code> will be added relative to the module base directory).
   * @param dirs Directories to be added (each <code>String</code> will be added relative to the module base directory).
   *
   * @throws VersionControlException Exceptions related to version control operations.
   */
  public void add(Module module, String[] files, String[] dirs) throws VersionControlException;

  /**
	 * Checks out a module from a version control system.
	 *
	 * @param module The module that should be checked out of the version control repository.
   *
   * @throws VersionControlException Exceptions related to version control operations.
	 */
	public void checkout(Module module) throws VersionControlException;

	/**
	 * Checks out a module from a version control system with the specified <code>version</code>. The module is checked
   * out relative to {@link nl.toolforge.karma.core.manifest.Manifest#getDirectory()}.
	 *
	 * @param module The module that should be checked out from the version control system.
	 * @param version The version of the module that should be checked out.
   *
   * @throws VersionControlException Exceptions related to version control operations.
	 */
	public void checkout(Module module, Version version) throws VersionControlException;

	/**
	 * Checks out a module from a version control system with the specified <code>version</code> and from a development
   * line. The module is checked out relative to {@link nl.toolforge.karma.core.manifest.Manifest#getDirectory()}.
	 *
	 * @param module The module that should be checked out from the version control system.
   * @param developmentLine The development line for the module.
	 * @param version The version of the module that should be checked out.
   *
   * @throws VersionControlException Exceptions related to version control operations.
	 */
  public void checkout(Module module, DevelopmentLine developmentLine, Version version) throws VersionControlException;

	/**
	 * Updates an already checked out module.
	 *
	 * @param module The module that should be updated.
   *
   * @throws VersionControlException Exceptions related to version control operations.
	 */
	public void update(Module module) throws VersionControlException;

	/**
	 * Updates an already checked out module to a specified <code>version</code>.
	 *
	 * @param module The module that should be updated.
	 * @param version The version to which the the module should be updated,
   *
   * @throws VersionControlException Exceptions related to version control operations.
	 */
	public void update(Module module, Version version) throws VersionControlException;

  /**
   *
   * @param module
   * @param comment  Comment of the developer
   * @param version
   * @throws VersionControlException
   */
	public void promote(Module module, String comment, Version version) throws VersionControlException;

  /**
   * Checks if a module exists in the repository. The module should contain the <code>module.info</code> file.
   * @param module
   * @return <code>true</code> if the module exists, <code>false</code> otherwise.
   */
  // todo some of the methods could be moved to a helper class that performs checks like existsInRepository. Much clearer and nicer.
  public boolean existsInRepository(Module module);

  /**
   * Checks if the module has a <code>PatchLine</code> in the version control system.
   * @param module
   * @return
   */
  public boolean hasPatchLine(Module module);

  /**
   * Creates a <code>PatchLine<code> for the module.
   */
  public void createPatchLine(Module module) throws VersionControlException ;
}
