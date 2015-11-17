package ru.jmessenger.application.common;

/**
 * Created by dima on 17.11.15.
 */
public enum Request {
    AUTH(null) {

    };

    private final Package aPackage;

    Request(Package pack) {
        aPackage = pack;
    }

}
