package nl.toolforge.karma.core.location;

import java.io.*;
import java.io.FilenameFilter;

/**
 * File filter for authentication xml files in a the Karma configuration directory. A file starting with
 * '<code>authentication</code>' and with an <code>xml</code>-extension, will be accepted.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class AuthenticationFilenameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		if ((name != null) && name.startsWith("authentication") && (name.endsWith(".xml"))) {
			return true;
		} else {
			return false;
		}
	}
}