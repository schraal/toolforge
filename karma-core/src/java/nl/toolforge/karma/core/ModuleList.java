package nl.toolforge.karma.core;

import java.util.ArrayList;

/**
 * <code>List</code> implementation containing <code>Module</coide> instances.
 *
 * @author D.A. Smedes
 */
public class ModuleList extends ArrayList
{
	/**
	 * Javadoc inherited.
	 */
	public void add(Module module) {
		super.add(module);
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
}
