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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author W.M. Oosterom
 */
final class HttpUtil {
    private final static int BUFFER_SIZE = 1024;

    private HttpUtil() {
        // Empty
    }

    /**
     * Fetch the contents of a URL and store it in a File.
     * 
     * @return A string representation of the MD5 MessageDigest of the url
     *         retrieved, or
     */
    static String get(URL url, File dest) throws IOException {
        String digestString = null;

        HttpURLConnection connection = null;
        File tmpFile = null;

        InputStream in = null;
        OutputStream out = null;

        try {
            // We use a temporary file to write the
            // incomming data to.
            //
            tmpFile = File.createTempFile("HttpUtil", null);
            out = new FileOutputStream(tmpFile);

            // Open the connection to the remote resource and check
            // for a correct response code
            //
            connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(
                        "Failed to retrieve remote file (HTTP response code = "
                                + connection.getResponseCode() + ")");
            }

            in = connection.getInputStream();

            // Now fetch the data and store it.
            //
            byte[] buffer = new byte[BUFFER_SIZE];
            MessageDigest md = MessageDigest.getInstance("MD5");

            int i = -1;
            int n = 0;
            boolean headerIsPrinted = false;

            while ((i = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, i);
                md.update(buffer, 0, i);

                n += i;
                if (n >= BUFFER_SIZE) {
                    if (!headerIsPrinted) {
                        System.out.print("Progress (in blocks of "
                                + BUFFER_SIZE + " bytes):");
                        headerIsPrinted = true;
                    }
                    System.out.print(".");
                    n -= BUFFER_SIZE;
                }
            }

            if (headerIsPrinted) {
                System.out.println();
            }

            out.flush();
            byte[] digest = md.digest();

            // Copy the file, if it fails, we try to delete
            // the destination file.
            //
            try {
                FileUtil.copy(tmpFile, dest);
            } catch (IOException e) {
                if (dest != null) {
                    dest.delete();
                }
                throw e;
            }

            return ByteUtil.toHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            // This one is not expected to occur, we just ask for a simple
            // MD5 message digest. I do not want to trouble the API with
            // this exception, so throw a runtime
            //
            throw new RuntimeException(e.getMessage());
        } finally {
            // Try to cleanup all resources
            //
            connection.disconnect();

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
}