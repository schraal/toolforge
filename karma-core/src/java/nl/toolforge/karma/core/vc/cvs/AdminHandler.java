package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Module;
import org.netbeans.lib.cvsclient.CVSRoot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper class to execute stuff on admin files in <code>CVS</code> directories.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class AdminHandler {

  public boolean isEqualLocation(Module module) {

    if (!module.getBaseDir().exists()) {
      // if the module has not been checked out, it is OK!
      //
      return true;
    }

    File rootFile = new File(module.getBaseDir(), "CVS/Root");

    String cvsRootString = null;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rootFile)));

      cvsRootString = in.readLine();
      in.close();

    } catch (FileNotFoundException e) {
      // We guess the user has created a module and not stored in a version control repository.
      //
      return false;
    } catch (IOException e) {
      throw new KarmaRuntimeException(e.getMessage());
    }

    CVSRoot cvsRoot = CVSRoot.parse(cvsRootString);

    CVSLocationImpl loc = (CVSLocationImpl) module.getLocation();
    try {
      return cvsRoot.toString().equals(loc.getCVSRootAsString());
    } catch (CVSException e) {
      return false;
    }
  }
}
