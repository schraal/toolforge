package nl.toolforge.karma.core.vc.cvs;

import java.util.Set;
import java.util.HashSet;
import java.io.File;

/**
 * <p>An object that can be passed to the CVSResponseAdapter that captures specific events during a
 * <code>cvs update</code> operation. This class can then be queried for specific entries matching specific criteria
 * (such as new files ('?'), etc.).
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class UpdateParser {

  private Set newFiles = new HashSet();

  public UpdateParser() {}

  /**
   * Returns a set of <code>File</code>s, mapping to new files (not yet in the CVS repository). These are
   * identified by <code>?</code>s at the beginning of an CVS output line after a <code>cvs update</code>.
   *
   * @return
   */
  public Set getNewFiles() {
    return newFiles;
  }

  public void addNewFile(String fileRef) {
    newFiles.add(new File(fileRef));
  }

  public void addNewFileByResponseLine(String responseLine) {
    // Parse the response line
    //
    addNewFile(responseLine);
  }
}
