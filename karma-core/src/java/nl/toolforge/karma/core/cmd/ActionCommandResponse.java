package nl.toolforge.karma.core.cmd;

/**
 * {@link CommandResponse} returned when the {@link Command} is an action command, i.e. a command that performs
 * an action. E.g. the StartWork command sets a module to working state.
 *
 * This reponse mainly returns whether the requested action has been executed successfully or not.
 *
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class ActionCommandResponse extends CommandResponse {
}
