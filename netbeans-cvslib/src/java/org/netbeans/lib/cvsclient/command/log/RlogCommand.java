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
package org.netbeans.lib.cvsclient.command.log;

import java.io.*;
import java.util.*;

import org.netbeans.lib.cvsclient.*;
import org.netbeans.lib.cvsclient.command.*;
import org.netbeans.lib.cvsclient.connection.*;
import org.netbeans.lib.cvsclient.event.*;
import org.netbeans.lib.cvsclient.request.*;

/**
 * The rlog command is similar to log, but doens't operate on currently checked 
 * out sources.
 *
 * @author  MIlos Kleint
 */
public class RlogCommand extends BasicCommand {


    /**
     * The modules to checkout. These names are unexpanded and will be passed
     * to a module-expansion request.
     */
    private final List modules = new LinkedList();

    /**
     * The expanded modules.
     */
    private final List expandedModules = new LinkedList();
    
    /**
     * Holds value of property defaultBranch.
     */
    private boolean defaultBranch;

    /**
     * Holds value of property dateFilter.
     */
    private String dateFilter;

    /**
     * Holds value of property headerOnly.
     */
    private boolean headerOnly;

    /**
     * Holds value of property noTags.
     */
    private boolean noTags;

    /**
     * Holds value of property revisionFilter.
     */
    private String revisionFilter;

    /**
     * Holds value of property stateFilter.
     */
    private String stateFilter;

    /**
     * Holds value of property userFilter.
     */
    private String userFilter;

    /**
     * Holds value of property headerAndDescOnly.
     */
    private boolean headerAndDescOnly;

    public RlogCommand() {
        resetCVSCommand();
    }

    /**
     * Set the modules to export.
     * @param theModules the names of the modules to export
     */
    public void setModule(String module) {
        modules.add(module);
    }

    /**
     * clears the list of modules for export.
     */

    public void clearModules() {
        this.modules.clear();
    }

    /**
     * Set the modules to export.
     * @param theModules the names of the modules to export
     */
    public void setModules(String[] modules) {
        clearModules();
        if (modules == null) {
            return;
        }
        for (int i = 0; i < modules.length; i++) {
            String module = modules[i];
            this.modules.add(module);
        }
    }

    public String[] getModules() {
        String[] mods = new String[modules.size()];
        mods = (String[])modules.toArray(mods);
        return mods;
    }

    private void processExistingModules(String localPath) {
        if (expandedModules.size() == 0) {
            return;
        }

        String[] directories = new String[expandedModules.size()];
        directories = (String[])expandedModules.toArray(directories);
        setModules(directories);
    }
    
    /**
     * Getter for property defaultBranch, equals the command-line CVS switch
     * "-b".
     * @return Value of property defaultBranch.
     */
    public boolean isDefaultBranch() {
        return defaultBranch;
    }

    /**
     * Setter for property defaultBranch, equals the command-line CVS switch
     * "-b".
     * @param defaultBranch New value of property defaultBranch.
     */
    public void setDefaultBranch(boolean defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    /**
     * Getter for property dateFilter, equals the command-line CVS switch "-d".
     * @return Value of property dateFilter.
     */
    public String getDateFilter() {
        return dateFilter;
    }

    /** Setter for property dateFilter, equals the command-line CVS switch "-d".
     * @param dateFilter New value of property dateFilter.
     */
    public void setDateFilter(String dateFilter) {
        this.dateFilter = dateFilter;
    }

    /** Getter for property headerOnly, equals the command-line CVS switch "-h".
     * @return Value of property headerOnly.
     */
    public boolean isHeaderOnly() {
        return headerOnly;
    }

    /** Setter for property headerOnly, equals the command-line CVS switch "-h".
     * @param headerOnly New value of property headerOnly.
     */
    public void setHeaderOnly(boolean headerOnly) {
        this.headerOnly = headerOnly;
    }

    /** Getter for property noTags, equals the command-line CVS switch "-N".
     * @return Value of property noTags.
     */
    public boolean isNoTags() {
        return noTags;
    }

    /** Setter for property noTags, equals the command-line CVS switch "-N".
     * @param noTags New value of property noTags.
     */
    public void setNoTags(boolean noTags) {
        this.noTags = noTags;
    }

    /** Getter for property revisionFilter, equals the command-line CVS switch "-r".
     * @return Value of property revisionFilter.
     */
    public String getRevisionFilter() {
        return revisionFilter;
    }

    /** Setter for property revisionFilter, equals the command-line CVS switch "-r".
     * @param revisionFilter New value of property revisionFilter.
     empty string means latest revision of default branch.
     */
    public void setRevisionFilter(String revisionFilter) {
        this.revisionFilter = revisionFilter;
    }

    /** Getter for property stateFilter, equals the command-line CVS switch "-s".
     * @return Value of property stateFilter.
     */
    public String getStateFilter() {
        return stateFilter;
    }

    /** Setter for property stateFilter, equals the command-line CVS switch "-s".
     * @param stateFilter New value of property stateFilter.
     */
    public void setStateFilter(String stateFilter) {
        this.stateFilter = stateFilter;
    }

    /** Getter for property userFilter, equals the command-line CVS switch "-w".
     * @return Value of property userFilter,  empty string means the current user.
     */
    public String getUserFilter() {
        return userFilter;
    }

    /** Setter for property userFilter, equals the command-line CVS switch "-w".
     * @param userFilter New value of property userFilter.
     */
    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }

