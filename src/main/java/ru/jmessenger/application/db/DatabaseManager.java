package ru.jmessenger.application.db;

import org.sql2o.Sql2o;
import org.sql2o.Connection;

import java.util.Date;
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
    static final String MESSAGE_TABLE = "messages";

    private static Sql2o sql2o;
    static {
        sql2o = new Sql2o(DB_URL, USER, PASS);
    }

    DatabaseManager(){
        try {
            Class.forName(JDBC_DRIVER).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Error in Class.forName(JDBC_DRIVER).newInstance();");
        }

        if(!isTableExists(USER_TABLE)){
            createUsersTable();
        }
        if(!isTableExists(MESSAGE_TABLE)){
            createMessagesTable();
        }

    }

    private boolean isTableExists(String tableName){
        final String request =  "SELECT 1 FROM `" + tableName + "` LIMIT 1;";
        try(Connection con = sql2o.beginTransaction()) {
            Object t = con.createQuery(request).executeScalar();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    private void createUsersTable () {
        final String createTable =
                "CREATE TABLE\n" +
                        "    `" + USER_TABLE + "` (\n" +
                        "        `user_id`      INT AUTO_INCREMENT,\n" +
                        "        `login`        VARCHAR(100) NOT NULL,\n" +
                        "        `hash_pass`    VARCHAR(100) NOT NULL,\n" +
                        "         PRIMARY KEY(`user_id`)\n" +
                        "    );";

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(createTable).executeUpdate();
            con.commit();
        } catch (Throwable t) {}
    }

    private void createMessagesTable () {
        final String createTable =
                "CREATE TABLE\n" +
                        "    `" + MESSAGE_TABLE + "` (\n" +
                        "        `message_id`   INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "        `sender_id`    INT,\n" +
                        "        `recipient_id` INT,\n" +
                        "        `date`         LONGTEXT NOT NULL,\n" +
                        "        `message`      LONGTEXT,\n" +
                        "        FOREIGN KEY (sender_id) REFERENCES users(user_id)\n" +
                        "    );";

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(createTable).executeUpdate();
            con.commit();
        } catch (Throwable t) {}
    }

    public void addMessage (TemproraryClassMessage message){
        final String insertMessage =
                "INSERT INTO\n" +
                "    `messages` (`sender_id`, `recipient_id`, `date`, `message`)\n" +
                "VALUES\n" +
                "    (:sender_id, :recipient_id, :date_param, :message_param);";

        final String getLogin =
                "SELECT user_id " +
                "FROM users " +
                "WHERE login = :userLogin";

        Integer senderId = (Integer) sql2o.createQuery(getLogin)
                .addParameter("userLogin", message.getSenderLogin())
                .executeScalar();

        Integer recipientId = (Integer) sql2o.createQuery(getLogin)
                .addParameter("userLogin", message.getRecipientLogin())
                .executeScalar();

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertMessage)
                    .addParameter("sender_id", senderId)
                    .addParameter("recipient_id", recipientId)
                    .addParameter("date_param", message.getDate())
                    .addParameter("message_param", message.getMessage())
                    .executeUpdate();
            con.commit();
        }
    }

    public void addUser (TemproraryClassUser user){
        final String insertPerson =
                "INSERT INTO\n" +
                "    `users` (`login`, `hash_pass`)\n" +
                "VALUES\n" +
                "    (:loginParam, :hashPassParam);";

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertPerson)
                    .addParameter("loginParam", user.getLogin())
                    .addParameter("hashPassParam", user.getHashPass())
                    .executeUpdate();
            con.commit();
        }
    }

    public static List<TemproraryClassMessage> getMessageListForUser(TemproraryClassUser user) {

        final String getRecipientId =
                "SELECT user_id " +
                "FROM users " +
                "WHERE login = :userLogin";

        Integer recipientId = (Integer) sql2o.createQuery(getRecipientId)
                .addParameter("userLogin", user.getLogin())
                .executeScalar();

        //Этот запрос я писал больше двух часов
        String sql =
                "SELECT login AS senderLogin, :login AS recipientLogin, date, message " +
                        "FROM " +
                        "        users " +
                        "        Inner JOIN messages " +
                        "            ON users.user_id = messages.sender_id " +
                        "WHERE recipient_id = (:id);";

        try(Connection con = sql2o.open()) {
           return con.createQuery(sql)
                   .addParameter("login", user.getLogin())
                   .addParameter("id", recipientId)
                   .executeAndFetch(TemproraryClassMessage.class);
        }
    }

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();

        //Просто создаем участников. Это ещё никак не относится к базе данных
        TemproraryClassUser Bob =  new TemproraryClassUser("Bob", "Nothing");
        TemproraryClassUser Alice =  new TemproraryClassUser("Alice", "Nothing");
        TemproraryClassUser Mallory =  new TemproraryClassUser("Mallory", "Nothing");

        //Добавляем участников в базу. (например, они прошли регистрацию).
        databaseManager.addUser(Bob);
        databaseManager.addUser(Alice);
        databaseManager.addUser(Mallory);

        // Bob -- "Ti kek" --> Alice
        // Alice -- "Nope" --> Bob
        // Mallory -- "Misha kek" --> Bob
        databaseManager.addMessage(new TemproraryClassMessage("Bob", "Alice", (new Date()).toString(), "Ti kek"));
        databaseManager.addMessage(new TemproraryClassMessage("Alice", "Bob", (new Date()).toString(), "Nope"));
        databaseManager.addMessage(new TemproraryClassMessage("Mallory", "Bob", (new Date()).toString(), "Misha kek"));

        //Хотим узнать, какие сообщения пришли Бобу
        getMessageListForUser(Bob).forEach(System.out::println);
    }
}
