package com.example.julia.uley.common;

import com.example.julia.uley.server.ConnectionManager;

import java.io.*;
import java.util.Date;

/**
 * Created by dima on 17.11.15.
 */
public class Package implements Serializable {
    public static final int BUFF_LEN = ConnectionManager.BUFF_LEN;

    private Date date;
    private Pass pass;
    private Login login;
    private String message;
    private byte[] file;
    private Login[] searchAnswer;
    private PackageType type;

    //Response on search request
    public Package(Login[] searchAnswer) {
        type = PackageType.RESP_SEARCH_ANSWER;
        this.searchAnswer = searchAnswer;
    }

    //Short response
    public Package(PackageType type) {
        this.type = type;
    }

    //sign in/sign out/sign up request
    public Package(PackageType type, Login login, Pass pass) {
        this(type, login, pass, null, null, new Date());
    }

    //search request
    public Package(Login login) {
        this(PackageType.REQ_SEARCH, login, null, null, null, new Date());
    }

    //send message to recipient
    public Package(String message, Login recipientLogin) {
        this(PackageType.REQ_SEND_MESSAGE, recipientLogin, null, message, null, new Date());
    }

    //send file to recipient
    public Package(byte[] file, Login recipientLogin) {
        this(PackageType.REQ_SEND_MESSAGE, recipientLogin, null, null, file, new Date());
    }

    //create message-package from DB
    public Package(String message, Login recipientLogin, Date date) {
        this(PackageType.REQ_SEND_MESSAGE, recipientLogin, null, message, null, date);
    }

    private Package(PackageType type, Login login, Pass pass, String message, byte[] file, Date date) {
        this.type = type;
        this.login = login;
        this.date = date;
        this.message = message;
        this.file = file;
        this.pass = pass;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public PackageType getType() {
        return type;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Pass getPass() {
        return pass;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getFile() {
        return file;
    }

    public Date getDate() {
        return date;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFF_LEN);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
        }
        return baos.toByteArray();
    }

    public static Package deserialize(byte[] data) throws Exception {
        Package pack;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                pack = (Package) ois.readObject();
        }
        return pack;
    }
}
