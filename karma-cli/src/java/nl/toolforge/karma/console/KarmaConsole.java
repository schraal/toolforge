/*
Karma KarmaConsole - Command Line Interface for the Karma application
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
package nl.toolforge.karma.console;

import nl.toolforge.karma.cli.cmd.ConsoleCommandResponseHandler;
import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.boot.Karma;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import nl.toolforge.karma.core.boot.WorkingContextException;
import nl.toolforge.karma.core.boot.ManifestStore;
import nl.toolforge.karma.core.boot.LocationStore;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * <p>The <code>KarmaConsole</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands and if you're lucky, stuff works.
 *
 * <p>Haven't fully tested it, but should be 'singleton'.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class KarmaConsole {

  // Logging system should be initialized by now ...
  //
  private Log logger = LogFactory.getLog(KarmaConsole.class);

  private final ResourceBundle FRONTEND_MESSAGES = BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

  private String lastLine = "";
  private boolean immediate = true;
//  private Manifest manifest = null;
  private CommandContext commandContext = null;

  public KarmaConsole() {}

  /**
   * Startup class for the command line interface.
   *
   * @param args As per the contract; we don't use it.
   */
  public static void main(String[] args) {
    KarmaConsole karmaConsole = new KarmaConsole();
    karmaConsole.runConsole(args);
  }

  /**
   * This one does the trick. Requires one (optional) argument : a working workingContext identifier.
   *
   * @param args Arguments passed to Karma.
   */
  private void runConsole(String[] args) {

    Runtime.getRuntime().addShutdownHook(new Thread() {

      public void run() {

        if (immediate) {

          String text = FRONTEND_MESSAGES.getString("message.THANK_YOU");
          int length = text.length();

          writeln("\n");

          writeln(FRONTEND_MESSAGES.getString("message.EXIT"));

          StringBuffer g = new StringBuffer();
          g.append("\n\n").append(StringUtils.repeat("*", length));
          g.append("\n").append(text).append("\n");
          g.append(StringUtils.repeat("*", length)).append("\n");

          writeln(g.toString());
        }
      }
    });

    // If the '-w <working-context> option is used, use it.
    //
    WorkingContext workingContext =
        (args.length == 0 ?
        new WorkingContext(Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT_PREFERENCE, WorkingContext.DEFAULT)) :
        new WorkingContext(args[0]));

    writeln(
        "\n" +
        "      _________________________________\n" +
        "      Welcome to Karma (R1.0 BETA5) !!!\n" +
        "\n" +
        "      K     A     R        M        A\n" +
        "      .     .     .        .        .\n" +
        "      Karma Ain't Remotely Maven or Ant\n" +
        "      _________________________________\n"
    );

    String karmaHome = System.getProperty("karma.home");
    if (karmaHome == null) {
      writeln("[ console ] Property 'karma.home' not set; logging will be written to " + System.getProperty("user.home") + File.separator + "logs.");
    } else {
      writeln("[ console ] Logging will be written to " + System.getProperty("karma.home") + File.separator + "logs.");
    }

    writeln("[ console ] Checking working context configuration.");

    WorkingContextConfiguration configuration = null;

    try {

      File configurationFile = Karma.getConfigurationFile(workingContext);
      configuration = new WorkingContextConfiguration(configurationFile);

      //
      // todo : moet nog anders, alhoewel het inmiddels beter is.
      //
      ManifestStore manifestStore = null;
      LocationStore locationStore  = null;
      try {
        configuration.load();
        //
        manifestStore = new ManifestStore(workingContext);
        configuration.getManifestStoreLocation().setWorkingContext(workingContext);
        manifestStore.setLocation(configuration.getManifestStoreLocation());
        manifestStore.setModuleName("manifests");
        workingContext.setManifestStore(manifestStore);
        //
        locationStore = new LocationStore(workingContext);
        configuration.getLocationStoreLocation().setWorkingContext(workingContext);
        locationStore.setLocation(configuration.getLocationStoreLocation());
        locationStore.setModuleName("locations");
        workingContext.setLocationStore(locationStore);

      } catch (WorkingContextException e) {
        //
      } catch (RuntimeException r) {
        //
      }

      // todo het feit dat dit eerst moet sucked.
      //
      workingContext.configure(configuration);




      // Check the validity state of the configuration.
      //

      ErrorCode error = configuration.check();

      while (error != null || manifestStore.checkConfiguration() != null || locationStore.checkConfiguration() != null ) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String start = "";
        try {
          while (!start.matches("n|y")) {
            write("[ console ] Working context not initialized properly, start configurator ? [Y|N] (Y) :");
            start = (reader.readLine().trim()).toLowerCase();
            start = ("".equals(start) ? "y" : start);
          }
        } catch (IOException e) {
          start = "n";
        }

        if ("n".equals(start)) {
          writeln("[ console ] Configuration incomplete. Cannot start Karma.");
          writeln("[ console ] Check configuration manually.");

          System.exit(1);

        } else {
          Configurator configurator = new Configurator(workingContext, configuration);
          configurator.checkConfiguration();

          manifestStore = workingContext.getManifestStore();
          locationStore = workingContext.getLocationStore();
        }
      }

    } catch (RuntimeException r) {
      writeln("\n");
      if (logger.isDebugEnabled()) {
        r.printStackTrace();
      }
      System.exit(1);
    }

    writeln("[ console ] Configuration complete. Loading working context `" + workingContext.getName() + "` ...");

    // Right now, we have a valid configuration and continue to load the working context.
    //
    workingContext.configure(configuration);

    writeln("[ console ] Configuration can be manually updated in `" + workingContext.getWorkingContextConfigurationBaseDir() + "`");

    writeln("\n[ console ] Starting up console ...\n");

    //
    //
    commandContext = new CommandContext(workingContext);
    try {
//      long start = System.currentTimeMillis();
      commandContext.init(new ConsoleCommandResponseHandler(this), true);
//      System.out.println("TOTAL STARTUP-TIME: " + (System.currentTimeMillis() - start));
    } catch (CommandException e) {
      logger.warn(e.getMessage());
    }

    try {

      // Open a reader, which is the actual command line ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      while (true) {

        prompt(commandContext);

        String line = null;
        if (reader != null || reader.readLine() != null) {
          line = reader.readLine().trim();
        }

        if ((line == null) || ("".equals(line.trim()))) {
          continue;
        }

        if ("[A".equals(line)) {
          line = lastLine;
          writeln(line);
        } else {
          lastLine = line;
        }

        try {
          commandContext.execute(line);
        } catch (CommandException e) {
          logger.error(e.getMessage(), e);
        }
      }
    } catch (RuntimeException r) {
      writeln("\n");
      if (logger.isDebugEnabled()) {
        r.printStackTrace();
      }
      System.exit(1);
    } catch (Exception e) {
      writeln("\n");
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
  }







