package nl.toolforge.karma.console;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.PasswordScrambler;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.AuthenticatorKey;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.cvsimpl.CVSException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Configures a WorkingContext and writes the configuration to <code>working-context.xml</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
final class Configurator {

  /**
   * Checks the state of the configuration for the working context, and will ask the user to complete it when required.
   *
   * @param ctx The current working context.
   */
  public void checkConfiguration(WorkingContext ctx) {

    try {

      checkMandatoryProperties(ctx);

      checkManifestStoreConfiguration(ctx);
      checkLocationStoreConfiguration(ctx);

    } catch (IOException e) {
      writeln("[ console ] Error reading configuration. Please inform the Karma support desk.");
      System.exit(1);
    }

    try {
      ctx.getConfiguration().store();
    } catch (IOException e) {
      write("[ console ] Error writing `working-context.xml`.");
      System.exit(1);
    }
  }

  private void checkMandatoryProperties(WorkingContext ctx) throws IOException {

    final String DEFAULT_PROJECT_BASE_DIR =
        System.getProperty("user.home") + File.separator + "karma" + File.separator + ctx.getName();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    boolean ok = false;

    writeln("");

    while (!ok) {

      String newValue = null;

      String projectBaseDir = ctx.getConfiguration().getProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY);

      if (projectBaseDir != null) {
        ok = true;
        break;
      }

      projectBaseDir = (projectBaseDir == null ? DEFAULT_PROJECT_BASE_DIR : projectBaseDir);

      write("[ console ] What is your project base directory ("+projectBaseDir+") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        projectBaseDir = newValue;
      }
      ctx.getConfiguration().setProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY, projectBaseDir);

