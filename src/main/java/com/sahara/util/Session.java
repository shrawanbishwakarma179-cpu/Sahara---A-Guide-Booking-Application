package com.sahara.util;

import com.sahara.model.User;

/**
 * Session — holds the currently logged-in user across screens.
 * Singleton pattern.
 */
public class Session {
    private static Session instance;
    private User currentUser;
    private String currentRole;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public User getCurrentUser()            { return currentUser; }
    public void setCurrentUser(User u)      { this.currentUser = u; }

    public String getCurrentRole()          { return currentRole; }
    public void setCurrentRole(String r)    { this.currentRole = r; }

    public void clear() {
        currentUser = null;
        currentRole = null;
    }
}