//  private void checkConfiguration(WorkingContext workingContext, String configKey) {
//
//    try {
//
//      Collection config = (List) workingContext.getInvalidConfiguration().get(configKey);
//
//      while (config.size() > 0) {
//
//        // Warning, modifies the configuration ...
//        //
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        Iterator i = config.iterator();
//
//        while (i.hasNext()) {
//
//          WorkingContext.ConfigurationItem key = (WorkingContext.ConfigurationItem) i.next();
//
//          String value = null;
//          try {
//
//            if (key.isScrambled()) {
//              value = PasswordField.promptPassword("Enter password.");
//            } else {
//
//              if (key.getDefaultValue() == null) {
//                System.out.print(key.getLabel() + " : ");
//              } else {
//                System.out.print(key.getLabel() + " [" + key.getDefaultValue() + "] : ");
//              }
//              value = reader.readLine().trim();
//            }
//          } catch (IOException e) {
//            logger.error(e.getMessage(), e);
//          }
//
//          if (value == null || "".equals(value)) {
//            value = (key.getDefaultValue() == null ? "" : key.getDefaultValue());
//          }
//          if (key.isScrambled()) {
//            value = PasswordScrambler.scramble(value);
//          }
//
//          workingContext.getConfiguration().setProperty(key.getProperty(), value);
//        }
//        config = (List) workingContext.getInvalidConfiguration().get(configKey);
//      }
//    } catch (RuntimeException r) {
//      // This check is required for Windows users.
//      //
//      if (logger.isDebugEnabled()) {
//        r.printStackTrace();
//      }
//      System.exit(1);
//    }
//
//    try {
//      workingContext.storeConfiguration();
//    } catch (IOException e) {
//      logger.error(e.getMessage(), e);
//      throw new KarmaRuntimeException(e.getMessage());
//    }
//  }

  /**
   * Gets the default prompt, constructed as follows : <code>HH:MM:SS [ Karma ]</code>
   */
  public String getPrompt(CommandContext context) {

    Calendar now = Calendar.getInstance();

    String end = (context.getCurrentManifest() == null ? "Karma" : context.getCurrentManifest().getName());
    end = context.getWorkingContext().getName() + "::" + end;
    return
        StringUtils.leftPad("" + now.get(Calendar.HOUR_OF_DAY) , 2, "0") + ":" +
        StringUtils.leftPad("" + now.get(Calendar.MINUTE) , 2, "0") + ":" +
        StringUtils.leftPad("" + now.get(Calendar.SECOND) , 2, "0") + " [ " + end + " ] > ";
  }

  public void writeln(String text) {
    System.out.println(text);
  }

  public void write(String text) {
    System.out.print(text);
  }

  public void prompt(CommandContext context) {
    System.out.print(getPrompt(context));
  }
}
