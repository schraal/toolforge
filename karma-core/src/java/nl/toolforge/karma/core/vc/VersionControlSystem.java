package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Module;

/**
 * A reference for a VCS (Version Control System). Everybody knows what a version
 * control system is (otherwise you are not entitled to use this codebase anyway ...),
 * so I'll stick to this message as a documentation snippet for this interface.
 *
 * @author D.A. Smedes
 */
public interface VersionControlSystem {

    /**
     * Checks out a module from a version control system.
     *
     * @param module
     */
    public void checkout(Module module);

    /**
     * Updates an already checked out module on a user's harddisk.
     *
     * @param module
     */
    public void update(Module module);

    /**
     * Commits a change to <code>file</code> to the version control system.
     *
     * @param file The file that should be committed.
     */
    public void commit(ManagedFile file);

    /**
     * Commits all changes on <code>ManagedFile</code> instances for this
     * <code>module</code>.
     *
     * @param module The module for which all changes should be committed.
     *
     * @see ManagedFile
     */
    public void commit(Module module);
}
