package nl.toolforge.karma.core.scm;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleDependency extends TestCase {

  public void testEquals() {

    ModuleDependency d1 = new ModuleDependency();
    d1.setModule("child");

    ModuleDependency d2 = new ModuleDependency();
    d2.setModule("baby");

    assertFalse(d1.equals(d2));
  }

  public void testEquals2() {

    ModuleDependency d1 = new ModuleDependency();
    d1.setGroupId("log4j");
    d1.setArtifactId("log4j");
    d1.setVersion("1.2.8");

    ModuleDependency d2 = new ModuleDependency();
    d2.setGroupId("log4j");
    d2.setArtifactId("log4j");
    d2.setVersion("1.2.8");

    assertEquals(d1, d2);
  }

  public void testComplexHashCode() {

    ModuleDependency d1 = new ModuleDependency();
    d1.setGroupId("log4j");
    d1.setArtifactId("log4j");
    d1.setVersion("1.2.8");

    ModuleDependency d2 = new ModuleDependency();
    d2.setGroupId("log4j");
    d2.setArtifactId("log4j");
    d2.setVersion("1.2.9");

    Set blaat = new HashSet();
    blaat.add(d1);
    blaat.add(d2); // Should fail, due to the hashCode() algorithm.

    assertEquals(d1.hashCode(), d2.hashCode());
    assertEquals(1, blaat.size());
  }

}
