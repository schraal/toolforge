package nl.toolforge.karma.launcher;

/**
 *
 * @author W.M. Oosterom
 */
final class ByteUtil
{
    private static char[] HEX_CHARS = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    

    private ByteUtil() {
	// Empty
    }

    /**
     * This method will convert a byte array into
     * a string representation in hexadecimal format
     * The length of the string returned is twice
     * the size of the byte array given as an argument.
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
