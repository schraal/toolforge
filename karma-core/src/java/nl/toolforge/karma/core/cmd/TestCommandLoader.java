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

import nl.toolforge.karma.core.test.BaseTest;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import java.util.Set;
import java.net.URL;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestCommandLoader extends BaseTest {

	public void testParse1() {

		CommandLoader cl = CommandLoader.getInstance();

    try {
      URL url = this.getClass().getClassLoader().getResource("test/test-commands.xml");
      Set l = cl.load(url);

      assertEquals("There should be two commands loaded from the descriptor file", 2, l.size());

    } catch (CommandLoadException e) {
      fail(e.getMessage());
    }
	}

	/**
	 * Tests the <code>org.apache.commons.cli</code> package. This test method is useful as a reference
	 * to how this thing works in the first place.
	 */
	public void testCommandOptions1() {

		Options options = new Options();

		Option option = new Option("m", "module-name", true, "The module name for the command.");
		options.addOption(option);

		CommandLineParser parser = new PosixParser();
		try {
			parser.parse(options, new String[]{"-m", "AAA"});
			assertEquals("AAA", options.getOption("m").getValue());
		} catch (ParseException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Tests whether we can query the Options as they have been generated by the <code>CommandLoader.load()</code>-method.
	 */
	public void testCommandOptions2() {

		CommandLoader cl = CommandLoader.getInstance();

		try {
			URL url = this.getClass().getClassLoader().getResource("test/test-commands.xml");

      Set l = cl.load(url);

			CommandDescriptor descriptor = null;
			if (l.iterator().hasNext()) {

				// Getting the first. Both test commands have the same options ...
				//
				descriptor = (CommandDescriptor) l.iterator().next();
			} else {
				fail("Expecting at least a few command descriptors here.");
			}

			CommandLineParser parser = new PosixParser();
			parser.parse(descriptor.getOptions(), new String[]{"-m", "BBB"});

			assertEquals("BBB", descriptor.getOptions().getOption("m").getValue());

		} catch (Exception k) {
			k.printStackTrace();
			fail(k.getMessage());
		}

	}


}
