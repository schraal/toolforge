package nl.toolforge.karma.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * The Repository represents the local storage of jar files.
 * 
 * @author W.M. Oosterom
 */
class LocalRepository {
    // Singleton implementation
    //
    private static LocalRepository instance = null;

    /**
     * Get the local repository. The default location is 1)
     * $HOME/.karma/repository. This default location can be overridden by the
     * following settings: 2) $HOME/.karma/karma-launcher.properties (overrides
     * default) 3) The system property karma.repository.local (override all)
     */
    static synchronized LocalRepository getInstance() throws IOException {
        if (instance == null) {
            instance = new LocalRepository();
        }
        return instance;
    }

    private static final String REPOSITORY_FILE = "karma-launcher.properties";

    private static final String REPOSITORY_PROPERTY = "karma.repository.local";

    private static final String defaultRepositoryLocation = System
            .getProperty("user.home")
            + File.separator + ".karma" + File.separator + "repository";

    private String repositoryLocation = defaultRepositoryLocation;

    private LocalRepository() throws IOException {

        // If there is a $HOME/.karma/karma-launcher.properties file, the
        // karma.repository.local property is supposed to
        // point to a repository location. This will override the default
        // repository location
        //
        File f = new File(System.getProperty("user.home") + File.separator
                + ".karma" + File.separator + REPOSITORY_FILE);

        if (f.exists()) {
            Properties props = new Properties();
            props.load(new FileInputStream(f));

            repositoryLocation = props.getProperty(REPOSITORY_PROPERTY,
                    repositoryLocation);
        }

        // If there is a system property called karma.repository.local, the
        // value of that system
        // property is supposed to point to a repository location. This will
        // override
        // all previous settings.
        //
        repositoryLocation = System.getProperty(REPOSITORY_PROPERTY,
                repositoryLocation);

        if (repositoryLocation.equals(defaultRepositoryLocation)) {
            // We need to be sure the defaultRepositoryLocation exists
            //
            (new File(defaultRepositoryLocation)).mkdirs();
        }

        //System.out.println("Repository location set to \"" +
        // repositoryLocation + "\".");
    }

    File getJarByVersion(String id, String version)
            throws FileNotFoundException {

        File jarFile = new File(repositoryLocation + File.separator + id
                + File.separator + "jars" + File.separator + id + "-" + version
                + ".jar");

        if (jarFile.exists()) {
            return jarFile;
        } else {
            throw new FileNotFoundException(jarFile.getName());
        }
    }

    File getJarByName(String id, String name) throws FileNotFoundException {

        File jarFile = new File(repositoryLocation + File.separator + id
                + File.separator + "jars" + File.separator + name);

        if (jarFile.exists()) {
            return jarFile;
        } else {
            throw new FileNotFoundException(jarFile.getName());
        }
    }

    void putJarByVersion(String id, String version, File jarFile)
            throws IOException {

        (new File(repositoryLocation + File.separator + id + File.separator
                + "jars")).mkdirs();
        File dest = new File(repositoryLocation + File.separator + id
                + File.separator + "jars" + File.separator + id + "-" + version
                + ".jar");
        FileUtil.copy(jarFile, dest);
    }

    void putJarByName(String id, String name, File jarFile) throws IOException {

        (new File(repositoryLocation + File.separator + id + File.separator
                + "jars")).mkdirs();
        File dest = new File(repositoryLocation + File.separator + id
                + File.separator + "jars" + File.separator + name);
        FileUtil.copy(jarFile, dest);
    }

}

