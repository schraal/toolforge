package nl.toolforge.karma.core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <code>List</code> implementation containing <code>Module</coide> instances.
 *
 * @author D.A. Smedes
 */
public final class ModuleList extends ArrayList
{
	/**
	 * Javadoc inherited.
	 */
	public void add(Module module) {

		if ((module instanceof SourceModule) || (module instanceof JarModule)) {
			super.add(module);
		}

		throw new KarmaRuntimeException("Module-type should be SourceModule or JarModule.");
	}

	/**
	 * Javadoc inherited.
	 */
	public void add(int index, Module module) {
		super.add(index, module);
	}

	/**
	 * Javadoc inherited.
	 */
	public boolean addAll(ModuleList moduleList) {
		return super.addAll(moduleList);
	}

	/**
	 * Javadoc inherited.
	 */
	public boolean addAll(int index, ModuleList moduleList) {
		return super.addAll(index, moduleList);
	}

	/**
	 * Javadoc inherited.
	 */
	public void set (int index, Module module) {
		super.set(index, module);
	}

	/**
	 * Javadoc inherited.
	 */
	public Iterator iterator() {
		return super.iterator();
	}

	/**
	 * Returns all <code>SourceModule</code> instances in this list.
	 *
	 * @return All <code>SourceModule</code> instances in this list or an empty list if none exists.
	 */
	public ModuleList getSourceModules() {

		ModuleList list = new ModuleList();

		for (Iterator i= super.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof SourceModule) {
				list.add(o);
			}
		}
		return list;
	}

	/**
	 * Returns all <code>JarModule</code> instances in this list.
	 *
	 * @return All <code>JarModule</code> instances in this list or an empty list if none exists.
	 */
	public ModuleList getJarModules() {

		ModuleList list = new ModuleList();

		for (Iterator i= super.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof JarModule) {
				list.add(o);
			}
		}
		return list;
	}

}
