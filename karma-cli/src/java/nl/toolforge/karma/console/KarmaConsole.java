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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.toolforge.karma.cli.cmd.ConsoleCommandResponseHandler;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;

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
  private Manifest manifest = null;
  private WorkingContext workingContext = null;

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

          writeln(FRONTEND_MESSAGES.getString("message.EXIT"));

          StringBuffer g = new StringBuffer();
          g.append("\n\n").append(StringUtils.repeat("*", length));
          g.append("\n").append(text).append("\n");
          g.append(StringUtils.repeat("*", length)).append("\n");

          writeln(g.toString());
        }
      }
    });

    String workingContextName = null;

    try {

      // to be compliant, the -w option is given, but for the rest, it serves no purpose.
      //
      workingContextName = args[1];
    } catch (IndexOutOfBoundsException i) {
      workingContextName = null;
    }

    // Initialize the command workingContext
    //
    CommandContext commandContext = null;

    writeln(
        "********************\n" +
        "Welcome to Karma !!!\n" +
        "********************\n");

    if (workingContextName == null) {
      writeln("Loading working context `default` ...");
    } else {
      writeln("Loading working context `" + workingContextName + "` ...");
    }

    workingContext = new WorkingContext(workingContextName);

    writeln("Checking manifest store configuration ...");
    checkConfiguration("MANIFEST-STORE");
    writeln("Complete ...");

    writeln("Checking location store configuration ...");
    checkConfiguration("LOCATION-STORE");
    writeln("Complete ...");

    writeln("Configuration can be updated in `" + workingContext.getWorkingContextConfigDir() + "`");

    writeln("\nStarting up ...\n");

    //
    //

    commandContext = new CommandContext(workingContext);
    try {
      commandContext.init(new ConsoleCommandResponseHandler(this));
    } catch (LocationException e) {
      writeln(e.getErrorMessage());
      logger.error(e.getMessage(), e);
    } catch (ManifestException e) {
      writeln(e.getErrorMessage());
      logger.warn(e.getMessage(), e);
    }


    Manifest currentManifest = commandContext.getCurrentManifest();
    if (currentManifest != null) {
      manifest = currentManifest;

      writeln(new MessageFormat(FRONTEND_MESSAGES.getString("message.MANIFEST_RESTORED")).format(new Object[]{currentManifest.getName()}));
    }

    String karmaHome = System.getProperty("karma.home");
    if (karmaHome == null) {
      writeln("Property 'karma.home' not set; logging will be written to " + System.getProperty("user.home") + File.separator + "logs.");
    } else {
      writeln("Logging will be written to " + System.getProperty("karma.home") + File.separator + "logs.");
    }

    try {

      // Open a reader, which is the actual command line ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      while (true) {

        prompt();

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
          writeln("");
          //ugly way to format the messages. There is going to be a more elegant
          //solution for this.
          String message;
          if (e.getMessageArguments() != null && e.getMessageArguments().length != 0) {
            MessageFormat messageFormat = new MessageFormat(e.getErrorMessage());
            message = messageFormat.format(e.getMessageArguments());
          } else {
            message = e.getErrorMessage();
          }
          writeln(message);
          logger.error(e.getMessage(), e);
        } 
      }
    }
    catch (RuntimeException r) {
      r.printStackTrace();
      System.exit(1);
    }
    catch (Exception e) {
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
  }


  private void checkConfiguration(String configKey) {

    Collection config = (List) workingContext.getInvalidConfiguration().get(configKey);

    while (config.size() > 0) {

      // Warning, modifies the configuration ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      Iterator i = config.iterator();

      while (i.hasNext()) {

        WorkingContext.ConfigurationItem key = (WorkingContext.ConfigurationItem) i.next();

        if (key.getDefaultValue() == null) {
          System.out.print(key.getLabel() + " : ");
        } else {
          System.out.print(key.getLabel() + " [" + key.getDefaultValue() + "] : ");
        }

        String value = null;
        try {
          value = reader.readLine().trim();
        } catch (IOException e) {
          // todo moet anders ........
          //
          e.printStackTrace();
        }

        if (value == null || "".equals(value)) {
          value = (key.getDefaultValue() == null ? "" : key.getDefaultValue());
        }
        workingContext.getConfiguration().setProperty(key.getProperty(), value);
      }
      config = (List) workingContext.getInvalidConfiguration().get(configKey);
    }

    try {
      workingContext.storeConfiguration();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new KarmaRuntimeException(e.getMessage());
    }

  }

  /**
   * Gets the default prompt, constructed as follows : <code>HH:MM:SS [ Karma ]</code>
   */
  public String getPrompt() {

    Calendar now = Calendar.getInstance();

    String end = (manifest == null ? "Karma" : manifest.getName());
    end = workingContext.getName() + "::" + end;
    return
        StringUtils.leftPad("" + now.get(Calendar.HOUR_OF_DAY) , 2, "0") + ":" +
        StringUtils.leftPad("" + now.get(Calendar.MINUTE) , 2, "0") + ":" +
        StringUtils.leftPad("" + now.get(Calendar.SECOND) , 2, "0") + " [ " + end + " ] > ";
  }

  public void writeln(String text) {
    System.out.println(text);
  }

  public void prompt() {
    System.out.print(getPrompt());
  }

  public void setManifest(Manifest manifest) {
    this.manifest = manifest;
  }

}
