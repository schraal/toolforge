package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.test.BaseTest;

/**
 * Test <code>CVSLocationImpl</code> instances.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class TestCVSLocationImpl extends BaseTest {

//	public void testConstructor() {
//
//		CVSLocationImpl c = new CVSLocationImpl("a");
//
//		try {
//
//			c.getCVSRootAs();
//			fail("Bad CVSROOT.");
//
//		} catch (SVNException e) {
//			assertTrue("Expecting SVNException", true);
//		}
//	}

//	public void testPort() {
//
////		try {
////			Properties p = new Properties();
////			p.load(getClass().getClassLoader().getSystemResourceAsStream("resources/test/karma.properties"));
////			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + p.toString());
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//
//		CVSLocationImpl c = new CVSLocationImpl("a");
//
//		c.setPort("333");
//		assertEquals(333, c.getPort());
//
//		c.setPort("DDDD");
//		assertEquals(CVSLocationImpl.DEFAULT_PORT, c.getPort());
//	}

//	public void testProtocol() {
//
//		CVSLocationImpl c = new CVSLocationImpl("a");
//
//		c.setProtocol(null);
//		assertEquals(CVSLocationImpl.DEFAULT_PROTOCOL, c.getProtocol());
//	}

	public void testgetCVSROOT1() {

		CVSLocationImpl c = new CVSLocationImpl("a");
		c.setProtocol("local");
		c.setRepository("/home/cvsroot");

		try {
			assertEquals(":local:/home/cvsroot", c.getCVSRootAsString());
		} catch (CVSException e) {
			fail(e.getMessage());
		}
	}
}