    /** Getter for property headerAndDescOnly, equals the command-line CVS switch "-t".
     * @return Value of property headerAndDescOnly.
     */
    public boolean isHeaderAndDescOnly() {
        return headerAndDescOnly;
    }

    /** Setter for property headerAndDescOnly, equals the command-line CVS switch "-t".
     * @param headerAndDescOnly New value of property headerAndDescOnly.
     */
    public void setHeaderAndDescOnly(boolean headerAndDescOnly) {
        this.headerAndDescOnly = headerAndDescOnly;
    }
    

    /**
     * Execute this command.
     * @param client the client services object that provides any necessary
     * services to this command, including the ability to actually process
     * all the requests
     */
    public void execute(ClientServices client, EventManager em)
            throws CommandException, AuthenticationException {

        client.ensureConnection();

        requests = new LinkedList();
        if (client.isFirstCommand()) {
            requests.add(new RootRequest(client.getRepository()));
        }
        for (Iterator it = modules.iterator(); it.hasNext();) {
            String module = (String)it.next();
            requests.add(new ArgumentRequest(module));
        }
        expandedModules.clear();
        requests.add(new DirectoryRequest(".", client.getRepository())); //NOI18N
        requests.add(new ExpandModulesRequest());
        try {
            client.processRequests(requests);
        }
        catch (CommandException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new CommandException(ex, ex.getLocalizedMessage());
        }
        requests.clear();
        postExpansionExecute(client, em);
    }

    /**
     * This is called when the server has responded to an expand-modules
     * request.
     */
    public void moduleExpanded(ModuleExpansionEvent e) {
        expandedModules.add(e.getModule());
    }

    /**
     * Execute this command
     * @param client the client services object that provides any necessary
     * services to this command, including the ability to actually process
     * all the requests
     */
    private void postExpansionExecute(ClientServices client, EventManager em)
            throws CommandException, AuthenticationException {

//        processExistingModules(client.getLocalPath());
        super.execute(client, em);

        //
        // moved modules code to the end of the other arguments --GAR
        //
        if (!isRecursive())
        {
            requests.add(1, new ArgumentRequest("-l")); //NOI18N
        }
        // first send out all possible parameters..
        if (defaultBranch) {
            requests.add(1, new ArgumentRequest("-b")); //NOI18N
        }
        if (headerAndDescOnly) {
            requests.add(1, new ArgumentRequest("-t")); //NOI18N
        }
        if (headerOnly) {
            requests.add(1, new ArgumentRequest("-h")); //NOI18N
        }
        if (noTags) {
            requests.add(1, new ArgumentRequest("-N")); //NOI18N
        }
        if (userFilter != null) {
            requests.add(1, new ArgumentRequest("-w" + userFilter)); //NOI18N
        }
        if (revisionFilter != null) {
            requests.add(1, new ArgumentRequest("-r" + revisionFilter)); //NOI18N
        }
        if (stateFilter != null) {
            requests.add(1, new ArgumentRequest("-s" + stateFilter)); //NOI18N
        }
        if (dateFilter != null) {
            requests.add(1, new ArgumentRequest("-d" + dateFilter)); //NOI18N
        }


        for (Iterator it = modules.iterator(); it.hasNext();) {
            String module = (String)it.next();
            requests.add(new ArgumentRequest(module));
        }

        requests.add(new DirectoryRequest(".", client.getRepository())); //NOI18N
        requests.add(CommandRequest.RLOG);
        try {
            client.processRequests(requests);
            requests.clear();

        }
        catch (CommandException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new CommandException(ex, ex.getLocalizedMessage());
        }
    }


