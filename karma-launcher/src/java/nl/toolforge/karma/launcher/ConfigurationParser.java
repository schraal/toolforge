package nl.toolforge.karma.launcher;

import java.io.*;

/**
 * 
 * @author W.M. Oosterom
 */
final class ConfigurationParser {
    private KarmaClassLoaderRepository classLoaderRepository = null;

    ConfigurationParser() {
        // Empty
    }

    void parse(InputStream in, KarmaClassLoaderRepository repository)
            throws IOException {
        //this.classLoaderRepository = classLoaderRepository;
        this.classLoaderRepository = repository;

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = null;
        String classLoaderName = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Comments are supported
            //
            if (line.equals("") || line.startsWith("#")) {
                continue;
            }

            // ClassLoaders are define in entries like "[<classloader-name>]"
            //
            if (line.startsWith("[") && line.endsWith("]")) {
                classLoaderName = line.substring(1, line.length() - 1).trim();
                classLoaderRepository.addClassLoader(classLoaderRepository
                        .getClassLoader(classLoaderName));

                //System.out.println("Created KarmaClassloader \"" +
                // classLoaderName + "\"");
                continue;
            }

            // If a classLoader name is available, we could to add
            // entries to the classpath, otherwise we will ignore the
            // currentline anyway. An valid entry for a
            //
            if (classLoaderName != null) {
                parseEntry(classLoaderName, line);
            }
        }

        reader.close();
    }

    private void parseEntry(String targetClassLoaderName, String entry)
            throws IOException, FileNotFoundException {
        if (entry.startsWith("dir")) {
            parseDirEntry(targetClassLoaderName, entry);
        }
        if (entry.startsWith("file")) {
            parseFileEntry(targetClassLoaderName, entry);
        }
        if (entry.startsWith("id")) {
            parseIdEntry(targetClassLoaderName, entry);
        }
    }

    /**
     * A directory can be added to the classloader with an entry like "dir =
     * <directory-name>".
     */
    private void parseDirEntry(String targetClassLoaderName, String entry)
            throws FileNotFoundException {
        int i = -1;

        if (((i = entry.indexOf("=")) != -1) && !entry.endsWith("=")) {
            String dirName = entry.substring(i + 1).trim();
            KarmaClassLoader classLoader = (KarmaClassLoader) classLoaderRepository
                    .getClassLoader(targetClassLoaderName);
            classLoader.addRepository(dirName);

            //			System.out.println("Added directory \"" + dirName + "\" to
            // KarmaClassLoader \"" + targetClassLoaderName + "\"");
        }
    }

    /**
     * A file can be added to the classloader with an entry like "file =
     * <file-name>".
     */
    private void parseFileEntry(String targetClassLoaderName, String entry)
            throws FileNotFoundException {
        int i = -1;

        if (((i = entry.indexOf("=")) != -1) && !entry.endsWith("=")) {
            String fileName = entry.substring(i + 1).trim();
            KarmaClassLoader classLoader = (KarmaClassLoader) classLoaderRepository
                    .getClassLoader(targetClassLoaderName);
            classLoader.addRepository(fileName);

            //			System.out.println("Added file \"" + fileName + "\" to
            // KarmaClassLoader \"" + targetClassLoaderName + "\"");
        }
    }

    /**
     * A jar can be added to the classloader with an entry like "id= <id>,
     * version= <version>" or "id= <id>, jar= <jar>"
     */
    private void parseIdEntry(String targetClassLoaderName, String entry)
            throws FileNotFoundException, IOException {

        int i = -1;

        if (((i = entry.indexOf(",")) != -1) && !entry.endsWith(",")) {
            String idPart = entry.substring(0, i).trim();
            String versionOrJarPart = entry.substring(i + 1).trim();

            String id = null;
            if (((i = idPart.indexOf("=")) != -1) && !idPart.endsWith("=")) {
                id = idPart.substring(i + 1).trim();
            }

            String versionOrJar = null;
            if (((i = versionOrJarPart.indexOf("=")) != -1)
                    && !versionOrJarPart.endsWith("=")) {
                versionOrJar = versionOrJarPart.substring(i + 1).trim();
            }

            if ((versionOrJarPart.startsWith("version") || versionOrJarPart
                    .startsWith("jar"))
                    && (id != null) && (versionOrJar != null)) {
                KarmaClassLoader classLoader = (KarmaClassLoader) classLoaderRepository
                        .getClassLoader(targetClassLoaderName);
                Repository repository = Repository.getInstance();

                if (versionOrJarPart.startsWith("version")) {
                    classLoader.addRepository(repository.getJarByVersion(id,
                            versionOrJar));
                    //					System.out.println("Added jar with id \"" + id + "\" and
                    // version \"" + versionOrJar +
                    //						"\" to KarmaClassLoader \"" + targetClassLoaderName +
                    // "\"");
                } else if (versionOrJarPart.startsWith("jar")) {
                    classLoader.addRepository(repository.getJarByName(id,
                            versionOrJar));
                    //					System.out.println("Added jar with id \"" + id + "\" and
                    // filename \"" + versionOrJar +
                    //						"\" to KarmaClassLoader \"" + targetClassLoaderName +
                    // "\"");
                }

            }
        }
    }
}