package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;

import java.util.Comparator;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleComparator implements Comparator {

  public int compare(Object o1, Object o2) {

    if ((!(o1 instanceof Module)) || (!(o1 instanceof Module))) {
      throw new KarmaRuntimeException("Don't compare apples with pears; they are not the same ... (use Module instances).");
    }

    String n1 = ((Module) o1).getName();
    String n2 = ((Module) o2).getName();

    return n1.compareTo(n2);
  }
}
