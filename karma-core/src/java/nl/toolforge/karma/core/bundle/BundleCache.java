package nl.toolforge.karma.core.bundle;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.prefs.Preferences;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * <p>Helper class initializing <code>ResourceBundle</code> and caching instances to enable localized messages.
 * Interface applications can extend this class to add and retrieve cached bundle.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BundleCache {

	private static Log logger = LogFactory.getLog(BundleCache.class);

	private static Map bundles = null;

	/**
	 * <p>All messages that can be generated by <code>KarmaException</code> instances.
	 * <p/>
	 * <p>This <code>final</code> thingie expects <code>error-messages_&lt;locale&gt;.properties</code> to be present
	 * in the classpath. The locale is read from the <code>locale</code> property in <code>karma.properties</code>.
	 * <p/>
	 * <p>This bundle can also be obtained using <code>getBundle("ERROR_MESSAGES")</code>.
	 */
	private static final ResourceBundle ERROR_MESSAGES =
      ResourceBundle.getBundle("error-messages", Locale.ENGLISH);
//  ResourceBundle.getBundle("error-messages", LocalEnvironment.getLocale());
  //todo read from system

	public static final String ERROR_MESSAGES_KEY = "ERROR_MESSAGES";

	/**
	 * <p>All messages that are sent to a frontend interface application (messages not coming from an exception).
	 * <p/>
	 * <p>This <code>final</code> thingie expects <code>frontend-messages_&lt;locale&gt;.properties</code> to be present
	 * in the classpath. The locale is read from the <code>locale</code> property in <code>karma.properties</code>.
	 * <p/>
	 * <p>This bundle can also be obtained using <code>getBundle("FRONTEND_MESSAGES")</code>
	 */
	private static final ResourceBundle FRONTEND_MESSAGES =
			ResourceBundle.getBundle("frontend-messages", Locale.ENGLISH);
//			ResourceBundle.getBundle("frontend-messages", LocalEnvironment.getLocale());
  //todo read from system

	public static final String FRONTEND_MESSAGES_KEY = "FRONTEND_MESSAGES";


	private static BundleCache instance = null;

	/**
	 * Initializes the cache or returns the cache.
	 *
	 * @return The cache instance.
	 */
	public synchronized static BundleCache getInstance() {
		return (instance == null ? new BundleCache() : instance);
	}


	private BundleCache() {
		bundles = new Hashtable();

		register("ERROR_MESSAGES", ERROR_MESSAGES);
		register("FRONTEND_MESSAGES", FRONTEND_MESSAGES);
	}


	/**
	 * Registers a resource bundle in the cache.
	 *
	 * @param bundleKey The unique key to the bundle. <b>All keys are transformed into uppercase.</b>
	 * @param bundle    The resource bundle to register in this cache.
	 */
	public final void register(String bundleKey, ResourceBundle bundle) {

		if ((bundleKey == null) || (bundleKey.length() == 0)) {
			throw new KarmaRuntimeException("Registration key for the resource bundle cannot be null or empty.");
		}

		if (bundle == null) {
			throw new NullPointerException("Resource bundle should not be null.");
		}

		if (bundles.values().contains(bundleKey)) {
			throw new KarmaRuntimeException("Registration key " + bundleKey + " already exist in cache.");
		}

		bundles.put(bundleKey, bundle);
	}

	/**
	 * Retrieves a bundle from the cache by <code>bundleKey</code>.
	 *
	 * @param bundleKey The unique key to this bundle.
	 * @return The resource bundle as identified by <code>bundleKey</code> in the cache.
	 */
	public final ResourceBundle getBundle(String bundleKey) {

//		System.out.println("Locale : " + Preferences.getInstance().getLocale());
//		System.out.println("Default locale : " + Locale.getDefault());

		if (bundles.keySet().contains(bundleKey)) {
			return (ResourceBundle) bundles.get(bundleKey);
		}
		throw new KarmaRuntimeException("Resource bundle for key " + bundleKey + " does not exist in this cache.");
	}

	/**
	 * Flushes this cache.
	 */
	public synchronized final void flush() {
		bundles.keySet().clear();
	}

}