package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;

/**
 * A dependency builder processes all available dependencies given a module or a manifest and builds a classpath string
 * for compile purposes.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public interface DependencyBuilder {

  /**
   * Returns a dependency String (semi-colon-separated) based on the module or manifest supplied.
   *
   * @return
   * @throws KarmaException When the dependencies could not be determined.
   */
//  public String getDependencies() throws KarmaException;

}
