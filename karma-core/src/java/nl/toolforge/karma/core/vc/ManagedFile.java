package nl.toolforge.karma.core.vc;

/**
 * Instances of this type represent files that are being managed by a version control system. A checkout of a module
 * is combined with generated files and directories etc. due to code building and other file processing (temporary
 * files). <code>ManagedFile</code>s have a reference in a version control system. CVS for example uses a
 * <code>CVS</code> directory in each subdirectory of a sourcetree to maintain meta-information. Only
 * <code>ManagedFile</code>s can be committed to a version control system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface ManagedFile {
}
