package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.Patch;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.model.MainLine;

public final class Utils {

  /**
   * <p>Creates a symbolic name for <code>module</code>, based on <code>version</code> and whether a module has an
   * associated <code>DevelopmentLine</code>.</p>
   *
   * <p>Right now, symbolic name stuff is supported for <code>SourceModule</code>s, so if this method is supplied with
   * another <code>module</code> type, the method returns an empty <code>CVSTag</code>.
   *
   * @param module The module at hand.
   * @param developmentLine The development line to which the version applies.
   * @param version The version (within the modules' development line. <code>version</code> may be <code>null</code> in
   *   which case it is ignored when creating a <code>SymbolicName</code>.
   *
   * @return A symbolic name (tag) as they are used in a CVS repository. An empty CVSTag is returned when another
   *  instance than a {@link SourceModule} is passed as the <code>module</code> parameter.
   */
  public static SymbolicName createSymbolicName(Module module, DevelopmentLine developmentLine, Version version) {

//    if (module instanceof SourceModule) {

    if (version == null) {
      new CVSTag("");
      if (developmentLine != null) {
        return new CVSTag(developmentLine.getName());
      } else {
        // We are using the TRUNK.
        //
        new CVSTag("");
      }
    } else {
      if (developmentLine != null) {

        if (developmentLine instanceof PatchLine) {
          if (!(version instanceof Patch)) {
            throw new KarmaRuntimeException(
                "The provided developmentLine is a PatchLine instance. This implies " +
                "that the provided version should be a Patch instance.");
          }
          return createSymbolicName((Patch)version);
        } else {
          return new CVSTag(developmentLine.getName() + "_" + version.getVersionNumber());
        }
      } else {
        return new CVSTag(MainLine.NAME_PREFIX + "_" + version.getVersionNumber());
      }
    }
    return new CVSTag("");
  }

  public static SymbolicName createSymbolicName(Module module,  Version version) {
    return Utils.createSymbolicName(module, null, version);
  }

  public static SymbolicName createSymbolicName(Patch patch) {
    return new CVSTag(PatchLine.NAME_PREFIX + PatchLine.PATCH_SEPARATOR + patch.getVersionNumber());
  }

  public static Version getLastVersion(Module module) throws VersionControlException {

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
    return status.getLastVersion();
  }

  public static Version getLocalVersion(Module module) throws VersionControlException {

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
    return status.getLocalVersion();
  }

    public static boolean existsInRepository(Module module) throws VersionControlException {

    Runner runner = RunnerFactory.getRunner(module.getLocation());

    //todo refactor this logic.
    //the idea now is that the retrieval of status would have given an exception when
    //the module was not in the repo.
    ModuleStatus status = new CVSModuleStatus(module, ((CVSRunner) runner).log(module));
//    return status.existsInRepository();
    return true;
  }
}
