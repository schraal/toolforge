package nl.toolforge.karma.launcher;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author W.M. Oosterom
 */
final class RemoteRepository {
    // Singleton implementation
    //
    private static RemoteRepository instance = null;

    /**
     * Get the remote repository. The locations can be specified in the
     * following ways 1) From a file loaded from the classpath called
     * "karma.repository.remote" 2) In a file
     * $HOME/.karma/karma-launcher.properties (overrides the file loaded from the
     * classpath) 3) The system property karma.repository.remote (overrides all)
     * remote locations will be searched in reverse order, so the repository
     * defined as a system property will be searched first, next the ones
     * specified in a local configuration file and last the ones specified in
     * the file available in the classpath.
     */
    static synchronized RemoteRepository getInstance() throws IOException {
        if (instance == null) {
            instance = new RemoteRepository();
        }
        return instance;
    }

    private static final String REPOSITORY_FILE = "karma-launcher.properties";

    private static final String REPOSITORY_PROPERTY = "karma.repository.remote";

    private List remoteRepositories = new ArrayList();

    private RemoteRepository() throws IOException {

        InputStream in = null;

        // If there is a system property, we should add it to
        // the remote repository list.
        //
        String systemProperty = System.getProperty("karma.repository.remote");
        if (systemProperty != null) {
            if (!remoteRepositories.contains(systemProperty)) {
                remoteRepositories.add(systemProperty);
                System.out.println("Adding a remote repository \""
                        + systemProperty + "\"");
            }
        }

        // First try to load a list of repositories from the classpath
        //
        if ((in = this.getClass().getClassLoader().getResourceAsStream(
                "karma.repository.remote")) != null) {
            readRepositories(in);
            in.close();
        }

        // Try to read the $HOME/.karma/karma.repository.remote file.
        //
        File f = new File(System.getProperty("user.home") + File.separator
                + ".karma" + File.separator + "karma.repository.remote");
        if (f.exists()) {
            in = new FileInputStream(f);
            readRepositories(in);
            in.close();
        }

    }

    private void readRepositories(InputStream in) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (!line.startsWith("#") && !line.equals("")) {
                if (!remoteRepositories.contains(line)) {
                    remoteRepositories.add(line);
                }
                System.out.println("Adding a remote repository \"" + line
                        + "\"");
            }
        }
    }

    File getJarByVersion(String id, String version) throws IOException {
        return getJar(id + "/jars/" + id + "-" + version + ".jar");
    }

    File getJarByName(String id, String name) throws IOException {
        return getJar(id + "/jars/" + name);
    }

    private File getJar(String name) throws IOException {
        int i = 0;
        boolean retrievedJar = false;
        File tmpFile = null;

        try {
            tmpFile = File.createTempFile("RemoteRepository", null);
            tmpFile.deleteOnExit();

            String urlString = null;

            while ((i < remoteRepositories.size()) && !retrievedJar) {
                try {
                    urlString = (String) remoteRepositories.get(i);
                    if (!urlString.endsWith("/")) {
                        urlString += "/";
                    }
                    urlString += name;

                    System.out.println("Trying to retrieve jar from url \""
                            + urlString + "\"");

                    URL url = new URL(urlString);

                    String md5 = HttpUtil.get(url, tmpFile);

                    System.out.println("Successfully retrieved jar from url \""
                            + urlString + "\", MD5=\"" + md5 + "\"");

                    // Try to get a MD5 file from the location from which the
                    // jar was obtained
                    // If the MD5 exists and we were able to retrieve it, we
                    // need to compare
                    // the MD5 of the jar with the retrieved MD5. If they are
                    // equal, than
                    // the retrieval of the jar was successfull, otherwise it
                    // failed.
                    //
                    String referenceMd5 = null;
                    if ((referenceMd5 = getMd5ForJar(urlString + ".md5")) != null) {
                        if (!referenceMd5.equals(md5)) {
                            throw new IOException("MD5 check failed, found \""
                                    + md5 + "\" should have been \""
                                    + referenceMd5 + "\"");
                        } else {
                            System.out.println("MD5 was verified.");
                        }
                    } else {
                        System.out.println("MD5 could not be verified.");
                    }

                    retrievedJar = true;
                } catch (IOException ioe) {
                    System.out.println("Failed to retrieve jar from url \""
                            + urlString + "\" due to " + "\""
                            + ioe.getMessage() + "\"");
                }
                i += 1;
            }
        } catch (IOException e) {
            tmpFile.delete();
            throw e;
        }

        if (retrievedJar) {
            return tmpFile;
        } else {
            throw new FileNotFoundException("Could not retrieve \"" + name
                    + "\"");
        }
    }

    private static String getMd5ForJar(String md5UrlString) {
        String referenceMd5 = null;
        File tmpFile = null;

        try {
            URL url = new URL(md5UrlString);

            tmpFile = File.createTempFile("RemoteRepository", null);
            tmpFile.deleteOnExit();

            HttpUtil.get(url, tmpFile);

            referenceMd5 = (new BufferedReader(new FileReader(tmpFile)))
                    .readLine().trim();

        } catch (Exception anyException) {
            // Whatever the exception, the md5 lookup failed, so we should
            // discard it and return null;
            //
            return null;
        } finally {
            tmpFile.delete();
        }

        return referenceMd5;
    }
}

