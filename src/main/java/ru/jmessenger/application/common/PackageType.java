package ru.jmessenger.application.common;

/**
 * Created by dima on 17.11.15.
 */
public enum PackageType {
    //server client req
    REQ_SEARCH,
    REQ_AUTH,
    REQ_SEND_MESSAGE,
    REQ_SEND_FILE,
    //server response
    RESP_AUTH_OK,
    RESP_AUTH_FAILED,
    RESP_MESSAGE_DELIVERED, // client response too
    RESP_MESSAGE_IN_QUEUE,
    RESP_MESSAGE_USER_NOT_FOUND,
    RESP_SEARCH_ANSWER,
}

