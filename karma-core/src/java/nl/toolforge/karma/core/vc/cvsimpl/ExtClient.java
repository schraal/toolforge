package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.core.util.net.Ping;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.location.LocationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.event.CVSListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Quick hack to support the CVS <code>ext</code> protocol. This implementation uses the Ant task, not anything
 * native, unfortunately.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ExtClient {

  private CommandResponse response = new CommandResponse();

  public void setHandler(CommandResponseHandler handler) {
    response.addCommandResponseListener(handler);
  }

  private LogParser listener = null;

  public void runCommand(Command command, File contextDirectory, CVSRepository location) throws CVSException {

    if (listener == null) {
      throw new KarmaRuntimeException("No listener registered for this client.");
    }

    // todo hmm, this mechanism doesn't integrate with the commandresponse mechanism
    //
    listener.setOutputPrintStream(System.out);
    listener.setMessageOutputLevel(Project.MSG_INFO);

    // Configure underlying ant to run a command.
    //
    Project project = new Project();
    project.addBuildListener(listener);
    project.init();

    // The ping thingie doesn't work through a proxy.
    //

//    if (!Ping.ping(location.getHost(), 22, 3000)) {
//      throw new CVSException(LocationException.CONNECTION_EXCEPTION, new Object[]{location.getId()});
//    } else {

    ProjectHelperImpl helper = new ProjectHelperImpl();
    try {
      helper.parse(project, getBuildFile("ext-support.xml"));
    } catch (IOException e) {
      throw new KarmaRuntimeException("Cannot find ext-support.xml");
    }

    project.setProperty("cvsroot", location.getCVSRoot());
    project.setProperty("command", command.getCVSCommand());
    project.setProperty("dest", contextDirectory.getPath());

    try {
      project.executeTarget("run");
    } catch (BuildException e) {
      throw new CVSException(e, CommandException.BUILD_FAILED);
    }
//    }
  }

  private final File getBuildFile(String buildFile) throws IOException {

    File tmp = null;

    tmp = MyFileUtils.createTempDirectory();

    ClassLoader loader = this.getClass().getClassLoader();

    BufferedReader in =
        new BufferedReader(new InputStreamReader(loader.getResourceAsStream("ant" + File.separator + buildFile)));
    BufferedWriter out =
        new BufferedWriter(new FileWriter(new File(tmp, buildFile)));

    String str;
    while ((str = in.readLine()) != null) {
      out.write(str);
    }
    out.close();
    in.close();

    // Return a temp reference to the file
    //
    return new File(tmp, buildFile);
  }

  public void addCVSListener(LogParser listener) {
    this.listener = listener;
  }
}
