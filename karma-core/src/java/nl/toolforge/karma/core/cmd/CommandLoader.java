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

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.digester.CommandDescriptorCreationFactory;
import nl.toolforge.karma.core.cmd.digester.OptionDescriptorCreationFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
  public static final String DEFAULT_COMMAND_FILE = "commands.xml";
  public static final String COMMAND_PLUGINS_DIR = "plugins";

  private CommandLoader() {
  }

  private static CommandLoader instance = null;

  public synchronized static CommandLoader getInstance() {
    if (instance == null) {
      instance = new CommandLoader();
    }
    return instance;
  }

  /**
   * Loads command xml files from a certain <code>baseDir</code>. All xml files in <code>baseDir</code> are parsed and
   * their commands are added to the default command set provided by Karma (as located in
   *
   * @return The default commands plus any
   */
  Set load() throws CommandLoadException {

    //
    //
    Set commandSet = loadDefaultCommands();

    File pluginBaseDir =
        new File(WorkingContext.getKarmaHome(), "resources" + File.separator + "commands" + File.separator + "plugins");

    if (pluginBaseDir.exists()) {

      String[] files = pluginBaseDir.list(new XMLFilenameFilter());
      for (int i = 0; i < files.length; i++) {

        try {
          commandSet.addAll((Set) getCommandDigester().parse(new File(pluginBaseDir, files[i])));
        } catch (IOException e) {
          logger.error(e);
          throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_COMMAND_FILE, new Object[]{files[i]});
        } catch (SAXException e) {
          logger.error(e);
          throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_COMMAND_FILE, new Object[]{files[i]});
        }
      }
    }
    return commandSet;
  }

  /**
   * <p>Loads the <code>xml</code> file containing command descriptors.
   *
   * @param resource The resource filename (relative to the classpath) to the <code>xml</code> file. Use
   *   {@link #load} to use the default settings.
   * @return A <code>Set</code> of {@link CommandDescriptor} instances.
   * 
   * @throws CommandLoadException
   */
  Set load(String resource) throws CommandLoadException {

    try {
      return (Set) getCommandDigester().parse(this.getClass().getClassLoader().getResourceAsStream(resource));
    } catch (IOException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_COMMAND_FILE);
    } catch (SAXException e) {
      logger.error(e);
      e.printStackTrace();
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_COMMAND_FILE);
    }
  }

  /**
   * Loads commands from the Karma default <code>commands.xml</code> file.
   *
   * @throws CommandLoadException
   */
  private Set loadDefaultCommands() throws CommandLoadException {

    try {
      File defaultCommands = new File(DEFAULT_COMMANDS_BASEDIR, DEFAULT_COMMAND_FILE);
      return (Set) getCommandDigester().parse(this.getClass().getClassLoader().getResourceAsStream(defaultCommands.getPath()));
    } catch (IOException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_DEFAULT_COMMANDS);
    } catch (SAXException e) {
      logger.error(e);
      throw new CommandLoadException(CommandLoadException.LOAD_FAILURE_FOR_DEFAULT_COMMANDS);
    }
  }

  private Digester getCommandDigester() {

    Digester digester = new Digester();

    digester.addObjectCreate("commands", HashSet.class);

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
