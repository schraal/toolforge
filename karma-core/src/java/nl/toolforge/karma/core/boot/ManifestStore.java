/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package nl.toolforge.karma.core.boot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;
import nl.toolforge.karma.core.module.BaseModule;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * Class representing the manifest store for a working context. A manifest store contains module(s) which - in turn -
 * contain manifest files (albeit in
 *
 * @author D.A. Smedes
 * @version $Id$
 *
 * @since Karma 1.0
 */
public final class ManifestStore extends AdminStore {

  public ManifestStore(WorkingContext workingContext) {
    super(workingContext);
  }

  public ManifestStore(WorkingContext workingContext, String moduleName, Location location) {
    super(workingContext, moduleName, location);
  }

  /**
   *
   * @return
   */
  public List getManifestFiles() {
    return new ArrayList();
  }

  public final Module getModule() {

    if (module != null) {
      return module;
    }

    if (getModuleName() == null || "".equals(getModuleName())) {
      throw new KarmaRuntimeException("Module name for manifest store has not been set (correctly).");
    }

    // Names for stores can contain an offset.
    //
    String name = getModuleName();
    while (name.endsWith(File.separator)) {
      name.substring(0, name.length());
    }
    if (name.lastIndexOf(File.separator) > 0) {
      name = name.substring(name.lastIndexOf(File.separator) + 1);
    }

    module = new ManifestModule(name, getLocation());
    module.setBaseDir(new File(getWorkingContext().getManifestStoreBasedir(), getModuleName()));

    return module;
  }

  /**
   * Commits the manifest file identified by <code>releaseName</code> to the repository. The file
   *
   * @param releaseName The name of the release that should be committed.
   * @throws AuthenticationException
   * @throws VersionControlException
   */
  public void commit(String releaseName) throws AuthenticationException, VersionControlException {

    File file = new File(module.getBaseDir(), releaseName + ".xml");

    Runner runner = RunnerFactory.getRunner(getLocation());
    runner.commit(file);
  }

  protected class ManifestModule extends BaseModule {

    public ManifestModule(String name, Location location) {
      super(name, location);
    }

    public ModuleLayoutTemplate getLayoutTemplate() {
      return null;
    }
  }

}
