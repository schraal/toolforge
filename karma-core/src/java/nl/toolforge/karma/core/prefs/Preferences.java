package nl.toolforge.karma.core.prefs;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.UserEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class is used to store configuration settings. If the
 * karma is used from the command line, then it will store
 * it's settings in a file, otherwise the preferences will
 * not be persisted, to enable multiple longer running instances
 * for example when the karma is used from withing a GUI
 */
public final class Preferences
{
    //private static Log logger = LogFactory.getLog(Preferences.class);

    private final static boolean COMMAND_LINE_MODE = System.getProperty("MODE", "UNKNOWN").equals("COMMAND_LINE_MODE");

    private static Preferences instance = null;

    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.load();
        }
        return instance;
    }

    private Properties values = new Properties();

    // Private constructor to prevent direct instantiation
    //
    private Preferences() {
        // We first try to load the preferences that might be there on disk
        //
        load();


        // Next we try to load the karma.properties file, which could override
        // the preferences that are loaded before. The preferences might be outdated you know.
        //
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(UserEnvironment.CONFIGURATION_DIRECTORY_PROPERTY, "karma.properties")));

            for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
                String prop = (String) e.nextElement();
                put(prop, props.getProperty(prop));
            }
        } catch (Exception e) {
            //logger.error("Could not load " + UserEnvironment.CONFIGURATION_DIRECTORY_PROPERTY + "/karma.properties, exiting...");
            throw new KarmaRuntimeException("Could not load " + UserEnvironment.CONFIGURATION_DIRECTORY_PROPERTY + "/karma.properties, exiting...", e);
        }

        // Lets store the prefs to a file (if possible), which now should be up to date.
        //
        flush();
    }

	/**
	 * Retrieves a property value by <code>key</code>. If the value for <code>key</code> is not found, the
	 * <code>defaultValue</code> will be returned.
	 *
	 * @param key
	 * @param defaultValue
	 *
	 * @return The value for <code>key</code>, or <code>defaultValue</code> if no value was found for key
	 *         <code>key</code>.
	 */
    public String get(String key, String defaultValue) {
        String returnValue = defaultValue;

        if ((returnValue = values.getProperty(key)) == null) {
            returnValue = defaultValue;
        }

        return returnValue;
    }

    public String get(String key) {
        String returnValue = values.getProperty(key);

        if (returnValue == null) {
            throw new UnavailableValueException("No value found in preferences for key \"" + key + "\"");
        } else {
            return returnValue;
        }
    }


    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, Integer.toString(defaultValue)));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.getBoolean(get(key, (new Boolean(defaultValue)).toString()));
    }

    public void put(String key, String value) {
        values.setProperty(key, value);
    }

    public void putInt(String key, int value) {
        put(key, Integer.toString(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, (new Boolean(value)).toString());
    }

    public void flush() {
        if (COMMAND_LINE_MODE) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(UserEnvironment.CONFIGURATION_DIRECTORY_PROPERTY, "preferences"));
                values.store(out, "Karma Preferences");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }

    private void load() {
        if (COMMAND_LINE_MODE) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(new File(UserEnvironment.CONFIGURATION_DIRECTORY_PROPERTY, "preferences"));
                values.load(in);
            }
            catch (IOException e) {
                // ignore, we assume it did not exist yet
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }
}


