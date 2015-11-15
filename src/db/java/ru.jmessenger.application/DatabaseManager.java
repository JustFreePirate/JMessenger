package ru.jmessenger.application;

import org.sql2o.Sql2o;
import org.sql2o.Connection;

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

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertMessage)
                    .addParameter("sender_id", message.getSenderId())
                    .addParameter("recipient_id", message.getRecipientId())
                    .addParameter("date_param", message.getDueDate())
                    .addParameter("message_param", message.getMessage())
                    .executeUpdate();
            con.commit();
        }
    }

    public static List<TemproraryClassMessage> getMessageListForUser(int idUser) {
        String sql =
                "SELECT sender_id, recipient_id, date, message " +
                "FROM messages " +
                "WHERE recipient_id = :id_user";

        try(Connection con = sql2o.open()) {
           return con.createQuery(sql)
                   .addParameter("id_user", idUser)
                   .executeAndFetch(TemproraryClassMessage.class);
        }
    }

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();

        //databaseManager.addMessage(new TemproraryClassMessage(new Long(2), new Long(4), "13/14/2013", "ti kek"));
        //List<TemproraryClassMessage> list = databaseManager.getMessageListForUser(new TemproraryClassUser("Kek", ""));
        List<TemproraryClassMessage> list = databaseManager.getMessageListForUser(3);
    }
}
