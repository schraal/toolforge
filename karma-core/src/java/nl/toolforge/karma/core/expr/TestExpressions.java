package nl.toolforge.karma.core.expr;

import junit.framework.TestCase;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestExpressions extends TestCase {

	public void testModuleNamePattern() {

		String moduleName = null;
		Matcher m = null;

		Pattern p = Pattern.compile(Expressions.MODULE_NAME);

		//
		// Correct patterns
		//

		moduleName = "test";
		m = p.matcher(moduleName);
		assertTrue("Checking " + moduleName, m.matches());

		moduleName = "M";
		m = p.matcher(moduleName);
		assertTrue("Checking " + moduleName, m.matches());

		//
		// Incorrect patterns
		//

		moduleName = "module_x-3-f";
		m = p.matcher(moduleName);
		assertFalse("Checking " + moduleName, m.matches());

		moduleName = "module_3-0";
		m = p.matcher(moduleName);
		assertFalse("Checking " + moduleName, m.matches());


		moduleName = "";
		m = p.matcher(moduleName);
		assertFalse("Checking " + moduleName, m.matches());

		moduleName = "M33_";
		m = p.matcher(moduleName);
		assertFalse("Checking " + moduleName, m.matches());
	}

	public void testVersionPattern() {

		String version = null;
		Matcher m = null;

		Pattern p = Pattern.compile(Expressions.VERSION);

		//
		// Correct patterns
		//

		version = "0-3";
		m = p.matcher(version);
		assertTrue("Checking " + version, m.matches());

		version = "0-3-2-22-1111111";
		m = p.matcher(version);
		assertTrue("Checking " + version, m.matches());

		version = "00-1";
		m = p.matcher(version);
		assertTrue("Checking " + version, m.matches());

		//
		// Incorrect patterns
		//

		version = "X";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "0";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());
		version = "0_0";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "MODULE_0-3";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "0--";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "0--3";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());

		version = "0-3--0";
		m = p.matcher(version);
		assertFalse("Checking " + version, m.matches());
	}

	public void testSymbolicNamePattern() {

		String symbolicName = null;
		Matcher m = null;

		Pattern p = Pattern.compile(Expressions.SYMBOLIC_NAME);

		//
		// Correct patterns
		//

		symbolicName = "MODULE_0-3";
		m = p.matcher(symbolicName);
		assertTrue("Checking " + symbolicName, m.matches());

		//
		// Incorrect patterns
		//

		symbolicName = "MODULE_0--3";
		m = p.matcher(symbolicName);
		assertFalse("Checking " + symbolicName, m.matches());

		symbolicName = "MODULE-0--3";
		m = p.matcher(symbolicName);
		assertFalse("Checking " + symbolicName, m.matches());
	}

	public void testBranchPattern() {
		fail("No tests implemented, i.e. not tested.");
	}

}
