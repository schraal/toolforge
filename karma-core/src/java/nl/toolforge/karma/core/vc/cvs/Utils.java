package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.model.MainLine;

public final class Utils {

	/**
	 * <p>Creates a symbolic name for <code>module</code>, based on <code>version</code>. The format of the symbolic name
	 * is determined by ... todo to be implemented.
	 * <p/>
	 * <p>Right now, symbolic name stuff is supported for <code>SourceModule</code>s, so if this method is supplied with
	 * another <code>module</code> type, the method returns an empty <code>CVSTage</code>.
	 */
	public static SymbolicName createSymbolicName(Module module, Version version) {

		if (module instanceof SourceModule) {

			SourceModule m = (SourceModule) module;

			if (m.hasDevelopmentLine()) {
				return new CVSTag(m.getDevelopmentLine().getName() + "_" + version.getVersionNumber());
			}
			return new CVSTag(MainLine.NAME_PREFIX + "_" + version.getVersionNumber());
		}
		return new CVSTag("");
	}
}
