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

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p>Loads command-descriptors from an <code>XML</code>-file. The default filename
 * is <code>commands.xml</code>, which should be available in the classpath. It should have been shipped with
 * the Karma Core release jar-file.
 * <p/>
 * <p>TODO the xml instance should be checked by a DTD or XML Schema document.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandLoader {

  private static Log logger = LogFactory.getLog(CommandLoader.class);

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
	 * <p>Loads the default <code>XML</code> file containing command descriptors. The default command descriptor file is
	 * located in <code>${user.home}/.karma</code>.
	 *
	 * @return A <code>Set</code> of {@link nl.toolforge.karma.core.cmd.CommandDescriptor} instances.
	 */
	Set load() {

		return load(Command.DEFAULT_COMMAND_FILE);
	}


	private static boolean loaded = false;
	private static Document descriptorDocument = null;

	/**
	 * <p>Loads the <code>XML</code> file containing command descriptors.
	 *
	 * @param resource The resource filename (relative to the classpath) to the <code>XML</code> file. Use
	 *                 {@link #load} to use the default settings.
	 * @return A <code>Set</code> of {@link nl.toolforge.karma.core.cmd.DefaultCommand} instances.
	 */


	Set load(String resource) {

		Set descriptors = new HashSet();
		Set uniqueAliasses = new HashSet();


		try {

			Element commandsElement = null;

			// We need to load the configuration file from the classpath.
			//
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);
			// Now some xml parsing stuff needs to be done
			//
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			if (!loaded) {
				descriptorDocument = documentBuilder.parse(in);
				loaded = true;
			}
			commandsElement = descriptorDocument.getDocumentElement();

			// Always reload commands into memory, due to something I don't understand in the cli stuff.
			//
			NodeList commands = commandsElement.getElementsByTagName("command");

			for (int i = 0; i < commands.getLength(); i++) {

				Element commandElement = (Element) commands.item(i);

				// First we get the basic things to create a descriptor
				//
				//boolean internalCommand = (new Boolean(command.getAttribute("internal"))).booleanValue();
				//boolean manifestSpecific = (new Boolean(command.getAttribute("needsManifest"))).booleanValue();
				//boolean extraArgumentsAllowed = (new Boolean(command.getAttribute("extraArgumentsAllowed"))).booleanValue();

				if (commandElement.getElementsByTagName("options").getLength() > 0) {

					NodeList optionsElement = ((Element) commandElement.getElementsByTagName("options").item(0)).getElementsByTagName("option");

					Options options = null;

					if (optionsElement.getLength() > 0) {

						options = new Options();
						Option option = null;

						for (int j = 0; j < optionsElement.getLength(); j++) {

							Element optionElement = (Element) optionsElement.item(j);

							String opt = optionElement.getAttribute("opt");
							String longOpt = optionElement.getAttribute("longOpt");

							// Add an options' arguments
							//
							boolean hasArgs = optionElement.getElementsByTagName("arg").getLength() > 0;
							boolean required = optionElement.getAttribute("required").equals("true");

							NodeList argElements = optionElement.getElementsByTagName("arg");

							option = new Option(opt, longOpt, hasArgs, optionElement.getAttribute("description"));
							option.setRequired(required);

							if (hasArgs) {

								for (int k = 0; k < argElements.getLength(); k++) {

									Element argElement = (Element) argElements.item(k);
									option.setArgName(argElement.getAttribute("name"));
								}
							}

							// We have an option for this command and add it to the Options container
							//
							options.addOption(option);
						}
					}

					String commandName = commandElement.getAttribute("name");
					String alias = commandElement.getAttribute("alias");

					// if we're dealing with an alias list, split it up and check whether all parts are unique.
					if( alias.indexOf(" ") != -1) {
						StringTokenizer tokenizer = new StringTokenizer(alias," ");
						while( tokenizer.hasMoreTokens()) {
							if (!uniqueAliasses.add(tokenizer.nextToken())) {
								throw new KarmaRuntimeException("Duplicate command alias.");
							}
						}
					}

          Node child = commandElement.getElementsByTagName("classname").item(0).getFirstChild();
          String clazzName;
          if (child != null) {
  					clazzName = child.getNodeValue();
          } else {
            throw new KarmaRuntimeException("No classname defined for command '"+commandName+"' in the commands.xml.");
          }
					String explanation = commandElement.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();

          CommandDescriptor descriptor = null;
          try {
            descriptor = new CommandDescriptor(commandName, alias, clazzName);
          } catch (KarmaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
          }
          if (options != null) {
						descriptor.setOptions(options);
					} else {
						descriptor.setOptions(new Options());
					}
					descriptor.setDescription(explanation);

					// TODO : dependencies should be added. Might not be required for version 2.0 (CVS support only)
					// descriptor.setDependencies(null);

					// If there is a help element for the command ...
					//
					if (commandElement.getElementsByTagName("help").getLength() > 0) {

						Element helpElement = (Element) commandElement.getElementsByTagName("help").item(0);

						if (helpElement.getFirstChild() != null) {
							descriptor.setHelp(helpElement.getFirstChild().getNodeValue());
						}
					}

					// Assign the Options object to the command descriptor. At this point, the command
					// can use the Options object.
					//

					// Check if the command has not yet been added.
					if (!descriptors.contains(descriptor)) {
						descriptors.add(descriptor);
					} else {
						throw new KarmaRuntimeException("Duplicate command definition.");
					}
				}
			}
		} catch (Exception e) {
      // If something goes wrong here, throw a runtime; this is too serious to ignore.
      //
      logger.error(e.getMessage(), e);
			throw new KarmaRuntimeException(e.getMessage());
		}

		return descriptors;
	}
}
