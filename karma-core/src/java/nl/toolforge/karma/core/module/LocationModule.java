package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.KarmaRuntimeException;

import java.util.Set;

/**
 * 
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class LocationModule extends AdminModule {

  public LocationModule(String moduleName, Location location) {
    super(moduleName, location);
  }


  public ModuleLayoutTemplate getLayoutTemplate() {
   throw new KarmaRuntimeException("To be implemented ...");
  }


  public void createRemote(Authenticator authenticator, String createComment) throws VersionControlException, AuthenticationException {

  }











  // Should be removed after Module is better ......




  public Type getType() {
    return null;
  }

  public DevelopmentLine getPatchLine() {
    return null;
  }

  public void markPatchLine(boolean mark) {

  }

  public boolean hasPatchLine() {
    return false;
  }

  public boolean hasDevelopmentLine() {
    return false;
  }

  public void markDevelopmentLine(boolean mark) {

  }

  public Version getVersion() {
    return null;
  }

  public String getVersionAsString() {
    return null;
  }

  public boolean hasVersion() {
    return false;
  }

  public Set getDependencies() {
    return null;
  }

  public void create() {

  }

  public void createRemote(String createComment) throws VersionControlException {

  }
}
