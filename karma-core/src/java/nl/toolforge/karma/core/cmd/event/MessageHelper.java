/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.cmd.event;



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