      ok = true;
    }
  }


  private void checkManifestStoreConfiguration(WorkingContext ctx) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    boolean ok = false;

    writeln("");

    // <location type="" id="manifest-store">
    //
    CVSRepository cvs = null;
    try {
      cvs = (CVSRepository) ctx.getManifestStoreLocation();
      if (cvs == null) {
        cvs = new CVSRepository("manifest-store");
      }
    } catch (ClassCastException c) {
      write("[ console ] Sorry, only CVS is supported in this release.");
      cvs = new CVSRepository("manifest-store");
    }

    cvs.setWorkingContext(ctx);

    // todo to be extended. CVS only for now.

    int retries = 0;

    while (!ok) {

      if (retries == 2) {
        writeln("[ console ] Configuration of the manifest store failed. Please call in some support.");
        return;
      }

      try {

        if (CVSRepository.PSERVER.equals(cvs.getProtocol())) {
          cvs.connect();

          ctx.getConfiguration().setManifestStore(cvs);

          ok = true;
          break;
        } else {
          try {
            cvs.getCVSRoot();

            // Connection data seems to be ok, nevertheless, we'll ask the user to accept it.
            //
//            write("[ console ] CVSROOT for the manifest store is : '" + cvsRoot + "'. Accept ? [Y|N] (Y) : ");
//            String check = reader.readLine().toUpperCase();
//            if ("Y".equals(check)) {
              // If the above succeeds we are through, otherwise, we ask the user for configuration.
              //
              ctx.getConfiguration().setManifestStore(cvs);

              ok = true;
              break;
//            }
          } catch (CVSException e) {}
        }
      } catch (LocationException e) {
        writeln("[ console ] Manifest store configuration invalid. " + e.getMessage());
        retries++;
      }

      if (retries != 0) {
        writeln("\n[ console ] Try again ...");
      }

      writeln("\n-------------------------------------------------");
      writeln("[ console ] Please configure the manifest-store !");
      writeln("-------------------------------------------------");

      // For Karma R1.0, we know its a CVS repo.
      //

      String newValue = null;

      // protocol
      //
      String protocol = cvs.getProtocol();
      protocol = (protocol == null ? "pserver" : protocol);

      write("[ console ] What is your server protocol ? [ local | pserver ] ("+protocol+") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        protocol = newValue;
      }
      cvs.setProtocol(protocol);

      if (protocol.equals(CVSRepository.PSERVER)) {

        // Host
        //
        String hostName = cvs.getHost();
        hostName = (hostName == null ? "127.0.0.1" : hostName);

        write("[ console ] What is your server's hostname or ip-adres ? ("+hostName+") : ");
        newValue = reader.readLine().trim();
        if (!"".equals(newValue)) {
          hostName = newValue;
        }
        cvs.setHost(hostName);

        // Port
        //
        String port = (cvs.getPort() == -1 ? "2401" : "" + cvs.getPort());

        write("[ console ] What is your server port ? ("+port+") : ");
        newValue = reader.readLine().trim();
        if (!"".equals(newValue)) {
          port = newValue;
        }
        cvs.setPort(("".equals(port) ? "2401" : port));
      }

      // Repository
      //
      String repository = cvs.getRepository();
      repository = (repository == null ? "/home/cvs" : repository);

      write("[ console ] What is your server repository ? ("+repository+") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        repository = newValue;
      }
      cvs.setRepository(repository);

      // manifest-store.module
      //
      String manifestStoreModule = ctx.getManifestStoreModule();
      manifestStoreModule = (manifestStoreModule == null ? "manifests" : manifestStoreModule);

      write("[ console ] What is the cvs module for the manifest store ? ("+manifestStoreModule+") ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        manifestStoreModule = newValue;
      }
      ctx.getConfiguration().setProperty(WorkingContext.MANIFEST_STORE_MODULE, manifestStoreModule);

      checkAuthentication(ctx, cvs);

    }
  }

  private void checkLocationStoreConfiguration(WorkingContext ctx) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    boolean ok = false;

    writeln("");

    // <location type="" id="manifest-store">
    //
    CVSRepository cvs = null;
    try {
      cvs = (CVSRepository) ctx.getLocationStoreLocation();
      if (cvs == null) {
        cvs = new CVSRepository("location-store");
      }
    } catch (ClassCastException c) {
      write("[ console ] Sorry, only CVS is supported in this release.");
      cvs = new CVSRepository("location-store");
    }

    cvs.setWorkingContext(ctx);

    // todo to be extended. CVS only for now.

    int retries = 0;

    while (!ok) {

      if (retries == 2) {
        writeln("[ console ] Configuration of the location store failed. Please call in some support.");
        return;
      }

      try {
        if (CVSRepository.PSERVER.equals(cvs.getProtocol())) {
          cvs.connect();

          ctx.getConfiguration().setLocationStore(cvs);

          ok = true;
          break;
        } else {
          try {
            cvs.getCVSRoot();

            // If the above succeeds ...
            //
            ctx.getConfiguration().setLocationStore(cvs);

            ok = true;
            break;

          } catch (CVSException e) {
          }
        }
      } catch (LocationException e) {
        writeln("[ console ] Location store configuration invalid. " + e.getMessage());
        retries++;
      }

      if (retries != 0) {
        writeln("\n[ console ] Try again ...");
      }

      writeln("\n-------------------------------------------------");
      writeln("[ console ] Please configure the location-store !");
      writeln("-------------------------------------------------");

      // For Karma R1.0, we know its a CVS repo.
      //

      String newValue = null;

      // protocol
      //
      String protocol = cvs.getProtocol();
      protocol = (protocol == null ? "pserver" : protocol);

      write("[ console ] What is your server protocol ? [ local | pserver ] ("+protocol+") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        protocol = newValue;
      }
      cvs.setProtocol(protocol);

      if (protocol.equals(CVSRepository.PSERVER)) {

        // Host
        //
        String hostName = cvs.getHost();
        hostName = (hostName == null ? "127.0.0.1" : hostName);

        write("[ console ] What is your server's hostname or ip-adres ? ("+hostName+") : ");
        newValue = reader.readLine().trim();
        if (!"".equals(newValue)) {
          hostName = newValue;
        }
        cvs.setHost(hostName);

        // Port
        //
        String port = (cvs.getPort() == -1 ? "2401" : "" + cvs.getPort());

        write("[ console ] What is your server port ? ("+port+") : ");
        newValue = reader.readLine().trim();
        if (!"".equals(newValue)) {
          port = newValue;
        }
        cvs.setPort(("".equals(port) ? "2401" : port));
      }

      // Repository
      //
      String repository = cvs.getRepository();
      repository = (repository == null ? "/home/cvs" : repository);

      write("[ console ] What is your server repository ? ("+repository+") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        repository = newValue;
      }
      cvs.setRepository(repository);

      // manifest-store.module
      //
      String locationStoreModule = ctx.getLocationStoreModule();
      locationStoreModule = (locationStoreModule == null ? "locations" : locationStoreModule);

      write("[ console ] What is the cvs module for the manifest store ? ("+locationStoreModule+") ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        locationStoreModule = newValue;
      }
      ctx.getConfiguration().setProperty(WorkingContext.LOCATION_STORE_MODULE, locationStoreModule);

      checkAuthentication(ctx, cvs);
    }
  }

  private void checkAuthentication(WorkingContext ctx, VersionControlSystem cvs) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    Authenticator authenticator = null;
    try {
      authenticator = Authenticators.getAuthenticator(new AuthenticatorKey(ctx.getName(), cvs.getId()));
    } catch (AuthenticationException e) {
      authenticator = new Authenticator();
    }

    authenticator.setWorkingContext(ctx.getName());
    authenticator.setId(cvs.getId());

    String userNameString = (authenticator.getUsername() == null ? "" : "(" + authenticator.getUsername() + ")");
    String password = "";

    String newValue = "";

    while ("".equals(newValue)) {
      write("[ console ] What is your username ? " + userNameString + " : ");
      newValue = reader.readLine().trim();
      newValue = ("".equals(newValue) ? authenticator.getUsername() : newValue);
      authenticator.setUsername(newValue);
    }

    if (cvs.getProtocol().equals(CVSRepository.PSERVER)) {
      writeln("[ console ] Password is required, but input is in text-mode. Be warned !");
      write("[ console ] What is your password ? : ");
      password = reader.readLine().trim();
      password = ("".equals(password) ? "" : PasswordScrambler.scramble(password));
    }

    authenticator.setPassword(password);

    Authenticators.addAuthenticator(authenticator);
  }


  private void writeln(String text) {
    System.out.println(text);
  }

  private void write(String text) {
    System.out.print(text);
  }

}
