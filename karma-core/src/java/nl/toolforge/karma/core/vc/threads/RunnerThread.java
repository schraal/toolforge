package nl.toolforge.karma.core.vc.threads;

import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.manifest.Module;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class RunnerThread extends Thread {

  private boolean running = true;

  protected ModuleStatus moduleStatus = null;
  private Module module = null;

  /**
   * Must be implemented by subclasses. The subclass is where all logic is performed.
   */
  public abstract void run();

  /**
   * Should be called to demarkate the start of the 'transaction'.
   */
  protected void startRunning() {
    running = true;
  }

  /**
   * Should be called to demarkate the end of the 'transaction'.
   */
  protected void stopRunning() {
    running = false;
  }

  /**
   * Should be checked to ensure that <code>run()</code> is finished.
   *
   * @return
   */
  public final boolean isRunning() {
    return running;
  }

  public final ModuleStatus getModuleStatus() {
    return moduleStatus;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  /**
   * Returns the module status, irrespective of whether this thread has finished executing or not. This check can be
   * performed by calling {@link #isRunning()}.
   *
   * @return The ModuleStatus instance generated based on what the {@link #run()} has done when executed properly.
   */
  public Module getModule() {
    return module;
  }

}
