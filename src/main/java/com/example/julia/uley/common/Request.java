package com.example.julia.uley.common;

import java.lang.Package;
import java.lang.String;

/**
 * Created by dima on 17.11.15.
 */
public class Request {
    public static final String SEARCH = "SEARCH";
    public static final String AUTH = "AUTH";
    public static final String SEND = "SEND";

    private Package aPackage;
    private User dataUser;
    private String searchLogin;
    private String TypeRequest;

    public Package getaPackage() {
        return aPackage;
    }

    public User getDataUser() {
        return dataUser;
    }

    public String getSearchLogin() {
        return searchLogin;
    }

    public String getTypeRequest() {
        return TypeRequest;
    }

    Request(Package aPackage, String TypeRequest, User dataUser, String searchLogin){
        this.aPackage = aPackage;
        this.TypeRequest = TypeRequest;
        this.dataUser = dataUser;
        this.searchLogin = searchLogin;
    }


}
