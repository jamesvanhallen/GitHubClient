package com.james.testgithub.main.Another;

/**
 * Created by james on 30.01.15.
 */
public class TokenRequest {

    public static final String SCOPE_REPO = "repo";
    public String[] scopes;
    public TokenRequest(String[] setScopes) {
        scopes = setScopes;
    }
}
