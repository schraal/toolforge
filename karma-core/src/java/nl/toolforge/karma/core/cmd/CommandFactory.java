package nl.toolforge.karma.core.cmd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import nl.toolforge.karma.core.KarmaException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;

/**
 * This factory is the single resource of Command objects.
 *
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public final class CommandFactory {

    //Singleton
    //
    private static CommandFactory factory = null;

    // Reference to all loaded commands
    //
    private static Map commands = null;

    // Reference ro all command names
    //
    private static Set commandNames = null;

    /**
     * Only static methods
     */
    private CommandFactory() throws KarmaException {

        Set descriptors = CommandLoader.getInstance().load();
        commands = new Hashtable();

        // Store all commands by name in a hash
        //
        for (Iterator i = descriptors.iterator(); i.hasNext();) {
            CommandDescriptor descriptor = (CommandDescriptor) i.next();
            commands.put(descriptor.getName(), descriptor);
        }

        // Create a set of all command names.
        //
        commandNames = new HashSet();
        for (Iterator i = descriptors.iterator(); i.hasNext();) {
            CommandDescriptor descriptor = (CommandDescriptor) i.next();
            commandNames.add(descriptor.getName());
            commandNames.add(descriptor.getAlias());
        }
    }


    public static CommandFactory getInstance() throws KarmaException {
        if (factory == null) {
            factory = new CommandFactory();
        }
        return factory;
    }

    /**
     * @param commandLine Command as entered on the command line
     * @return A fully correct Command
     * @throws CommandException  When a correct command could not be constructed.
     */
    public Command getCommand(String commandLine) throws CommandException {
        StringTokenizer tokenizer = new StringTokenizer(commandLine);
        String commandName = null;
        if (tokenizer.hasMoreTokens()) {
            commandName = tokenizer.nextToken();
        }
        String[] commandArgs = null;
        if (tokenizer.hasMoreTokens()) {
            commandArgs = new String[tokenizer.countTokens()-1];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                commandArgs[i] = tokenizer.nextToken();
                i++;
            }
        }

        Command cmd = null;
        if (isCommand(commandName)) {
            try {
                CommandDescriptor cmdDesc = (CommandDescriptor) commands.get(commandName);
                cmd = (Command) cmdDesc.getImplementation().newInstance();
                CommandLineParser parser = new PosixParser();
                parser.parse(cmd.getOptions(), commandArgs);

                return cmd;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CommandException(KarmaException.LAZY_BASTARD);
            }
        } else {
            throw new CommandException(KarmaException.LAZY_BASTARD);
        }
    }


    /**
     * Checks if some string is a command within this context.
     *
     * @param name
     * @return
     */
    private boolean isCommand(String name) {
        return commandNames.contains(name);
    }

    public Collection getCommands() {
        return commands.values();
    }


}
