package nl.toolforge.karma.core.vc.threads;

import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.cvs.threads.CVSRunnerThread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The ParallelRunner handles parallel <code>RunnerThread</code>s. This concept should be used when parallel read
 * actions on version control repositories are possible to speed up the process of performing commands for
 * <strong>each</strong> module in a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ParallelRunner {

  private Map statusOverview = null;

  private Manifest manifest = null;
  private RunnerThread[] threads = null;

  private boolean initializing = true;

  /**
   * Initializes this <code>ParallelRunner</code> with the correct <code>Manifest</code>.
   *
   * @param manifest The manifest.
   */
  public ParallelRunner(Manifest manifest) {
    this.manifest = manifest;
  }

  /**
   *
   */
  public void execute() {

    // todo abstract should work for all subtypes...

    Map modules = manifest.getAllModules();

    // Initialize status overview map
    //
    statusOverview = new HashMap();

    // Initialize an array of threads.
    //
    threads = new RunnerThread[modules.size()];

    int index = 0;

    // Start each task in parallel ...
    //

    initializing = true;

    for (Iterator i = modules.values().iterator(); i.hasNext();) {
      threads[index] = new CVSRunnerThread((Module) i.next());
      threads[index].start();
      index++;
    }

    initializing = false;
  }

  private void addStatus(Module module, ModuleStatus moduleStatus) {
    statusOverview.put(module, moduleStatus);
  }

  /**
   * Returns a map of {@link nl.toolforge.karma.core.vc.ModuleStatus} objects, each accessible by the the corresponding
   * {@link nl.toolforge.karma.core.manifest.Module} instance.
   *
   * @return
   */
  public Map retrieveStatus() {
    return statusOverview;
  }

  /**
   * Determines if all tasks are finished. This method is non-blocking and should be called continuously if all tasks
   * are required to have finished before the transaction is complete.
   *
   * @return <code>true</code> if all tasks are finished.
   */
  public boolean finished() {

    if (initializing) {
      // This class should have started all threads first, before this method can execute properly.
      //
      return false;
    }

    int totalThreads = threads.length;
    int runningThreads = threads.length;

    for (int i = 0; i < totalThreads; i++) {

      boolean runningThread = threads[i].isRunning();
      runningThreads -= (runningThread ? 0 : 1);

      if (!runningThread) {
        addStatus(threads[i].getModule(), threads[i].getModuleStatus());
      }
    }
    return (runningThreads == 0);
  }
}
