package nl.toolforge.karma.core.cmd;

/**
 * {@link CommandResponse} returned when the {@link Command} is a query command, i.e. a command that just returns information
 * the user is interested in. E.g. the ListManifests commands returns a list of all selectable manifests.
 *
 * This reponse mainly returns the information the user asked for.
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public class QueryCommandResponse extends CommandResponse {
}
