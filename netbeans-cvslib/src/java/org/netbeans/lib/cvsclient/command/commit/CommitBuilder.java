/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.lib.cvsclient.command.commit;

import java.io.*;

import org.netbeans.lib.cvsclient.command.*;
import org.netbeans.lib.cvsclient.event.*;

/**
 * Handles the building of update information object and the firing of
 * events when complete objects are built.
 *
 * @author  Milos Kleint
 */
public class CommitBuilder
        implements Builder {
            
    /**
     * Parsing constants.
     */           
    public static final String UNKNOWN = "commit: nothing known about `"; //NOI18N
    public static final String EXAM_DIR = ": Examining"; //NOI18N
    public static final String CHECKING_IN = "Checking in "; //NOI18N
    public static final String REMOVING = "Removing "; //NOI18N
    public static final String NEW_REVISION = "new revision:"; //NOI18N
    public static final String INITIAL_REVISION = "initial revision:"; //NOI18N
    public static final String DONE = "done"; //NOI18N
    public static final String RCS_FILE = "RCS file: "; //NOI18N
    public static final String ADD = "commit: use `cvs add' to create an entry for "; //NOI18N

    /**
     * The status object that is currently being built.
     */
    private CommitInformation commitInformation;

    /**
     * The directory in which the file being processed lives. This is
     * relative to the local directory
     */
    private String fileDirectory;

    /**
     * The event manager to use.
     */
    private final EventManager eventManager;

    private final String localPath;

    private boolean isAdding;

    public CommitBuilder(EventManager eventManager, String localPath) {
        this.eventManager = eventManager;
        this.localPath = localPath;
    }

    public void outputDone() {
        if (commitInformation != null) {
            eventManager.fireCVSEvent(new FileInfoEvent(this, commitInformation));
            commitInformation = null;
        }
    }

    public void parseLine(String line, boolean isErrorMessage) {
        if (line.indexOf(UNKNOWN) >= 0) {
            processUnknownFile(line.substring(line.indexOf(UNKNOWN) + UNKNOWN.length()).trim());
        }
        else if (line.indexOf(ADD) > 0) {
            processToAddFile(line.substring(line.indexOf(ADD) + ADD.length()).trim());
        }
        else if (line.startsWith(CHECKING_IN)) {
            // - 1 means to cut the ';' character
            processFile(line.substring(CHECKING_IN.length(), line.length() - 1));
            if (isAdding) {
                commitInformation.setType(commitInformation.ADDED);
                isAdding = false;
            }
            else {
                commitInformation.setType(commitInformation.CHANGED);
            }
        }
        else if (line.startsWith(REMOVING)) {
            processFile(line.substring(REMOVING.length(), line.length() - 1));
            // - 1 means to cut the ';' character
            commitInformation.setType(commitInformation.REMOVED);
        }
        else if (line.indexOf(EXAM_DIR) >= 0) {
            fileDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
        }
        else if (line.startsWith(RCS_FILE)) {
            isAdding = true;
        }
        else if (line.startsWith(DONE)) {
            outputDone();
        }
        else if (line.startsWith(INITIAL_REVISION)) {
            processRevision(line.substring(INITIAL_REVISION.length()));
        }
        else if (line.startsWith(NEW_REVISION)) {
            processRevision(line.substring(NEW_REVISION.length()));
        }
    }

    private File createFile(String fileName) {
        return new File(localPath, fileName);
    }

    private void processUnknownFile(String line) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInformation.UNKNOWN);
        int index = line.indexOf('\'');
        String fileName = line.substring(0, index - 1).trim();
        commitInformation.setFile(createFile(fileName));
        outputDone();
    }

    private void processToAddFile(String line) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInformation.TO_ADD);
        String fileName = line.trim();
        if (fileName.endsWith(";")) { //NOI18N
            fileName = fileName.substring(0, fileName.length() - 2);
        }
        commitInformation.setFile(createFile(fileName));
        outputDone();
    }

    private void processFile(String filename) {
        if (commitInformation == null) {
            commitInformation = new CommitInformation();
        }

        if (filename.startsWith("no file")) { //NOI18N
            filename = filename.substring(8);
        }
        commitInformation.setFile(createFile(filename));
    }

    private void processRevision(String revision) {
        int index = revision.indexOf(';');
        if (index >= 0) {
            revision = revision.substring(0, index);
        }
        commitInformation.setRevision(revision.trim());
    }

    public void parseEnhancedMessage(String key, Object value) {
    }
}
