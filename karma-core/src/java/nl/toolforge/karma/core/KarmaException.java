package nl.toolforge.karma.core;

import java.text.MessageFormat;

import nl.toolforge.karma.core.exception.ErrorCode;
import nl.toolforge.karma.core.prefs.Preferences;

/**
 * Root exception for <code>nl.toolforge.karma</code> classes. A <code>KarmaException</code> can be initialized with a
 * structured error code {@link nl.toolforge.karma.core.exception.ErrorCode} or the good old way (a <code>String</code>). The first method supports
 * localized error messages, which is usefull when building a localized Karma client.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class KarmaException extends Exception {

  //private static Log logger = LogFactory.getLog(KarmaException.class);
  private static Preferences prefs = Preferences.getInstance();

  // TODO : Work trough all errorcodes and come up with a nice setup of codes.
  //


  /** When some function is not yet implemented and likely to be forgotten, this one can be thrown to be reminded */
  public static final ErrorCode LAZY_BASTARD = new ErrorCode("RT-00000");

  /** Vital configuration is missing */
  public static final ErrorCode MISSING_CONFIGURATION = new ErrorCode("RT-00001");

  /** The development home directory cannot be created */
  public static final ErrorCode DEVELOPMENT_HOME_CANNOT_BE_CREATED = new ErrorCode("RT-00010");

  /** The configuration home directory cannot be created */
  public static final ErrorCode CONFIG_HOME_CANNOT_BE_CREATED = new ErrorCode("RT-00011");

  /** Manifest cannot only contain certain types of <code>Module</code>s. */
  public static final ErrorCode INVALID_MANIFEST_NAME = new ErrorCode("RT-00004");





  /** Can be used to identify something that is not implemented */
  public static final ErrorCode NOT_IMPLEMENTED = new ErrorCode("CORE-00000");


  /** The implementation class for the command as defined in the descriptor cannot be found */
  public static final ErrorCode COMMAND_IMPLEMENTATION_CLASS_NOT_FOUND = new ErrorCode("CORE-00002");

  /** The XML describing the command is invalid */
  public static final ErrorCode COMMAND_DESCRIPTOR_XML_ERROR = new ErrorCode("CORE-00003");

  /**
   * The {@link nl.toolforge.karma.core.cmd.CommandContext#init} method has not been called. This is
   * serious, as commands are run by this command context.
   */
  public static final ErrorCode COMMAND_CONTEXT_NOT_INITIALIZED = new ErrorCode("CORE-00004");

  /** No development home directory could be referenced to. This is panic, because without it, nothing will work. */
  public static final ErrorCode NO_DEVELOPMENT_HOME = new ErrorCode("CORE-00005");

  /** No configuration directory could be referenced to. This is panic, because without it, nothing will work. */
  public static final ErrorCode NO_CONFIGURATION_DIRECTORY = new ErrorCode("CORE-00006");

  /** Duplicate command descriptor name in commands XML file */
  public static final ErrorCode DUPLICATE_COMMAND = new ErrorCode("CORE-00007");

  /** Duplicate command descriptor alias in commands XML file */
  public static final ErrorCode DUPLICATE_COMMAND_ALIAS = new ErrorCode("CORE-00008");

  /** No location files were found. A developer should have a directory configured in karma.properties. */
  public static final ErrorCode NO_LOCATION_DATA_FOUND = new ErrorCode("CORE-00009");

  /** Data format errors (errors during pattern matching, etc). */
  public static final ErrorCode DATAFORMAT_ERROR = new ErrorCode("CORE-00020");

  /** A manifest file could not be found or loading errors occurred */
  public static final ErrorCode MANIFEST_COULD_NOT_BE_LOADED = new ErrorCode("CORE-00030");

  /** No manifest has been assigned to the <code>CommandContext</code>  */
  public static final ErrorCode NO_MANIFEST_SELECTED = new ErrorCode("CORE-00031");

  /** IO Error when trying to flush the manifest file to disk */
  public static final ErrorCode MANIFEST_FLUSH_ERROR = new ErrorCode("CORE-00032");

  /** The console could not be initialized with user defined settings. Settings could not be available. */
  public static final ErrorCode CONSOLE_INITIALIZATION_ERROR = new ErrorCode("CORE-00040");

  /** Command does not exist */
  public static final ErrorCode INVALID_COMMAND = new ErrorCode("CORE-00050");

  /** Invalid option for this command */
  public static final ErrorCode INVALID_COMMAND_OPTION = new ErrorCode("CORE-00051");

  /**
   * Location descriptor does not exist. This happens when a module's 'location'-attribute cannot be found by the
   * <code>LocationFactory</code>, which contains references to all <code>Location</code> objects mapped in
   * <code>locations.xml</code>.
   */
  public static final ErrorCode LOCATION_NOT_FOUND = new ErrorCode("CORE-00060");

  /** No <code>module.info</code> file exists for the module. This file is mandatory for <code>SourceModule</code>s. */
  public static final ErrorCode NO_MODULE_INFO = new ErrorCode("CORE-00065");

  protected ErrorCode errorCode = null;

  protected Object[] messageArguments = new Object[]{};

  //TODO: uitfaseren?
  public KarmaException() {}

  /**
   * Create a new KarmaException, with the specific errorCode.
   *
   * @param errorCode  The errorCode that identifies the specific error that has occurred.
   */
  public KarmaException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and messageArguments.
   *
   * @param errorCode         The errorCode that identifies the specific error that has occurred.
   * @param messageArguments  These arguments are filled in into the error codes' message.
   */
  public KarmaException(ErrorCode errorCode, Object[] messageArguments) {
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and Throwable that caused the exception.
   *
   * @param errorCode  The errorCode that identifies the specific error that has occurred.
   * @param t          The Throwable that caused this specific exception.
   */
  public KarmaException(ErrorCode errorCode, Throwable t) {
    super(t);
    this.errorCode = errorCode;
  }

  /**
   * Create a new KarmaException, with the specific errorCode and Throwable that caused the exception.
   *
   * @param errorCode         The errorCode that identifies the specific error that has occurred.
   * @param messageArguments  These arguments are filled in into the error codes' message.
   * @param t                 The Throwable that caused this specific exception.
   */
  public KarmaException(ErrorCode errorCode, Object[] messageArguments, Throwable t) {
    super(t);
    this.errorCode = errorCode;
    this.messageArguments = messageArguments;
  }

  /**
   * Gets this instance' {@link nl.toolforge.karma.core.exception.ErrorCode}.
   *
   * @return This instance' {@link nl.toolforge.karma.core.exception.ErrorCode} or <code>null</code> if this exception was not initialized with an
   *         <code>ErrorCode</code>.
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

  /**
   * A <code>KarmaException</code> can be constructed with a structured error code {@link nl.toolforge.karma.core.exception.ErrorCode}. When this is
   * done, the error message will return {@link nl.toolforge.karma.core.exception.ErrorCode#getErrorMessage} for this exception. If no
   * <code>ErrorCode</code> was used for initialization, the exceptions' {@link #getMessage} is returned, so there is
   * always something to tell the developer or user.
   *
   * @return Return's the <code>ErrorCode</code>s' error message, if the <code>ErrorCode</code> was set, otherwise it
   *         will return <code>Throwable.getMessage()</code>.
   */
  public String getErrorMessage() {

    if (errorCode == null) {
      //TODO: deze call zorgt IMHO een eindeloze loop.
      return getMessage();
    } else {
      String errorMessage = errorCode.getErrorMessage(prefs.getLocale());
      if (getMessageArguments().length != 0) {
        MessageFormat messageFormat = new MessageFormat(errorMessage);
        return messageFormat.format(getMessageArguments());
      } else {
        return errorMessage;
      }
    }
  }

  /**
   *
   * @return  The arguments that are to be filled in into the error codes' message.
   */
  private Object[] getMessageArguments() {
    return messageArguments;
  }

  public String getMessage() {
    return getErrorMessage();
  }

  public void printStackTrace() {

    //logger.error("ERRORCODE : " + errorCode.getErrorCode());

    super.printStackTrace();
  }
}
