package ru.jmessenger.application;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import java.sql.Driver;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.sql.*;

/**
 * Created by Сергей on 14.11.2015.
 */
public class AutoClosableTest {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/my_db";
    static final String USER = "admin";
    static final String PASS = "admin";

    private final Sql2o sql2o = new Sql2o(DB_URL, USER, PASS);

    private final Random random = new Random();

    @Test
    public void testAutoClosable() throws Exception {
        Class.forName(JDBC_DRIVER).newInstance();

        /**
         *  Create all tables
         */
        final String createUsersTable =
                        "CREATE TABLE\n" +
                        "    `users` (\n" +
                        "        `user_id`      INT AUTO_INCREMENT,\n" +
                        "        `login`        VARCHAR(100) NOT NULL,\n" +
                        "        `hash_pass`    VARCHAR(100) NOT NULL,\n" +
                        "         PRIMARY KEY(`user_id`)\n" +
                        "    );";

        //Пока что не указываю от кого -_-
        final String createMessagesTable =
                        "CREATE TABLE\n" +
                        "    `messages` (\n" +
                        "        `message_id`   INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "        `user_id`      INT,\n" +
                        "        `date`         VARCHAR(100) NOT NULL,\n" +              //It must be a DATA
                        "        `message`      LONGTEXT,\n" +
                        "        FOREIGN KEY (user_id) REFERENCES users(user_id)\n" +
                        "    );";

        //если таблицы ещё не созданы
//        try(Connection con = sql2o.beginTransaction()) {
//            con.createQuery(createUsersTable).executeUpdate();
//            con.createQuery(createMessagesTable).executeUpdate();
//            con.commit();
//        }


        /**
         *  Add some people
         */
        final String insertPerson =
                "INSERT INTO\n" +
                "    `users` (`login`, `hash_pass`)\n" +
                "VALUES\n" +
                "    (:login_param, :hash_pass_param);";


        String person = "person" + String.valueOf(Math.abs(random.nextInt() % 1000));

        try(Connection con = sql2o.beginTransaction()) {
            con.createQuery(insertPerson)
                    .addParameter("login_param", person)
                    .addParameter("hash_pass_param", person.hashCode()).executeUpdate();
            con.commit();
        }


        /**
         *  And messages
         */
        Long sizePersonTable = (Long)sql2o
                .createQuery("SELECT count(*) sizePersonTable FROM `users`;")
                .executeScalar();

        final String insertMessage =
                "INSERT INTO\n" +
                "    `messages` (`user_id`, `date`, `message`)\n" +
                "VALUES\n" +
                "    (:user_id, :date_param, :message_param);";


        final String date = (new Date(System.currentTimeMillis())).toString();
        final Integer id1 = Math.abs(random.nextInt() %  (int) (long) sizePersonTable) + 1;
        final Integer id2 = Math.abs(random.nextInt() % (int) (long) sizePersonTable) + 1;

        try(Connection con = sql2o.beginTransaction()) {

            con.createQuery(insertMessage)
                    .addParameter("user_id", id1)
                    .addParameter("date_param", date)
                    .addParameter("message_param", "loooooooongtext #id" + id1.toString())
                    .executeUpdate();

            con.createQuery(insertMessage)
                    .addParameter("user_id", id2)
                    .addParameter("date_param", date)
                    .addParameter("message_param", "loooooooongtext #id" + id2.toString())
                    .executeUpdate();

            con.commit();
        }


        /**
         *  Get: Login --> Messages
         *  (naive)
         */

        final Integer id3 = Math.abs(random.nextInt() % (int) (long) sizePersonTable) + 1;
        final String selectSqlM =
                "SELECT message FROM users NATURAL JOIN messages WHERE user_id=" + id3.toString() + ";";
        final String selectSqlP =
                "SELECT login FROM users NATURAL JOIN messages WHERE user_id=" + id3.toString() + ";";

        Object pers = sql2o.createQuery(selectSqlP).executeScalar();
        Object mess = sql2o.createQuery(selectSqlM).executeScalar();

        System.out.println(pers + " <~~ \"" + mess + "\"");
         assertThat(true, is(equalTo(true)));

    }
}