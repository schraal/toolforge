package nl.toolforge.karma.launcher;

import java.io.*;

/**
 * 
 * @author W.M. Oosterom
 */
final class FileUtil {
    private FileUtil() {
        // ignore
    }

    private static final int BUFFER_SIZE = 1024;

    static void copy(File src, File dest) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);

            byte[] buffer = new byte[BUFFER_SIZE];
            int i = -1;

            while ((i = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, i);
            }

            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}