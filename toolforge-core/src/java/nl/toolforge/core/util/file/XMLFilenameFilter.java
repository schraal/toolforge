package nl.toolforge.core.util.file;

import java.io.*;
import java.io.FilenameFilter;

/**
 * File filter for xml files in a directory.
 *
 * @author D.A. Smedes
 */
public class XMLFilenameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		if ((name != null) && (name.endsWith(".xml"))) {
			return true;
		} else {
			return false;
		}
	}
}