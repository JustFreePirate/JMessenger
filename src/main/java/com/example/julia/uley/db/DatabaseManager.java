package com.example.julia.uley.db;

import com.example.julia.uley.common.Login;
import com.example.julia.uley.common.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import com.example.julia.uley.common.Package;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Сергей on 15.11.2015.
 */
public class DatabaseManager {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/my_db";
    static final String USER = "admin";
    static final String PASS = "admin";

    static final String USER_TABLE = "users";
    static final String PACKAGE_TABLE = "packages";

    private class Pack {
        public final String login;
        public final String date;
        public final String message;
        public final byte[] file;

        Pack() {
            this.date = null;
            this.file = null;
            this.login = null;
            this.message = null;
        }
    }

    private static Sql2o sql2o;

    static {
        sql2o = new Sql2o(DB_URL, USER, PASS);
    }

    public DatabaseManager() {
        try {
            Class.forName(JDBC_DRIVER).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Error in Class.forName(JDBC_DRIVER).newInstance();");
        }

        if (!isTableExists(USER_TABLE)) {
            createUsersTable();
        }
        if (!isTableExists(PACKAGE_TABLE)) {
            createPackagesTable();
        }

    }

    private boolean isTableExists(String tableName) {
        final String request = "SELECT 1 FROM `" + tableName + "` LIMIT 1;";
        try (Connection con = sql2o.beginTransaction()) {
            Object t = con.createQuery(request).executeScalar();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    private void createUsersTable() {
        final String createTable =
                "CREATE TABLE\n" +
                        "    `" + USER_TABLE + "` (\n" +
                        "        `user_id`      INT AUTO_INCREMENT,\n" +
                        "        `login`        VARCHAR(100) NOT NULL,\n" +
                        "        `hash_pass`    VARCHAR(100) NOT NULL,\n" +
                        "         PRIMARY KEY(`user_id`)\n" +
                        "    );";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(createTable).executeUpdate();
            con.commit();
        } catch (Throwable t) {
        }
    }

    private void createPackagesTable() {
        final String createTable =
                "CREATE TABLE\n" +
                        "    `" + PACKAGE_TABLE + "` (\n" +
                        "        `package_id`   INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "        `sender_id`    INT,\n" +
                        "        `recipient_id` INT,\n" +
                        "        `date`         LONGTEXT NOT NULL,\n" +
                        "        `message`      LONGTEXT,\n" +
                        "        `file`         LONGTEXT,\n" +
                        "        FOREIGN KEY (sender_id) REFERENCES users(user_id)\n" +
                        "    );";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(createTable).executeUpdate();
            con.commit();
        } catch (Throwable t) {
        }
    }

    public void addPackage(Login login, Package aPackage) {
        //Не знаю почему, но похоже, что login это recipient,
        //А aPackage.getLogin() -- sender

        final String insertPackage =
                "INSERT INTO\n" +
                        "    `" + PACKAGE_TABLE + "` (`sender_id`, `recipient_id`, `date`, `message`, `file`)\n" +
                        "VALUES\n" +
                        "    (:sender_id, :recipient_id, :date_param, :message_param, :file_param);";

        final String getLogin =
                "SELECT user_id " +
                        "FROM users " +
                        "WHERE login = :userLogin";

        Integer recipientId = (Integer) sql2o.createQuery(getLogin)
                .addParameter("userLogin", login.toString())
                .executeScalar();

        Integer senderId = (Integer) sql2o.createQuery(getLogin)
                .addParameter("userLogin", aPackage.getLogin().toString())
                .executeScalar();

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertPackage)
                    .addParameter("sender_id", senderId)
                    .addParameter("recipient_id", recipientId)
                    .addParameter("date_param", aPackage.getDate().toString())
                    .addParameter("message_param", aPackage.getMessage())
                    .addParameter("file_param", aPackage.getFile())
                    .executeUpdate();
            con.commit();
        }
    }

    public void addUser(User user) {
        final String insertPerson =
                "INSERT INTO\n" +
                        "    `users` (`login`, `hash_pass`)\n" +
                        "VALUES\n" +
                        "    (:loginParam, :hashPassParam);";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertPerson)
                    .addParameter("loginParam", user.getLogin())
                    .addParameter("hashPassParam", user.getHashPass())
                    .executeUpdate();
            con.commit();
        }
    }

    public void updateUser(User user) {
        final String updateUser =
                "UPDATE users " +
                        "SET    hash_pass = :hashPassParam " +
                        "WHERE  login = :loginParam ;";

        try (Connection con = sql2o.open()) {
            con.createQuery(updateUser)
                    .addParameter("loginParam", user.getLogin())
                    .addParameter("hashPassParam", user.getHashPass())
                    .executeUpdate();
        }
    }

