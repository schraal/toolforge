/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.cli;

import nl.toolforge.karma.cli.cmd.CLICommandResponseHandler;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands and if you're lucky, stuff works.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class CLI {

  // Logging system should be initialized by now ...
  //
  private static Log logger = LogFactory.getLog(CLI.class);

  private static final ResourceBundle FRONTEND_MESSAGES =
      BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

  private static String lastLine = "";

  private static boolean immediate = true;

  /**
   * Startup class for the command line interface.
   *
   * @param args As per the contract; we don't use it.
   */
  public static void main(String[] args) {

    Runtime.getRuntime().addShutdownHook(new Thread() {

      public void run() {

        if (immediate) {

          ConsoleWriter writer = new ConsoleWriter(true);
          String text = FRONTEND_MESSAGES.getString("message.THANK_YOU");
          int length = text.length();

          writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));

          StringBuffer g = new StringBuffer();
          g.append("\n\n").append(StringUtils.repeat("*", length));
          g.append("\n").append(text).append("\n");
          g.append(StringUtils.repeat("*", length)).append("\n");

          writer.writeln(g.toString());
        }
      }
    });

    String workingContext = null;

    try {
      workingContext = args[0];
    } catch (IndexOutOfBoundsException i) {
      workingContext = null;
    }

    ConsoleWriter writer = new ConsoleWriter(true);

    // Initialize the command context
    //
    CommandContext commandContext = null;

    writer.writeln(
        "********************\n" +
        "Welcome to Karma !!!\n" +
        "********************\n");

    if (workingContext == null) {
      writer.writeln("Loading `default` working context ...\n");
    } else {
      writer.writeln("Loading `" + workingContext + "` working context ...\n");
    }

    WorkingContext context = new WorkingContext(workingContext);
    Collection invalid = context.getInvalidConfiguration();

    if (invalid.size() > 0) {
      writer.writeln("Configuration for working context `" + context.getName() + "` incomplete !\n");
    } else {
      writer.writeln("Configuration for working context `" + context.getName() + "` complete !\n");
    }

    while (invalid.size() > 0) {

      // Warning, modifies the configuration ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      Iterator i = invalid.iterator();

      while (i.hasNext()) {

        WorkingContext.ConfigurationItem key = (WorkingContext.ConfigurationItem) i.next();

        if (key.getDefaultValue() == null) {
          System.out.print(key.getLabel() + " : ");
        } else {
          System.out.print(key.getLabel() + " (" + key.getDefaultValue() + ") : ");
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
        context.getConfiguration().setProperty(key.getProperty(), value);
      }

      invalid =  context.getInvalidConfiguration();
    }

    try {
      context.storeConfiguration();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      throw new KarmaRuntimeException(e.getMessage());
    }

    writer.writeln("Starting up ...\n");

    //
    //

//    KarmaRuntime.init(context);
    commandContext = new CommandContext(context);
    try {
      commandContext.init(new CLICommandResponseHandler(writer));
    } catch (LocationException e) {
      writer.writeln(e.getErrorMessage());
      logger.error(e.getMessage(), e);
    } catch (ManifestException e) {
      writer.writeln(e.getErrorMessage());
      logger.warn(e.getMessage(), e);
    }


    Manifest currentManifest = commandContext.getCurrentManifest();
    if (currentManifest != null) {
      ConsoleConfiguration.setManifest(currentManifest);

      writer.writeln(new MessageFormat(FRONTEND_MESSAGES.getString("message.MANIFEST_RESTORED")).format(new Object[]{currentManifest.getName()}));
    }

    String karmaHome = System.getProperty("karma.home");
    if (karmaHome == null) {
      writer.writeln("Property 'karma.home' not set; logging will be written to " + System.getProperty("user.home") + File.separator + "logs.");
    } else {
      writer.writeln("Logging will be written to " + System.getProperty("karma.home") + File.separator + "logs.");
    }

    try {

      // Open a reader, which is the actual command line ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      while (true) {

        writer.prompt();

        String line = null;
        if (reader != null || reader.readLine() != null) {
          line = reader.readLine().trim();
        }

        if ((line == null) || ("".equals(line.trim()))) {
          continue;
        }

        if ("[A".equals(line)) {
          line = lastLine;
          writer.writeln(line);
        } else {
          lastLine = line;
        }

        try {
          commandContext.execute(line);
        } catch (CommandException e) {
          writer.writeln("");
          //ugly way to format the messages. There is going to be a more elegant
          //solution for this.
          String message;
          if (e.getMessageArguments() != null && e.getMessageArguments().length != 0) {
            MessageFormat messageFormat = new MessageFormat(e.getErrorMessage());
            message = messageFormat.format(e.getMessageArguments());
          } else {
            message = e.getErrorMessage();
          }
          writer.writeln(message);
          logger.error(e.getMessage(), e);
        }
      }
    }
    catch (IOException e) {
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
    catch (RuntimeException r) {
      r.printStackTrace();
      System.exit(1);
    }
  }
}
