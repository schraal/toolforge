package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.vc.VersionExtractor;
import org.netbeans.lib.cvsclient.command.log.LogInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author D.A. Smedes
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

  private CVSVersionExtractor() {
  }

  /**
   * <p>See {@link VersionExtractor#getNextVersion}.
   * <p/>
   * <p>Connects to the correct CVS repository and determines the next version in the branch (if applicable, otherwise
   * it is the trunk) for the module. This is done by quering <code>module.info</code>.
   *
   * @param module The next version number <code>module</code>.
   * @return The next version for <code>module</code>.
   * @throws CVSException When the version is not found for the module in the repository.
   */
  private static synchronized Version getNext(SourceModule module) throws CVSException {

    List matchingList = collectVersions(module);

    if (matchingList.size() == 0) {
      return Version.INITIAL_VERSION;
//      throw new CVSException(CVSException.VERSION_NOT_FOUND, new Object[] { module.getVersionAsString(), module.getName() });
    }

    Version lastMatch = (Version) matchingList.get(matchingList.size() - 1);
    lastMatch.setDigit(lastMatch.getLastDigitIndex(), lastMatch.getLastDigit() + 1);

    return lastMatch;
  }

  public Version getNextVersion(SourceModule module) throws CVSException {
    return getNext(module);
  }

  /**
   * <p>Connects to the correct CVS repository and determines the last version in the branch (if applicable, otherwise
   * it is the trunk) for the module. This is done by quering <code>module.info</code>.
   *
   * @param module The next version number <code>module</code>.
   * @return The next version for <code>module</code> or <code>null</code> when none is found.
   * @throws KarmaException TODO complete when implementation is ready.
   */
  private static synchronized Version getLast(Module module) throws KarmaException {

    List matchingList = collectVersions(module);

    if (matchingList.size() == 0) {
      throw new CVSException(CVSException.VERSION_NOT_FOUND);
    }

    Version lastMatch = (Version) matchingList.get(matchingList.size() - 1);
    lastMatch.setDigit(lastMatch.getLastDigitIndex(), lastMatch.getLastDigit());

    return lastMatch;
  }

  public Version getLastVersion(Module module) throws KarmaException {
    return getLast(module);
  }

  private static List collectVersions(Module module) throws CVSException {

//		if (module instanceof SourceModule) {
//			if (!((SourceModule) module).hasModuleInfo()) {
//				throw new KarmaException(KarmaException.NO_MODULE_INFO, new Object[]{module.getName()});
//			}
//		}

    // todo : this method should probably check if the module is at all compliant with the standards set by Karma.
    // for instance, are the basic version info and branch available ???
    //

    // Step 1 : get all symbolicnames that apply to the correct pattern
    //
    //
    List matchingList = new ArrayList();

    CVSRunner runner = null;
    try {
      // todo what is the effect of passing a the following instance of CommandResponseListener ??
      //
//      CommandResponseListener listener = new CommandResponseListener() {
//        public void commandHeartBeat() {}
//        public void commandResponseChanged(CommandResponseEvent event) { }
//        public void commandResponseFinished(CommandResponseEvent event) { }
//      };

//      runner = new CVSRunner(module.getLocation(), new File(""), listener); // baseLocation doesn't matter ...
      runner = new CVSRunner(module.getLocation(), new File("")); // baseLocation doesn't matter ...
    } catch (KarmaException e) {
      throw new CVSException(null); // TODO to be defined (errorcode).
    }

    LogInformation logInformation = runner.log(module);
    Collection currentVersions = logInformation.getAllSymbolicNames();


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

    // Collect all applicable symbolic names.
    //
    for (Iterator it = currentVersions.iterator(); it.hasNext();) {

      LogInformation.SymName s = (LogInformation.SymName) it.next();

      Matcher matcher = pattern.matcher(s.getName());
      if (matcher.matches()) {
        matchingList.add(new Version(s.getName().substring(s.getName().lastIndexOf("_") + 1)));
      }
    }

    // Step 2 : Sort them, so the last one is on top (or something).
    //

    Collections.sort(matchingList);

    return matchingList;
  }

}