//    public boolean isUserExists (String login) {
//        final String findUser =
//                "SELECT count(*) " +
//                        "FROM users " +
//                        "WHERE login = :loginParam" ;
//
//        Long result = (Long) sql2o
//                .createQuery(findUser)
//                .addParameter("loginParam", login)
//                .executeScalar();
//        return result > 0;
//    }
//    public boolean isUserExists (String login, String hashPass) {
//        User user = new User (login, hashPass);
//        return isUserExists(user);
//    }

    public boolean isUserExists(User user) throws Exception {
        final String findUser =
                "SELECT count(*) " +
                        "FROM users " +
                        "WHERE login = :loginParam AND hash_pass = :hashPassParam;";
        Long result = (Long) sql2o
                .createQuery(findUser)
                .addParameter("loginParam", user.getLogin())
                .addParameter("hashPassParam", user.getHashPass())
                .executeScalar();
        return result > 0;

    }

    public boolean isUserExists(Login login) throws Exception {
        final String findUser =
                "SELECT count(*) " +
                        "FROM users " +
                        "WHERE login = :loginParam;";

        try {
            Long result = (Long) sql2o
                    .createQuery(findUser)
                    .addParameter("loginParam", login.toString())
                    .executeScalar();
            return result > 0;
        } catch (Throwable e) {
            throw new Exception("Something went wrong in \"boolean isUserExists (Login login)\"");
        }
    }

    private List<Package> mapToPackage(List<Pack> list) {
        List<Package> result = new LinkedList<>();

        //String string = "January 2, 2010";
        //DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        //Date date = format.parse(string);

        //TODO Сделать настоящее время
        for (Pack entry : list) {
            result.add(new Package(entry.message, new Login(entry.login), new Date()));
        }
        return result;
    }

    public List<Package> getPackageListForUser(Login login) {

        final String getRecipientId =
                "SELECT user_id " +
                        "FROM users " +
                        "WHERE login = :loginParam";

        Integer recipientId = (Integer) sql2o.createQuery(getRecipientId)
                .addParameter("loginParam", login.toString())
                .executeScalar();


        String sql =
                "SELECT login, date, message " +
                        "FROM " +
                        "        users " +
                        "        Inner JOIN " + PACKAGE_TABLE +
                        "            ON users.user_id = " + PACKAGE_TABLE + ".sender_id " +
                        "WHERE recipient_id = (:idParam);";

        List<Pack> result = null;
        try (Connection con = sql2o.open()) {
            result =
                    con.createQuery(sql)
                            .addParameter("idParam", recipientId)
                            .executeAndFetch(Pack.class);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        String sqlDelete =
                "DELETE FROM `" + PACKAGE_TABLE + "`" +
                        "WHERE recipient_id = :recipientIdParam;";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(sqlDelete)
                    .addParameter("recipientIdParam", recipientId)
                    .executeUpdate();
            con.commit();
        }

        return mapToPackage(result);
    }

// Old version
//    public static void main(String[] args)
//        DatabaseManager databaseManager = new DatabaseManager();
//
//        //Просто создаем участников. Это ещё никак не относится к базе данных
//        User Bob =  new User("Bob", "Nothing");
//        User Alice =  new User("Alice", "Nothing");
//        User Mallory =  new User("Mallory", "Nothing");
//
//        //Добавляем участников в базу. (например, они прошли регистрацию).
//        databaseManager.addUser(Bob);
//        databaseManager.addUser(Alice);
//        databaseManager.addUser(Mallory);
//
//        // Bob -- "Ti kek" --> Alice
//        // Alice -- "Nope" --> Bob
//        // Mallory -- "Misha kek" --> Bob
//        databaseManager.addPackage(new Package("Ti kek", "Bob", "Alice", (new Date()).toString(), null));
//        databaseManager.addPackage(new Package("Nope", "Alice", "Bob", (new Date()).toString(), null));
//        databaseManager.addPackage(new Package("Misha kek", "Mallory", "Bob", (new Date()).toString(), null));
//
//        //Хотим узнать, какие сообщения пришли Бобу
//        getPackageListForUser(Bob).forEach(System.out::println);
//    }
}
