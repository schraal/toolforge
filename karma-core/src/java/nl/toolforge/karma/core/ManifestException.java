package nl.toolforge.karma.core;

import nl.toolforge.karma.core.exception.ErrorCode;

/**
 * <p>Exceptions relating to a manifest. Exceptions of this type will be thrown during manifest
 * loading and other major errors relating to the manifest.
 *
 * <p>As with all other <code>KarmaExceptions</code>, exceptions can only be thrown with a certain
 * <code>ErrorCode</code>
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ManifestException extends KarmaException {

	/**
	 * No manifest store directory could be found. This directory contains all manifest XML files. This directory
	 * is resolved through the {@link nl.toolforge.karma.core.prefs.Preferences#MANIFEST_STORE_DIRECTORY_PROPERTY} property.
	 */
	public static final ErrorCode NO_MANIFEST_STORE_DIRECTORY = new ErrorCode("MFS-00001");

	/**
	 * No manifest file could be found in the manifest store directory.
	 */
	public static final ErrorCode MANIFEST_FILE_NOT_FOUND = new ErrorCode("MFS-00002");

	/** Duplicate module name in manifest file. */
	public static final ErrorCode DUPLICATE_MODULE_IN_MANIFEST = new ErrorCode("MFS-00003");

	/** Duplicate module name in manifest file. */
	public static final ErrorCode INVALID_LOCAL_PATH = new ErrorCode("MFS-00004");

	/** No history item could be found for a manifest. */
	public static final ErrorCode NO_HISTORY_AVAILABLE = new ErrorCode("MFS-00010");

	/** Container for 'general' errors during a manifest load */
	public static final ErrorCode MANIFEST_LOAD_ERROR = new ErrorCode("MFS-00004");

	/** Container for 'general' errors during a manifest load */
	public static final ErrorCode MANIFEST_LOAD_RECURSION = new ErrorCode("MFS-00005");

	/** A module instance was requested from the manifest by its name, but the module did not exist. */
	public static final ErrorCode NO_SUCH_MODULE = new ErrorCode("MFS-00010");

	/** IO Error when trying to flush the manifest file to disk */
  public static final ErrorCode MANIFEST_FLUSH_ERROR = new ErrorCode("MFS-00020");

	public ManifestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ManifestException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

	public ManifestException(ErrorCode errorCode, Object[] messageArguments) {
		super(errorCode, messageArguments);
	}

	public ManifestException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
		super(errorCode, messageArguments, t);
	}

}
