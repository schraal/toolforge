package nl.toolforge.karma.launcher;

import java.security.Permission;

/**
 * A security manager that grants everything.
 * 
 * @author W.M. Oosterom
 */
public final class KarmaLauncherSecurityManager extends SecurityManager {

    public void checkPermission(Permission permission) {
        // Silently grant all
    }
}