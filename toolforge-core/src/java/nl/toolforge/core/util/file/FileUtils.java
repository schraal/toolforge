package nl.toolforge.core.util.file;

import java.io.IOException;
import java.io.File;


/**
 * File utilities.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class FileUtils {

  /**
   * Removes a file or directory at this location.
   *
   * @param file A <code>String</code> representation of the file or directory
   */
  public static void delete(String file) throws IOException {
    delete(new File(file));
  }

  /**
   * Deletes the file or directory corresponding to this <code>File</code> object. This one does a recursive removal.
   *
   * @param file a <code>File</code> to delete, either a file or dir
   * @return If the delete operation was not succesfull.
   */
  public static boolean delete(File file) {

    if ((file == null) || (!file.exists())) {
      return false;
    }

    // If directory contains files or directories, recurse
    //
    if (file.isDirectory()) {
      File[] list = file.listFiles();
      File f;
      if (list!=null) {
        for (int i = 0; i < list.length; i++) {
          f = list[i];
          if (f.isDirectory())
            return delete(f);
          else if (!f.delete())
            return false;
        }
      }
    }
    // Delete file or dir itself
    //
    return file.delete();
  }
}
