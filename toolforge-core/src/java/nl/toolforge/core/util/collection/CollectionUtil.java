package nl.toolforge.core.util.collection;

import java.util.Collection;
import java.util.Iterator;

/**
 * Some utils. I reckon this sort of thing already exists. Just too lazy to look for it now.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CollectionUtil {

  /**
   * Given a collection of <code>String</code> objects (determines runtime ...), this method returns a
   * <code>String</code>, separated by a separator character. After the last String in the collection, no separator
   * char is placed.
   *
   * @param collection
   * @return
   */
  public static String concat(Collection collection, char separatorChar) {

    String s = "";

    for (Iterator i = collection.iterator(); i.hasNext();) {

      s += (String) i.next();

      if (i.hasNext()) {
        s += separatorChar;
      }
    }
    return s;
  }

}
