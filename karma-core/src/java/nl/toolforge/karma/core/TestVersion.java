package nl.toolforge.karma.core;

import junit.framework.TestCase;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class TestVersion extends TestCase {

	public void testConstuctor1() {

		Version v = new Version("0-1");
		assertEquals("0-1", v.getVersionNumber());
	}

	public void testConstuctor2() {

		Version v = new Version(new int[]{0, 1, 3});
		assertEquals("0-1-3", v.getVersionNumber());
	}

	public void testCompare() {

		Version v1 = new Version("0-2-6");
		Version v2 = new Version("1-1-1");
		Version v3 = new Version("0-2-8");

		List s = new ArrayList();
		s.add(v1);
		s.add(v2);
		s.add(v3);

    Collections.sort(s);

		assertEquals(v1, (Version) s.get(0));
		assertEquals(v2, (Version) s.get(2));
		assertEquals(v3, (Version) s.get(1));
	}

}
