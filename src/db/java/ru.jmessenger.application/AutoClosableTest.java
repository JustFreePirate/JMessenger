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


    @Test
    public void testAutoClosable() throws Exception {
        Class.forName(JDBC_DRIVER).newInstance();

        final String createSql =
                "CREATE TABLE\n" +
                "    `users` (\n" +
                "        `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "        `login` CHAR(30) NOT NULL,\n" +
                "        `password` CHAR(30) NOT NULL,\n" +
                "        PRIMARY KEY(`id`)\n" +
                "    );";

        final String insertSql =
                "INSERT INTO\n" +
                "    `users` (`login`, `password`)\n" +
                "VALUES\n" +
                "    (:loginParam, :passwordParam);";

        final String selectSql =
                "SELECT\n" +
                "    *\n" +
                "FROM\n" +
                "    `users`;";


        final Random random = new Random();

        try(Connection con = sql2o.beginTransaction()) {
            //Если таблица ещё не создана
            //con.createQuery(createSql).executeUpdate();

            String person = "person" + String.valueOf(random.nextInt() % 1000);

            con.createQuery(insertSql)
                    .addParameter("loginParam", person)
                    .addParameter("passwordParam", person.hashCode()).executeUpdate();
            con.commit();
        }

        final String selectCount =
                "SELECT\n" +
                "    count(*) cnt \n" +
                "FROM\n" +
                "    `users`;";

        //final String selectUser = "select * from db_guest where (name='$user')"

        Long cnt = (Long)sql2o.createQuery(selectCount).executeScalar();
        //всегда true
        assertThat(cnt, is(equalTo(cnt)));

    }
}