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
package nl.toolforge.karma.core.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class defining pattern strings for model elements, such as module names, version numbers and the like. This class can
 * be extended by implementations of Karma where other requirements exist.
 *
 * @author D.A. Smedes
 */
public class Expressions {

	/**
	 * Pattern string for a module name. Module names consist of a sequence of characters (<code>A-Z, a-z, 0-9</code>),
	 * followed by an underscore (<code>_</code>), and then the
	 */
	public static String MODULE_NAME = "[\\w&&[^_]]+";

	/**
	 * Pattern string for a version number. Version numbers consist of a sequence of digits and dashes (<code>-</code>).
	 * Examples : <code>0-0</code>, <code>1-0-9</code>.
	 */
	public static String VERSION = "(\\d+-{1})+\\d+";

	/**
	 * Symbolic names in a version control repository consist of the module name, concattenated with the version number,
	 * seperated by an underscore (<code>_</code>).
	 */
	public static String SYMBOLIC_NAME = MODULE_NAME + "_{1}" + VERSION;

	/**
	 * Branch names start with the letter <code>B</code>
	 */
	public static String BRANCH_NAME = "B{1}\\d{1,}_{1}" + MODULE_NAME;

	private static Map patterns = new HashMap();

	static {
		patterns.put("MODULE_NAME", Pattern.compile(MODULE_NAME));
		patterns.put("VERSION", Pattern.compile(VERSION));
		patterns.put("SYMBOLIC_NAME", Pattern.compile(SYMBOLIC_NAME));
	}

	public static Pattern getPattern(String key) {
		return (Pattern) patterns.get(key);
	}
}
