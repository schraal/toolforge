package nl.toolforge.karma.core;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <code>List</code> implementation containing <code>Module</coide> instances.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ModuleMap extends HashMap
{
	/**
	 * Javadoc inherited.
	 */
	public final void add(Module module) {

		if ((module instanceof SourceModule) || (module instanceof JarModule)) {

			super.put(module.getName(), module);

			return;
		}

		throw new KarmaRuntimeException("Module-type should be SourceModule or JarModule.");
	}

	/**
	 * Javadoc inherited.
	 */
	public final void put (Module module) {
		super.put(module.getName(), module);
	}

//	/**
//	 * Retrieves an iterator over
//	 */
//	public final Iterator iterator() {
//		return super.valuesiterator();
//	}

	/**
	 * Javadoc inherited.
	 */
	public final int size() {
		return super.size();
	}

	/**
	 * Returns all <code>SourceModule</code> instances in this list.
	 *
	 * @return All <code>SourceModule</code> instances in this list or an empty list if none exists.
	 */
	public final ModuleMap getSourceModules() {

		ModuleMap list = new ModuleMap();

		for (Iterator i= super.values().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SourceModule) {
				list.put(((Module) o).getName(), o);
			}
		}
		return list;
	}

	/**
	 * Returns all <code>JarModule</code> instances in this list.
	 *
	 * @return All <code>JarModule</code> instances in this list or an empty list if none exists.
	 */
	public final ModuleMap getJarModules() {

		ModuleMap list = new ModuleMap();

		for (Iterator i= super.values().iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof JarModule) {
				list.put(((Module) o).getName(), o);
			}
		}
		return list;
	}

}
