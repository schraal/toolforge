/*
Karma launcher - Library for launching a clean classloader from a Java application
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
package nl.toolforge.karma.launcher;

/**
 *
 * @author W.M. Oosterom
 */
final class ByteUtil {
    private static char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private ByteUtil() {
        // Empty
    }

    /**
     * This method will convert a byte array into a string representation in
     * hexadecimal format The length of the string returned is twice the size of
     * the byte array given as an argument.
     */
    public static String toHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < b.length; i++) {
            hexString.append(HEX_CHARS[(b[i] >>> 4) & 0x0f]);
            hexString.append(HEX_CHARS[b[i] & 0x0f]);
        }

        return hexString.toString();
    }
}