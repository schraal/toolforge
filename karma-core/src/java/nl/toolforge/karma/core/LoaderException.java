package nl.toolforge.karma.core;

/**
 * Thrown when an exception occurred while loading the manifest file in memory. This
 * exception might occur when the user has not yet updated the manifest store on disk.
 *
 * @author D.A. Smedes
 */
public class LoaderException extends KarmaException {

    public LoaderException(String message) {
        super(message);
    }
}

