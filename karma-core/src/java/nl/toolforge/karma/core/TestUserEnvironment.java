package nl.toolforge.karma.core;

import junit.framework.TestCase;

import java.io.File;

/**
 *
 * @author D.A. Smedes
 */
public class TestUserEnvironment extends TestCase {

    public void testGetKarmaHomeDirectory1() {
        UserEnvironment u = UserEnvironment.getInstance(false);

        assertTrue(u.getKarmaHomeDirectoryAsString().equals(System.getProperty("user.home").concat(File.separator).concat("karma-projects")));
    }

    public void testGetKarmaHomeDirectory2() {

        // Fake 'java -Dkarma.home.directory=/home/asmedes/dev/projects
        //
        System.setProperty("karma.development.home", "/home/asmedes/dev/projects/");

        UserEnvironment u = UserEnvironment.getInstance(false);
        assertEquals(u.getKarmaHomeDirectoryAsString(),
                File.separator + "home" +
                File.separator + "asmedes" +
                File.separator + "dev" +
                File.separator + "projects");
    }

    public void testGetKarmaHomeDirectory3() {

        UserEnvironment u = UserEnvironment.getInstance(false);

        try {
            //assertTrue(u.getDevelopmentHome().equals());
        } catch (NullPointerException n) {
            fail();
        }
    }

    public void testGetOperationSystemFamily() {

        UserEnvironment u = UserEnvironment.getInstance(false);

        //System.out.println(Os.getFamily(System.getProperty("os.name")).getName());

        try {
            assertTrue(u.getOperationSystem().equals(System.getProperty("os.name")));
        } catch (Exception e) {
            fail("Exceptions should not occur here.");
        }
    }
}
