package nl.toolforge.karma.core;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class Patch extends Version {

  public static final String PATCH_POSTFIX = "-{1}\\d{1,4}";

  /**
   * Patches have the following format : <code>0-0-x</code>, where x is the actual patch number within the
   * <code>0-0</code> version. The full thing has to be provided.
   *
   * @param patchNumber
   */
  public Patch(String patchNumber) {
    super(patchNumber);
  }

  public String getPatternString() {
    return super.getPatternString() + PATCH_POSTFIX;
  }

}
