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
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
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
import java.util.ResourceBundle;

/**
 * <p>The <code>CLI</code> is the command-line interface for Karma. The class presents a simple-to-use command-line
 * terminal, where developers can type in their commands and if you're lucky, stuff works.
 *
 * <p>See {@link nl.toolforge.karma.core.LocalEnvironment} for a description of how to configure the logging
 * environment for your <strong>Karma</strong> runtime environment.
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

    ConsoleWriter writer = new ConsoleWriter(true);

    // Initialize the command context
    //
    CommandContext ctx = null;
    try {
      LocalEnvironment.initialize();

      ctx = new CommandContext();
      try {
        ctx.init(new CLICommandResponseHandler(writer));
      } catch (LocationException e) {
        writer.writeln(e.getErrorMessage());
        logger.error(e.getMessage(), e);
      } catch (ManifestException e) {
        writer.writeln(e.getErrorMessage());
        logger.warn(e.getMessage(), e);
      }

      Manifest currentManifest = ctx.getCurrentManifest();
      if (currentManifest != null) {
        ConsoleConfiguration.setManifest(currentManifest);

        writer.writeln(new MessageFormat(FRONTEND_MESSAGES.getString("message.MANIFEST_RESTORED")).format(new Object[]{currentManifest.getName()}));
      }

    } catch (KarmaException k) {

      writer.writeln(k.getErrorMessage());

      logger.error(k.getMessage(), k);

      writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));
      System.exit(1);
    }

    String karmaHome = System.getProperty("karma.home");
    File logDirectory = null;
    if (karmaHome == null) {
      String userHome = System.getProperty("user.home");
      logDirectory = new File(userHome, "logs");
      writer.writeln("Property 'karma.home' not set; logging will be written to " + System.getProperty("user.home") + File.separator + "logs.");
    } else {
      logDirectory = new File(karmaHome, "logs");
      writer.writeln("Logging will be written to " + System.getProperty("karma.home") + File.separator + "logs.");
    }

    try {

      // Open a reader, which is the actual command line ...
      //
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      while (true) {

        writer.prompt();

        String line = reader.readLine().trim();

        if ((line == null) || ("".equals(line.trim()))) {
          continue;
        }

        if ("[A".equals(line)) {
          line = lastLine;
          writer.writeln(line);
        } else {
          lastLine = line;
        }

        // Check if the user wants to exit
        //
        if (ConsoleConfiguration.getExitCommands().contains(line.trim().toUpperCase())) {

          String text = FRONTEND_MESSAGES.getString("message.THANK_YOU");
          int length = text.length();

          StringBuffer g = new StringBuffer();
          g.append("\n\n").append(StringUtils.repeat("*", length));
          g.append("\n").append(text).append("\n");
          g.append(StringUtils.repeat("*", length)).append("\n");

          writer.writeln(g.toString());

          immediate = false;

          break;
        }

        try {

          // Filter out the HELP command
          //
          if (line.trim().toLowerCase().startsWith("help") || line.trim().startsWith("?")) {

            writer.writeln("\n\n" + FRONTEND_MESSAGES.getString("message.VALID_COMMANDS"));
            writer.writeln(CommandRenderer.renderedCommands(CommandFactory.getInstance().getCommands()));

          } else {
            ctx.execute(line);
          }
        } catch (CommandException e) {
          writer.writeln("");
          writer.writeln(e.getErrorMessage());
          logger.error(e.getMessage(), e);
        }
      }
      writer.writeln(FRONTEND_MESSAGES.getString("message.EXIT"));
      logger.info("Exiting Karma ...");

      System.exit(0);
    }
    catch (IOException e) {
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
  }
}
