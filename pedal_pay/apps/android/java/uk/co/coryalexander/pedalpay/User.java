package uk.co.coryalexander.pedalpay;

import java.util.HashMap;
import java.util.Map;

public class User {

    private Map<String, String> details = new HashMap<String, String>();
    private static User instance;

    public User() {}

    public Map<String, String> getDetails() {
        return details;
    }

    public static synchronized User getInstance() {
        return instance == null ? instance = new User() : instance;
    }
}
