package nl.toolforge.karma.core.vc;

/**
 * A <code>PatchLine</code> is a special type of <code>DevelopmentLine</code>, used when a module has been released to
 * (for example) the test department
 *
 * @author D.A. Smedes, Doubleforge
 * @version $Id$
 */
public class PatchLine extends DevelopmentLine {

  /**
   * Name prefix for the symbolic name that is applied to the module when a patchline is created. A symbolic name
   * would look like this : <code>PATCHLINE-core-karma_0-2</code>, indicating that a patchline is created for the
   * <code>core-karma</code> module, version <code>0-2</code>.
   */
  public static final String NAME_PREFIX = "PATCHLINE";

  public PatchLine(String lineName) {
    super(lineName);
  }

}
