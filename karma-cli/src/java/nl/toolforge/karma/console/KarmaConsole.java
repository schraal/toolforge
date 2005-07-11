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
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import nl.toolforge.karma.core.boot.WorkingContextException;
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
  private CommandContext commandContext = null;

  public KarmaConsole() {}


  /**
   *
   * @param args  The working context (0), whether to update (1) and the command
   *              plus his options (3 ...).
   */
  public void runConsole(String[] args) {

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
    WorkingContext workingContext;
    if ( args[0] == null || args[0].equals("") ) {
      workingContext = new WorkingContext(Preferences.userRoot().get(WorkingContext.WORKING_CONTEXT_PREFERENCE, WorkingContext.DEFAULT));
    } else {
      workingContext = new WorkingContext(args[0]);
    }

    writeln(
        "\n" +
        "      _________________________________\n" +
        "      Welcome to Karma (R1.0 RC1) !!!!!\n" +
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

    writeln("[ console ] Checking working context configuration for `" + workingContext.getName() + "`.");

    WorkingContextConfiguration configuration = null;

    try {
      configuration = new WorkingContextConfiguration(workingContext);
      try {
        configuration.load();
      } catch (WorkingContextException e) {}

      workingContext.configure(configuration);

      // Check the validity state of the configuration.
      //

      ErrorCode error = configuration.check();
      ErrorCode error2 = null;
      ErrorCode error3 = null;

      if (error != null) {
        writeln("[ console ] ** Error in working context configuration : " + error.getErrorMessage());
      }
      if (configuration.getManifestStore() == null) {
        writeln("[ console ] ** Error in working context configuration : Missing configuration for manifest store.");
      } else {
        error2 = configuration.getManifestStore().checkConfiguration();
        if (error2 != null) {
          writeln("[ console ] ** Error in working context configuration : " + error2.getErrorMessage());
        }
      }
      if (configuration.getLocationStore() == null) {
        writeln("[ console ] ** Error in working context configuration : Missing configuration for location store.");
      } else {
        error3 = configuration.getLocationStore().checkConfiguration();
        if (error3 != null) {
          writeln("[ console ] ** Error in working context configuration : " + error3.getErrorMessage());
        }
      }

      while (error != null || configuration.getManifestStore() == null || error2 != null || configuration.getLocationStore() == null || error3 != null ) {

        // todo hier eerst de foutmelding tonen.

        // todo offline-mode ?

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
          writeln("[ console ] ** Configuration incomplete. Cannot start Karma.");
          writeln("[ console ] Check configuration manually.");

          System.exit(1);

        } else {
          Configurator configurator = new Configurator(workingContext, configuration);
          configurator.checkConfiguration();

          // Run checks once more.
          //
          error = configuration.check();
          if (configuration.getManifestStore() != null) {
            error2 = configuration.getManifestStore().checkConfiguration();
          }
          if (configuration.getLocationStore() != null) {
            error3 = configuration.getLocationStore().checkConfiguration();
          }
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
      commandContext.init(new ConsoleCommandResponseHandler(this), new Boolean(args[1]).booleanValue());
//      System.out.println("TOTAL STARTUP-TIME: " + (System.currentTimeMillis() - start));
    } catch (CommandException e) {
      logger.warn(e.getMessage());
    }

    try {

      // Open a reader, which is the actual command line ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


      String line = null;
      while (true) {

        prompt();

        if (reader != null || reader.readLine() != null) {
          line = reader.readLine().trim();
        }

        if ((line == null) || ("".equals(line.trim()))) {
          prompt();
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
          // The command context has already sent all required messages.
          //
        }
      }
    } catch (Throwable e) {
      writeln("\n");
      logger.fatal("Exception caught by KarmaConsole catch-all. ", e);
      
      String logfile = System.getProperty("karma.home", System.getProperty("user.home")) + File.separator + "logs" + File.separator + "karma-default.log";
      
      System.out.println("Something went BOOM inside of Karma.");
      System.out.println("Details: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
      System.out.println("See the log file (" + logfile + ") for more information.");
      System.out.println("Please report recurring problems to the Karma developers (http://sourceforge.net/tracker/?group_id=98766).");
      System.out.println("We apologize for the inconvenience.");
      System.out.println();
      
      System.exit(1);
    }
  }


  /**
   * Gets the default prompt, constructed as follows : <code>HH:MM:SS [ Karma ]</code>
   */
  public String getPrompt() {

    Calendar now = Calendar.getInstance();

    String end = (commandContext.getCurrentManifest() == null ? "Karma" : commandContext.getCurrentManifest().getName());
    end = commandContext.getWorkingContext().getName() + "::" + end;
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

  public void prompt() {
    System.out.print(getPrompt());
  }
}
