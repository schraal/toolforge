package nl.toolforge.karma.core.manifest.util;



/**
 * <p>Layout for a module. A new modules can be initialized with new files and directories. Implementations of this
 * class define those files and directories.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface ModuleLayoutTemplate {

  public String[] getFileElements();

  public String[] getDirectoryElements();

}
