package nl.toolforge.karma.console;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.boot.LocationStore;
import nl.toolforge.karma.core.boot.ManifestStore;
import nl.toolforge.karma.core.boot.Store;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import nl.toolforge.karma.core.boot.WorkingContextException;
import nl.toolforge.karma.core.location.PasswordScrambler;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.AuthenticatorKey;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Configures a <code>WorkingContext</code> and writes the configuration to <code>working-context.xml</code>. Required
 * input will be asked through <code>System.in</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
final class Configurator {

  private Log logger = LogFactory.getLog(Configurator.class);

  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  private WorkingContext context = null;
  private WorkingContextConfiguration config = null;

  /**
   * Constructs a <code>Configurator</code> for <code>workingContext</code>.
   *
   * @param config The <code>WorkingContextConfiguration</code> to configure.
   */
  public Configurator(WorkingContext context, WorkingContextConfiguration config) {

    this.config = config;
    this.context = context;

  }

  /**
   * Configures. When this method is finished, it returns silently. If something major has happened,
   * <code>System.exit(1)</code> will have been called.
   */
  public void checkConfiguration() {

    ManifestStore manifestStore = new ManifestStore(context);
    LocationStore locationStore = new LocationStore(context);

    boolean mChecked = false;

    try {
      checkMandatoryProperties();

      // We need some of these properties later on already.
      //
      context.configure(config);

      mChecked = checkManifestStoreConfiguration(manifestStore);

    } catch (IOException e) {
      logger.error(e);
      writeln("[ configurator ] Error configurating working context : " + e.getMessage());
      System.exit(1);
    }

    if (mChecked) {
      config.setProperty(WorkingContext.MANIFEST_STORE_MODULE, manifestStore.getModuleName());
      config.setManifestStoreLocation(manifestStore.getLocation());
      context.setManifestStore(manifestStore);

      try {

        boolean lChecked = checkLocationStoreConfiguration(locationStore);

        if (lChecked) {
          config.setProperty(WorkingContext.LOCATION_STORE_MODULE, locationStore.getModuleName());
          config.setLocationStoreLocation(locationStore.getLocation());
          context.setLocationStore(locationStore);
        } else {
          logger.error("Could not configure location store.");
          writeln("[ configurator ] Failed to configure the working context properly. Contact your administrator");
          System.exit(1);
        }
      } catch (IOException e) {
        logger.error(e);
        writeln("[ configurator ] Error configurating working context : " + e.getMessage());
        System.exit(1);
      }
    } else {
      logger.error("Could not configure manifest store.");
      writeln("[ configurator ] Failed to configure the working context properly. Contact your administrator");
      System.exit(1);
    }

    try {

      config.store();
    } catch (WorkingContextException e) {
      logger.error("[ configurator ] Failed to write configuration to `working-context.xml`.");
      System.exit(1);
    }

  }

  private void checkMandatoryProperties() throws IOException {

    final String DEFAULT_PROJECT_BASE_DIR =
        System.getProperty("user.home") + File.separator + "karma" + File.separator + context.getName();

    boolean ok = false;

    writeln("");

    while (!ok) {

      String newValue = null;

      String projectBaseDir = config.getProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY);

      // Does project.basedir exists as a property ?
      //
      if (projectBaseDir != null) {

        if (new File(projectBaseDir).canWrite()) {
          ok = true;
          break;
        } else {
          write("[ configurator-error ] '" + projectBaseDir + "' is write protected.");
        }
      }

      projectBaseDir = (projectBaseDir == null ? DEFAULT_PROJECT_BASE_DIR : projectBaseDir);

      write("[ configurator ] What is your project base directory (" + projectBaseDir + ") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        projectBaseDir = newValue;
      }
      config.setProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY, projectBaseDir);

