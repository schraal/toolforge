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
 * @version
 */
public final class ManifestException extends KarmaException {

    /**
	 * No manifest store directory could be found. This directory contains all manifest XML files. This directory
	 * is resolved through the {@link nl.toolforge.karma.core.UserEnvironment#MANIFEST_STORE_DIRECTORY_PROPERTY} property.
	 */
	public static final ErrorCode NO_MANIFEST_STORE_DIRECTORY = new ErrorCode("MNFS-00001");

	/**
	 * No manifest file could be found in the manifest store directory.
	 */
	public static final ErrorCode MANIFEST_FILE_NOT_FOUND = new ErrorCode("MNFS-00002");

	/**
	 * Duplicate module name in manifest file.
	 */
	public static final ErrorCode DUPLICATE_MODULE_IN_MANIFEST = new ErrorCode("MNFS-00003");

 	public ManifestException() {}

	public ManifestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ManifestException(ErrorCode errorCode, Throwable t) {
		super(errorCode, t);
	}

}
