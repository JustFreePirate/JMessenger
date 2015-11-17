package ru.jmessenger.application.common;

import java.io.*;
import java.util.Date;

/**
 * Created by dima on 17.11.15.
 */
public class Package implements Serializable {
    public static final int BUFF_LEN = 100;

    private Login login;
    private final Date date;
    private final String message;
    private final byte[] file;
    private final Pass pass;
    private PackageType type;


    //auth request
    public Package(Login login, Pass pass) {
        this(PackageType.REQ_AUTH, login, pass, null, null, new Date());
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
        this(PackageType.REQ_SEND_FILE, recipientLogin, null, null, file, new Date());
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

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }
}
