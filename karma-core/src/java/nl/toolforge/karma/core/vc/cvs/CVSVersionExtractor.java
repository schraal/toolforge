package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.cmd.CommandContext;
import nl.toolforge.karma.core.vc.VersionExtractor;
import org.netbeans.lib.cvsclient.command.log.LogInformation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class CVSVersionExtractor implements VersionExtractor {

	private static CVSVersionExtractor instance = null;

	public static CVSVersionExtractor getInstance() {

		if (instance == null) {
			return new CVSVersionExtractor();
		}
		return instance;
	}

	private CVSVersionExtractor() {}

	/**
	 * <p>See {@link VersionExtractor#getNextVersion}.
	 *
	 * <p>Connects to the correct CVS repository and determines the next version in the branch (if applicable, otherwise
	 * it is the trunk) for the module. This is done by quering <code>module.info</code>.
	 *
	 * @param module The next version number <code>module</code>.
	 *
	 * @return The next version for <code>module</code>.
	 * @throws KarmaException TODO complete when implementation is ready.
	 */
	private static synchronized String getNext(Module module) throws KarmaException {

		if (module instanceof SourceModule) {
			if (!((SourceModule) module).hasModuleInfo()) {
				throw new KarmaException(KarmaException.NO_MODULE_INFO, new Object[]{module.getName()});
			}
		}

		CVSRunner runner = null;
		try {
			runner = new CVSRunner(module.getLocation(), new File("")); // baseLocation doesn't matter ...
		} catch (KarmaException e) {
			throw new CVSException(null); // TODO to be defined (errorcode).
		}

		LogInformation logInformation = runner.log(module);
		Collection currentVersions = logInformation.getAllSymbolicNames();

		// Step 1 : get all symbolicnames that apply to the correct pattern
		//
		//
		SortedSet matchingSet = new TreeSet();

		Pattern pattern = null;
		if (((SourceModule) module).hasDevelopmentLine()) {
      // We are working on a branch
			//
			pattern = Pattern.compile(module.getName().concat("_\\d\\-[\\d\\-\\d]+"));
		} else {
			// We are doing MAINLINE development.
			//
			pattern = Pattern.compile("MAINLINE+_\\d\\-[\\d\\-\\d]+");
		}

		for (Iterator it = currentVersions.iterator(); it.hasNext();) {

			LogInformation.SymName s = (LogInformation.SymName) it.next();

			Matcher matcher = pattern.matcher(s.getName());
			if (matcher.matches()) {
				matchingSet.add(s.getName());
			}
		}

		// Step 2 : Sort them, so the last one is on top (or something).
		//

		String lastMatch = (String) matchingSet.last();

		// todo what about number format exceptions ?? with the patterns, this is - in theory - been taken care of.
		//
    Integer highestDigit = new Integer(lastMatch.substring(lastMatch.length() - 1));
		int nextDigit = (highestDigit.intValue() + 1);

		// Step 4 : replace the old version with the new version
		//
		String oldVersion = ((SourceModule) module).getVersion().getVersionIdentifier();
		int index = oldVersion.lastIndexOf("-");
		String newVersion = oldVersion.substring(0, index + 1) + nextDigit;

		return newVersion;
	}

	public String getNextVersion(Module module) throws KarmaException {
		return getNext(module);
	}
}
