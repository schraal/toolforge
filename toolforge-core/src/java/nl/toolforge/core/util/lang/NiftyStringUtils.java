package nl.toolforge.core.util.lang;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Extras to <code>org.apache.commons.lang.StringUtils</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class NiftyStringUtils {

  /**
   * Splits a <code>String</code> into an <code>Array</code> of <code>String</code>s. Each <code>String</code> is
   * wrapped maximum at position <code>wrapCount</code>, but taking into account the last position of
   * <code>wrapChar</code>.
   *
   * @param str
   * @param wrapString
   * @param wrapCount
   * @return
   */
  public static String[] split(String str, String wrapString, int wrapCount) {

    if (str == null || wrapString == null || wrapCount <= 1) {
      return new String[0];
    }

    List list = new ArrayList();

    while (str.length() > wrapCount) {

      String sub = str.substring(0, wrapCount);
      int pos = sub.lastIndexOf(wrapString);

      String substring = null;
      if (pos > 0) {
        substring = str.substring(0, pos);
        list.add(substring);

        str = str.substring(substring.length() + 1);
      } else {
        list.add(sub);

        str = str.substring(sub.length() + 1);
      }

    }

    // Add the last bit.
    //
    list.add(str);

    String[] ret = new String[list.size()];
    int i = 0;
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      ret[i] = (String) iter.next();
      i++;
    }
    return ret;
  }

  /**
   * Removes blocks of spaces, leaving one intact.
   *
   * @param str
   * @return
   */
  public static String deleteWhiteSpaceExceptOne(String str) {

    String newString = "";

    char[] c = str.toCharArray();

    int i = 0;
    while (i < c.length) {

      if (Character.isWhitespace(c[i])) {
        while (Character.isWhitespace(c[i])) {
          i++;
        }
        newString = newString + " ";
      } else {
        newString = newString + c[i];
        i++;
      }
    }

    return newString;

  }
}
