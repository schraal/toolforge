/*
Karma core - Core of the Karma application
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
package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.digester.CommandDescriptorCreationFactory;
import nl.toolforge.karma.core.cmd.digester.OptionDescriptorCreationFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * <p>Loads command-descriptors from an <code>XML</code>-file. The default filename
 * is <code>commands.xml</code>, which should be available in the classpath. It should have been shipped with
 * the Karma Core release jar-file.
 * <p/>
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandLoader {

  //TODO the xml instance should be checked by a DTD or XML Schema document.

  private static Log logger = LogFactory.getLog(CommandLoader.class);
  /**
   * Default filename for the command descriptor file
   */
  public static final String DEFAULT_COMMANDS_BASEDIR = "commands";

  /** File name for core commands. */
  public static final String CORE_COMMANDS_FILE = "core-commands.xml";

  /** Directory where plugins are located. */
  public static final String COMMAND_PLUGINS_DIR = "plugins";

  /** File name for plugin commands definitions. */
  public static final String PLUGIN_COMMANDS_FILE = "commands.xml";

  private CommandLoader() {
  }

  private static CommandLoader instance = null;

  /**
   * Gets the singleton instance of the <code>CommandLoader</code>.
   *
   * @return The singleton instance of the <code>CommandLoader</code>.
   */
  public static CommandLoader getInstance() {
    if (instance == null) {
      instance = new CommandLoader();
    }
    return instance;
  }

  /**
   * Loads command xml files from a predefined base directory. The base directory is determined by
   * {@link DEFAULT_COMMANDS_BASEDIR}, relative to the runtime classpath. Core commands are considered to be in
   * <code>core-commands.xml</code>. The rest of the commands are located in plugin directories on the classpath.
   *
   * @return The full set of commands for Karma.
   * @throws CommandLoadException
   */
  CommandDescriptorMap load() throws CommandLoadException {

    // Load the core commands
    //
    CommandDescriptorMap commandSet = loadCoreCommands();

    // Load the plugin commands
    //
    Enumeration enum = null;
    try {
      String commands = DEFAULT_COMMANDS_BASEDIR + "/" + COMMAND_PLUGINS_DIR + "/" + PLUGIN_COMMANDS_FILE;
      enum = this.getClass().getClassLoader().getResources(commands);
    } catch (IOException ioe) {
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_PLUGIN_COMMANDS_FILE, new Object[]{PLUGIN_COMMANDS_FILE});
    }

    while (enum.hasMoreElements()) {
      commandSet.addAll((CommandDescriptorMap) load((URL) enum.nextElement()));
    }

    return commandSet;
  }

  /**
   * <p>Loads the <code>xml</code> file containing command descriptors.
   *
   * @param resource         The resource url to a command <code>xml</code> file. Use
   *                         {@link #load} to use the default settings.
   * @return                 A <code>List</code> of {@link CommandDescriptor} instances.
   *
   * @throws CommandLoadException
   */
  CommandDescriptorMap load(URL resource) throws CommandLoadException {

    try {
      return (CommandDescriptorMap) getCommandDigester().parse(resource.openStream());
    } catch (IOException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_PLUGIN_COMMANDS_FILE, new Object[]{PLUGIN_COMMANDS_FILE});
    } catch (SAXException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_PLUGIN_COMMANDS_FILE, new Object[]{PLUGIN_COMMANDS_FILE});
    }
  }

  /**
   * Loads commands from the Karma default <code>commands.xml</code> file.
   *
   * @throws CommandLoadException
   */
  private CommandDescriptorMap loadCoreCommands() throws CommandLoadException {

    try {
      String defaultCommands = DEFAULT_COMMANDS_BASEDIR + "/" + CORE_COMMANDS_FILE;

      CommandDescriptorMap cds = (CommandDescriptorMap) getCommandDigester().parse(this.getClass().getClassLoader().getResourceAsStream(defaultCommands));

      return cds;
    } catch (IOException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_DEFAULT_COMMANDS, new Object[]{CORE_COMMANDS_FILE});
    } catch (SAXException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_DEFAULT_COMMANDS, new Object[]{CORE_COMMANDS_FILE});
    }
  }

  private Digester getCommandDigester() {

    Digester digester = new Digester();

//    digester.addObjectCreate("commands", ArrayList.class);
    digester.addObjectCreate("commands", CommandDescriptorMap.class);

    digester.addFactoryCreate("*/command", CommandDescriptorCreationFactory.class);
    digester.addCallMethod("*/command/description", "setDescription", 0);
    digester.addCallMethod("*/command/classname", "setClassName", 0);
    digester.addCallMethod("*/command/help", "setHelp", 0);

    digester.addObjectCreate("*/command/options", Options.class);

    digester.addFactoryCreate("*/command/options/option", OptionDescriptorCreationFactory.class);
    digester.addCallMethod("*/command/options/option/arg", "setArgName", 0);
    digester.addSetNext("*/command/options/option", "addOption");

    digester.addSetNext("*/command/options", "addOptions"); // Adds an Option to the command.

    digester.addSetNext("*/command", "add"); // Adds a CommandDescriptor instance to the set.

    return digester;
  }
}
