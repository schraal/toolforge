package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.Manifest;

/**
 * <p>This class represents the shortlist of manifests. The shortlist is a
 * list of manifests that have been accessed most recently. Each time a
 * manifest is used, a counter is maintained in the users' configuration
 * directory (<code>manifest-shortlist.xml</code>).
 * <p/>
 * <p>Note that the mechanism is <b>NOT</b> thread safe. Two instances of
 * karma could overwrite the <code>manifest-shortlist.xml</code> file, but
 * since this is really an unlikely case (a user would have to execute the
 * correct command for two instances at the same time. What are the odds to
 * that ? The convenience of having this functionality outweighs this tiny
 * disadvantage,
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestShortList {

	private String lastManifestName = null;

	public ManifestShortList() {
		//
	}

	/**
	 * Gets the last <code>n</code> manifest names that have been used.
	 *
	 * @param n The number of manifest names you want to retrieve.
	 * @return The last <code>n</code> manifest names that have been used by (other)
	 *         instances of Karma.
	 */
	public static String[] getShortList(int n) {

		String[] shortList = new String[0];
		shortList[0] = "karma-1-0";

		return shortList;
	}

	/**
	 * Gets the last used manifest name.
	 *
	 * @return The last used manifest name.
	 */
	public static String getLast() {
		return null;
	}

	/**
	 * @param manifest
	 */
	public static void setCurrentManifest(Manifest manifest) {

		if (manifest.getName() != null) {
			// update in file
		}
		// update manifest.getName();
	}

	/**
	 * <p>Stores the current state to file.
	 */
	public static void flush() {
		// Save to file.
	}
}
