package com.example.xevaq.parcie;

import android.content.SharedPreferences;

public class GlobalConfig {
    private static GlobalConfig mInstance = null;

    private String endpoint = "http://ec2-54-200-171-216.us-west-2.compute.amazonaws.com:8080";
    private String login = "";
    private String password = "";
    private boolean loggedIn = false;

    private GlobalConfig(){

    }

    public static GlobalConfig getInstance(){
        if(mInstance == null)
        {
            mInstance = new GlobalConfig();
        }
        return mInstance;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}