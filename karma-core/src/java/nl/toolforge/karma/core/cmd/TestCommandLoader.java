package nl.toolforge.karma.core.cmd;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;

import java.util.List;

/**
 *
 * @author D.A. Smedes
 */
public class TestCommandLoader extends TestCase {

	public void testParse1() {

		CommandLoader cl = CommandLoader.getInstance();

		try {
			List l = cl.parse("commands-example.xml");

			assertEquals("There should be two commands loaded from the descriptor file", 2, l.size());

		} catch (KarmaException k) {
			k.printStackTrace();
			fail(k.getMessage());
		}
	}
}