    public String getCVSCommand() {
        StringBuffer toReturn = new StringBuffer("rlog "); //NOI18N
        toReturn.append(getCVSArguments());
        if (modules != null && modules.size() > 0) {
            for (Iterator it = modules.iterator(); it.hasNext();) {
                String module = (String)it.next();
                toReturn.append(module);
                toReturn.append(' ');
            }
        }
        else {
            String localizedMsg = CommandException.getLocalMessage("ExportCommand.moduleEmpty.text"); //NOI18N
            toReturn.append(" "); //NOI18N
            toReturn.append(localizedMsg);
        }
        return toReturn.toString();
    }

    public String getCVSArguments() {
        StringBuffer toReturn = new StringBuffer(""); //NOI18N
        if (isDefaultBranch()) {
            toReturn.append("-b "); //NOI18N
        }
        if (isHeaderAndDescOnly()) {
            toReturn.append("-t "); //NOI18N
        }
        if (isHeaderOnly()) {
            toReturn.append("-h "); //NOI18N
        }
        if (isNoTags()) {
            toReturn.append("-N "); //NOI18N
        }
        if (!isRecursive()) {
            toReturn.append("-l "); //NOI18N
        }
        if (userFilter != null) {
            toReturn.append("-w"); //NOI18N
            toReturn.append(userFilter);
            toReturn.append(' ');
        }
        if (revisionFilter != null) {
            toReturn.append("-r"); //NOI18N
            toReturn.append(revisionFilter);
            toReturn.append(' ');
        }
        if (stateFilter != null) {
            toReturn.append("-s"); //NOI18N
            toReturn.append(stateFilter);
            toReturn.append(' ');
        }
        if (dateFilter != null) {
            toReturn.append("-d"); //NOI18N
            toReturn.append(dateFilter);
            toReturn.append(' ');
        }
        return toReturn.toString();
    }

    public boolean setCVSCommand(char opt, String optArg) {
        if (opt == 'R') {
            setRecursive(true);
        }
        else if (opt == 'l') {
            setRecursive(false);
        }
        else if (opt == 'b') {
            setDefaultBranch(true);
        }
        else if (opt == 'h') {
            setHeaderOnly(true);
        }
        else if (opt == 't') {
            setHeaderAndDescOnly(true);
        }
        else if (opt == 'N') {
            setNoTags(true);
        }
        else if (opt == 'd') {
            setDateFilter(optArg);
        }
        else if (opt == 'r') {
            setRevisionFilter(optArg == null ? "" : optArg); //NOI18N
            // for switches with optional args do that.. ^^^^
        }
        else if (opt == 's') {
            setStateFilter(optArg);
        }
        else if (opt == 'w') {
            setUserFilter(optArg == null ? "" : optArg); //NOI18N
        }
        else {
            return false;
        }
        return true;
    }

    public void resetCVSCommand() {
        setRecursive(true);
        setDefaultBranch(false);
        setHeaderOnly(false);
        setHeaderAndDescOnly(false);
        setNoTags(false);
        setDateFilter(null);
        setRevisionFilter(null);
        setStateFilter(null);
        setUserFilter(null);
    }

    /**
     * String returned by this method defines which options are available for this particular command
     */
    public String getOptString() {
        return "RlbhtNd:r:s:w:"; //NOI18N4
    }


    /**
     * Create a builder for this command.
     * @param eventMan the event manager used to receive events.
     */
    public Builder createBuilder(EventManager eventMan) {
        return new LogBuilder(eventMan, this);
    }
}
