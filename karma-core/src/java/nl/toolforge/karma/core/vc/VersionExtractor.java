package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;

/**
 * <p>Modules in a version control system are manageable through their symbolic names (or tags). The tag describes the
 * actual version number of the module.
 *
 * <p>An implementation of this class will be configurable in <b>later</b> versions of Karma where one would be able to
 * write his own extractor based on the naming scheme applicable for the organization.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 *
 * @since 2.0
 */
public interface VersionExtractor {

  /**
   * <p>Given a modules' branch name, this method is able to determine the next version number that applies to this
   * module. For instance, if a module has a version <code>1-4</code> and is developed in branch <code>B1</code>, the
   * symbolic name would be <code>B1_1-4</code>. This method would then (
   *
   * <p>Note that this method does not take any thread-safety into account with respect to the CVS repository nor does
   * it do any transaction management. If two developers are trying to obtain a next version and both get
   * <code>1-4</code>, because they got there at the -more or less- the same moment, we (you) have a problem.
   *
   * @param module The module for which the next version should be obtained.
   *
   * @return A <code>String</code> representation of the version number that's next in line for the branch that the
   *         module is developed on.
   *
   * @throws KarmaException When the next version number could not be determined correctly. See error codes
   *         <code>CMD-</code>. TODO to be completed after implementation.
   */
  public String getNextVersion(Module module) throws KarmaException;

}