      ok = true;
    }
  }

  /**
   * Checks the ManifestStore configuration until it is correct.
   *
   * @return The actual number of retries
   * @throws IOException
   */
  private boolean checkManifestStoreConfiguration(ManifestStore mStore) throws IOException {

    ErrorCode error = mStore.checkConfiguration();

    int retries = 0; // Breaks the loop after three entry runs.
    boolean quit = false;

    while (error != null && !quit) {

      if (error != null) {
        retries++;
        if (retries == 2) {
          quit = true; // Set the hook to quit
        }
      }

      writeln("[ configurator ] Error in manifest store configuration : " + error.getErrorMessage());

      CVSRepository cvs = null;

      try {
        cvs = (CVSRepository) mStore.getLocation();
        if (cvs == null) {
          cvs = new CVSRepository("manifest-store");
        }
      } catch (ClassCastException c) {
        write("[ configurator ] Sorry, only CVS is supported in this release.");
        cvs = new CVSRepository("manifest-store");
      }

      cvs.setWorkingContext(context); // todo hmmm

      writeln("[ configurator ] Please configure the manifest-store !");

      configureStore(cvs, mStore);

      // Next check.
      //
      error = mStore.checkConfiguration();

      if (error == null) {
        break;
      }
    }

    // Its wrong when there is still an error.
    //
    return (error == null);
  }


  private boolean checkLocationStoreConfiguration(LocationStore lStore) throws IOException {

    ErrorCode error = lStore.checkConfiguration();

    int retries = 0; // Breaks the loop after three entry runs.
    boolean quit = false;

    while (error != null && !quit) {

      if (error != null) {
        retries++;
        if (retries == 2) {
          quit = true; // Set the hook to quit
        }
      }

      writeln("[ configurator ] Error in location store configuration : " + error.getErrorMessage());

      CVSRepository cvs = null;

      try {
        cvs = (CVSRepository) lStore.getLocation();
        if (cvs == null) {
          cvs = new CVSRepository("location-store");
        }
      } catch (ClassCastException c) {
        write("[ configurator ] Sorry, only CVS is supported in this release.");
        cvs = new CVSRepository("location-store");
      }

      cvs.setWorkingContext(context); // todo hmmm

      writeln("[ configurator ] Please configure the location-store !");

      configureStore(cvs, lStore);

      // Next check.
      //
      error = lStore.checkConfiguration();

      if (error == null) {
        break;
      }
    }

    // Its wrong when there is still an error.
    //
    return (error == null);
  }

  private Store configureStore(CVSRepository cvs, Store store) throws IOException {

    // For Karma R1.0, we know its a CVS repo.
    //
    String newValue = "";

    // protocol
    //
    String protocol = cvs.getProtocol();
    protocol = (protocol == null ? "pserver" : protocol);

    while (!newValue.matches("pserver|local")) {
      write("[ configurator ] What is your server protocol ? [ local | pserver ] (" + protocol + ") : ");
      newValue = reader.readLine().trim();
      newValue = ("".equals(newValue) ? protocol : newValue);
    }
    protocol = newValue;
    cvs.setProtocol(protocol);

    if (protocol.equals(CVSRepository.PSERVER)) {

      // Host
      //
      String hostName = cvs.getHost();
      hostName = (hostName == null ? "127.0.0.1" : hostName);

      write("[ configurator ] What is your server's hostname or ip-adres ? (" + hostName + ") : ");
      newValue = reader.readLine().trim();
      if (!"".equals(newValue)) {
        hostName = newValue;
      }
      cvs.setHost(hostName);

// Port
//
      String port = (cvs.getPort() == -1 ? "2401" : "" + cvs.getPort());

      write("[ configurator ] What is your server port ? (" + port + ") : ");
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

    write("[ configurator ] What is your server repository ? (" + repository + ") : ");
    newValue = reader.readLine().trim();
    if (!"".equals(newValue)) {
      repository = newValue;
    }
    cvs.setRepository(repository);

// manifest-store.module
//
    String moduleName = store.getModuleName();

    if (store instanceof ManifestStore) {
      moduleName = (moduleName == null ? "manifests" : moduleName);
      write("[ configurator ] What is the cvs module for the manifest store ? (" + moduleName + ") ");
    } else {
      moduleName = (moduleName == null ? "locations" : moduleName);
      write("[ configurator ] What is the cvs module for the location store ? (" + moduleName + ") ");
    }

    newValue = reader.readLine().trim();
    if (!"".equals(newValue)) {
      moduleName = newValue;
    }

    try {
      checkAuthentication(cvs);
    } catch (ConfigurationException e) {
      write("[ configurator ] Error : " + e.getMessage());
    }

// Assign the properties that were asked to the user to the ManifestStore instance.
//
    store.setLocation(cvs);
    store.setModuleName(moduleName);

    return store;
  }


//  private void checkAuthentication(WorkingContext ctx, VersionControlSystem cvs) throws IOException, ConfigurationException {
  private void checkAuthentication(VersionControlSystem cvs) throws IOException, ConfigurationException {

    Authenticator authenticator = null;
    try {
      authenticator = Authenticators.getAuthenticator(new AuthenticatorKey(context.getName(), cvs.getId()));
    } catch (IllegalArgumentException e) {
      throw new ConfigurationException("The file $HOME/.karma/authenticators.xml seems to be corrupt. Please remove it and try again, karma will generate it for you.", e);
    } catch (AuthenticationException e) {
      authenticator = new Authenticator();
    }

    authenticator.setWorkingContext(context.getName());
    authenticator.setId(cvs.getId());

    String userNameString = (authenticator.getUsername() == null ? "" : "(" + authenticator.getUsername() + ")");
    String password = "";

    String newValue = "";

    while ("".equals(newValue)) {
      write("[ configurator ] What is your username ? " + userNameString + " : ");
      newValue = reader.readLine().trim();
      newValue = ("".equals(newValue) ? authenticator.getUsername() : newValue);
      authenticator.setUsername(newValue);
    }

    if (cvs.getProtocol().equals(CVSRepository.PSERVER)) {
      writeln("[ configurator ] Password is required, but input is in text-mode. Be warned !");
      write("[ configurator ] What is your password ? : ");
      password = reader.readLine().trim();
      password = ("".equals(password) ? "" : PasswordScrambler.scramble(password));
    }

    authenticator.setPassword(password);

    try {
      Authenticators.addAuthenticator(authenticator);
    } catch (AuthenticationException e) {
      throw new ConfigurationException("The file $HOME/.karma/authenticators.xml seems to be corrupt. Please remove it and try again, karma will generate it for you.", e);
    }
  }


  private void writeln(String text) {
    System.out.println(text);
  }

  private void write(String text) {
    System.out.print(text);
  }

}
