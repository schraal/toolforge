/*
* Created on Oct 17, 2004
*/
package nl.toolforge.karma.core.cmd;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author W.M. Oosterom
 */
public class CommandDescriptorMap {

  private Map map = new HashMap();

  public CommandDescriptorMap() {
    // Empty
  }

  public void add(CommandDescriptor newDescriptor) {

    if (newDescriptor == null) {
      return;
    }

    // Create a set of identifiers for this command descriptor
    //
    Set identifiers = new HashSet();
    if (newDescriptor.getName() != null) {
      identifiers.add(newDescriptor.getName());
    }
    if (newDescriptor.getAliasList() != null) {
      identifiers.addAll(newDescriptor.getAliasList());
    }
    identifiers.remove(null);

    // If map already contains a key, that is also found in identifiers
    // than do not add the current command descriptor, but add the current
    // aliases that were not yet registered to the commanddescriptor that
    // was already put in map before
    //
    String alreadyContainsKey = null;
    for (Iterator i = identifiers.iterator(); i.hasNext();) {
      String currentAlias = (String) i.next();
      if (map.containsKey(currentAlias)) {
        alreadyContainsKey = currentAlias;
      }
    }
    if (alreadyContainsKey != null) {
      // map already contains this command descriptor; add
      // missing aliases
      //
      CommandDescriptor existingDescriptor = (CommandDescriptor) map.get(alreadyContainsKey);
      for (Iterator i = identifiers.iterator(); i.hasNext();) {
        String currentAlias = (String) i.next();
        if (!map.containsKey(currentAlias)) {
          map.put(currentAlias, existingDescriptor);
        }
      }
    } else {
      // map did not yet contains this command descriptor
      //
      for (Iterator i = identifiers.iterator(); i.hasNext();) {
        map.put(i.next(), newDescriptor);
      }
    }
  }

  /**
   * Adds all objects in <code>anotherMap</code> to this <code>CommandDescriptorMap</code>.
   *
   * @param anotherMap A <code>CommandDescriptorMap</code> instance.
   */
  public void addAll(CommandDescriptorMap anotherMap) {

    for (Iterator i = anotherMap.keySet().iterator(); i.hasNext();) {
      String key = (String) i.next();
      map.put(key, anotherMap.get(key));
    }
  }

  /**
   * Returns this <code>CommandDescriptorMap</code>s values.
   */
  public Collection values() {
    return map.values();
  }

  /**
   * Returns a Collection view of all keys in this <code>CommandDescriptorMap</code>.
   *
   * @return A Collection view of all keys in this <code>CommandDescriptorMap</code>.
   */
  public Collection keySet() {
    return map.keySet();
  }

  /**
   * Returns a <code>CommandDescriptor</code> instance identified by the key <code>name</code>.
   *
   * @param name A command descriptor name. Acts as key for the <code>CommandDescriptorMap</code>.
   *
   * @return
   */
  public CommandDescriptor get(String name) {
    if (name == null) {
      return null;
    }
    return (CommandDescriptor) map.get(name);
  }

  /**
   * Returns the number of keys in this <code>CommandDescriptorMap</code>.
   *
   * @return The number of keys in this <code>CommandDescriptorMap</code>.
   */
  public int size() {
    return keySet().size();

  }
}