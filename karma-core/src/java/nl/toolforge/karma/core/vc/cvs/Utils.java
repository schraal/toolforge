package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.model.MainLine;

public final class Utils {

  /**
   * <p>Creates a symbolic name for <code>module</code>, based on <code>version</code> and whether a module has an
   * assocated <code>DevelopmentLine</code>.</p>
   *
   * <p>Right now, symbolic name stuff is supported for <code>SourceModule</code>s, so if this method is supplied with
   * another <code>module</code> type, the method returns an empty <code>CVSTag</code>.
   *
   * @param module The module at hand.
   * @param version The version (within the modules' development line. <code>version</code> may be <code>null</code> in
   *   which case it is ignored when creating a <code>SymbolicName</code>.
   *
   * @return A symbolic name (tag) as they are used in a CVS repository. An empty CVSTag is returned when another
   *  instance than a {@link SourceModule} is passed as the <code>module</code> parameter.
   */
  public static SymbolicName createSymbolicName(Module module, Version version) {

    if (module instanceof SourceModule) {

      SourceModule m = (SourceModule) module;

      if (version == null) {
        if (m.hasDevelopmentLine()) {
          return new CVSTag(m.getDevelopmentLine().getName());
        } else {
          // We are using the TRUNK.
          //
          new CVSTag("");
        }
      } else {
        if (m.hasDevelopmentLine()) {
          return new CVSTag(m.getDevelopmentLine().getName() + "_" + version.getVersionNumber());
        } else {
          return new CVSTag(MainLine.NAME_PREFIX + "_" + version.getVersionNumber());
        }
      }
    }
    return new CVSTag("");
  }
}
