package nl.toolforge.karma.core.cmd;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.Set;

/**
 *
 * @author D.A. Smedes
 */
public class TestCommandLoader extends BaseTest {

	public void testParse1() {

		CommandLoader cl = CommandLoader.getInstance();

		try {
			Set l = cl.load("resources/commands-example.xml");

			assertEquals("There should be two commands loaded from the descriptor file", 2, l.size());

		} catch (KarmaException k) {
			k.printStackTrace();
			fail(k.getMessage());
		}
	}
}
