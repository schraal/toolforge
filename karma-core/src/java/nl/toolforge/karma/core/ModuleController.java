package nl.toolforge.karma.core;


/**
 * The <code>ModuleController</code> controls all access to and from a local harddisk for a
 * <code>Module</code>. Modules need filesystem access to create directory structures, write
 * files, etc. This task is solely assigned to this class.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ModuleController {

	private Module moduleReference = null;

	/**
	 * Constructor. The modulecontroller is initialized with a <code>Module</code> instance so it can
	 * prepare itself for full operational mode.
	 *
	 * @param module
	 */
	public ModuleController(Module module) {

		moduleReference = module;
	}
}
