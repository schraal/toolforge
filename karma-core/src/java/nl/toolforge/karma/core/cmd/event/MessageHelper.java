package nl.toolforge.karma.core.cmd.event;

import org.apache.commons.lang.StringUtils;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class MessageHelper {

//  private static int LEFT_COLUMN_SIZE = 20;

  /**
   * Return an 'Ant'-like formatting for the messagetext. <code>label</code> and <code>message</code> are returned like
   * this : <code>&lt;n-spaces&gt;[ &lt;label&gt; ] &lt;message&gt;</code>
   *
   * @param label   Some label
   * @param message The actual message
   * @return
   */
  public static String format(String label, String message) {

//    int size = (label.length() < LEFT_COLUMN_SIZE ? LEFT_COLUMN_SIZE - label.length() : 0);

    StringBuffer buffer = new StringBuffer();
//    buffer.append(StringUtils.repeat(" ", size));
    buffer.append("[ ").append(label).append(" ] ");
    buffer.append(message);

    return buffer.toString();
  }
}
