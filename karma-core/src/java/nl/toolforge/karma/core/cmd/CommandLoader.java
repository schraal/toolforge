package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Loads command-descriptors from an <code>XML</code>-file. The default filename
 * is <code>commands.xml</code>, which should be available in the classpath. It should have been shipped with
 * the Karma Core release jar-file.
 *
 * <p>TODO the xml instance should be checked by a DTD or XML Schema document.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class CommandLoader {

	private CommandLoader() {}

	private static CommandLoader instance = null;

	public synchronized static CommandLoader getInstance() {
		if (instance == null) {
			instance = new CommandLoader();
		}
		return instance;
	}

	/**
	 * <p>Loads the default <code>XML</code> file containing command descriptors. The default command descriptor file is
	 * located in the directory that is set with the {@link nl.toolforge.karma.core.prefs.Preferences#CONFIGURATION_DIRECTORY_PROPERTY}. The
	 * default command descriptor <code>XML</code> file is designated with {@link Command#DEFAULT_COMMAND_FILE}.
	 *
	 * @return A <code>Set</code> of {@link nl.toolforge.karma.core.cmd.DefaultCommand} instances.
	 */
	Set load() throws KarmaException {

		return load(Command.DEFAULT_COMMAND_FILE);
	}

	/**
	 * <p>Loads the <code>XML</code> file containing command descriptors.
	 *
	 * @param resource The resource filename (relative to the classpath) to the <code>XML</code> file. Use
	 *                 {@link #load} to use the default settings.
	 *
	 * @return A <code>Set</code> of {@link nl.toolforge.karma.core.cmd.DefaultCommand} instances.
	 */
	Set load(String resource) throws KarmaException {

		Set descriptors = new HashSet();
		Set uniqueAliasses = new HashSet();

		try {
			// We do need to load the configuration file from the classpath.
			//
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource);

			// Now some xml parsing stuff needs to be done
			//
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document descriptorDocument = documentBuilder.parse(in);
			Element commandsElement = descriptorDocument.getDocumentElement();

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

					Options options = new Options();
					Option option = null;

					for (int j = 0; j < optionsElement.getLength(); j++) {

						Element optionElement = (Element) optionsElement.item(j);

						String opt = optionElement.getAttribute("opt");
						String longOpt = optionElement.getAttribute("longOpts");
						boolean required = optionElement.getAttribute("required").equals("true");

						// Add an options' arguments
						//
						boolean hasArgs = optionElement.getElementsByTagName("arg").getLength() > 0;

						NodeList argElements = optionElement.getElementsByTagName("arg");

						if (hasArgs) {

							for (int k = 0; k < argElements.getLength(); k++) {

								Element argElement = (Element) argElements.item(k);

								option =
									OptionBuilder.withArgName(argElement.getAttribute("module-name"))
									.hasArg()
									.withDescription(optionElement.getAttribute("description"))
									.create(opt);
							}
						} else {
							// Simple boolean option
							//
							option = new Option(opt, longOpt, false, optionElement.getAttribute("description"));
						}

						// We have an option for this command and add it to the Options container
						//
						options.addOption(option);
					}

					String commandName = commandElement.getAttribute("name");
					String alias = commandElement.getAttribute("alias");

					if (!uniqueAliasses.add(alias)) {
						throw new KarmaException(KarmaException.DUPLICATE_COMMAND_ALIAS);
					}

					String clazzName = commandElement.getElementsByTagName("classname").item(0).getFirstChild().getNodeValue();
					String explanation = commandElement.getElementsByTagName("description").item(0).getFirstChild().getNodeValue();

					CommandDescriptor descriptor = new CommandDescriptor(commandName, alias, options, clazzName);
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
					//
					// TODO : Check if the alias has not been used before either.
					if (!descriptors.contains(descriptor)) {
						descriptors.add(descriptor);
					} else {
						throw new KarmaException(KarmaException.DUPLICATE_COMMAND);
					}
				}
			}
		} catch (Exception e) {
			// TODO Important NOT to catch KarmaException at this place
			e.printStackTrace();
			throw new KarmaException(KarmaException.COMMAND_DESCRIPTOR_XML_ERROR, e);
		}
		return descriptors;
	}
}
