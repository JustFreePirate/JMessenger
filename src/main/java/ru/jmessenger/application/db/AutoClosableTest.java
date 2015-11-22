package ru.jmessenger.application.db;

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
 *  Файл неактуален
 */

/**
 * Created by Сергей on 14.11.2015.
 *
 * Как сделать так, чтобы работало:
 * Скачать файл ojdbc6.jar версии 11.2.0.3 отсюда:
 *      http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
 *
 * На самом деле, он будет версии 11.2.0.4, но у меня ничего не заработало,
 *      и я его просто переименовал в 11.2.0.3, и писал везде 0.3
 *
 * Затем, идём в репозиторий Maven
 *      Путь по умолчанию: "C:\Users\%username%\.m2\repository"
 * И добавляем скачанный файл в репозиторий, должно получиться так:
 *      C:\Users\%username%\.m2\repository\oracle\ojdbc6\11.2.0.3\ojdbc6-11.2.0.3.jar
 *
 * Теперь большая инструкция, как установить SQL
 *      http://www.tutorialspoint.com/jdbc/jdbc-environment-setup.htm
 *
 *  Касательно пункта "что скачивать", я скачивал это:
 *      http://dev.mysql.com/downloads/windows/installer/5.7.html
 *      http://dev.mysql.com/downloads/connector/j/5.0.html
 *
 *  При установке MySql, указать установку только сервера
 *
 *  Создаем базу данных my_db (не таблицу).
 */
public class AutoClosableTest {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/my_db";
    static final String USER = "admin";
    static final String PASS = "admin";

    // см. http://www.sql2o.org/
    private final Sql2o sql2o = new Sql2o(DB_URL, USER, PASS);

    private final Random random = new Random();

    @Test
    public void testAutoClosable() throws Exception {
        Class.forName(JDBC_DRIVER).newInstance();

        /**
         *  Create all tables
         */
        //Запрос на создание таблицы user-hash(pass)
        final String createUsersTable =
                "CREATE TABLE\n" +
                        "    `users` (\n" +
                        "        `user_id`      INT AUTO_INCREMENT,\n" +
                        "        `login`        VARCHAR(100) NOT NULL,\n" +
                        "        `hash_pass`    VARCHAR(100) NOT NULL,\n" +
                        "         PRIMARY KEY(`user_id`)\n" +
                        "    );";

        //Создание таблицы с недоставленными сообщениями
        //Пока что не указываю от кого
        final String createMessagesTable =
                "CREATE TABLE\n" +
                        "    `messages` (\n" +
                        "        `message_id`   INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "        `sender_id`      INT,\n" +
                        "        `recipient_id`      INT,\n" +
                        "        `date`         VARCHAR(100) NOT NULL,\n" +
                        "        `message`      LONGTEXT,\n" +
                        "        FOREIGN KEY (sender_id) REFERENCES users(user_id)\n" +
                        "    );";

        //Если таблицы ещё не созданы, создаем.
        //если таблицы уже есть, оно упадет
        //попозже добавлю какой-нибудь if, или улучшу sql-запрос
//        try(Connection con = sql2o.beginTransaction()) {
//            //Выполняем запрос на создание таблицы
//            con.createQuery(createUsersTable).executeUpdate();
//            con.createQuery(createMessagesTable).executeUpdate();
//            con.commit();
//        }


        /**
         *  Add some people
         */
        //Запрос на создание записи в базе
        final String insertPerson =
                "INSERT INTO\n" +
                        "    `users` (`login`, `hash_pass`)\n" +
                        "VALUES\n" +
                        "    (:login_param, :hash_pass_param);";

        //Генерирую случайное имя
        String person = "person" + String.valueOf(Math.abs(random.nextInt() % 1000));

        try(Connection con = sql2o.beginTransaction()) {
            //Добавление записи в таблицу
            // person.hashCode(), вообще, это hash(login), но могло бы быть hasp(pass)
            con.createQuery(insertPerson)
                    .addParameter("login_param", person)
                    .addParameter("hash_pass_param", person.hashCode()).executeUpdate();
            con.commit();
        }


        /**
         *  And messages
         */
        //Узнаем, сколько записей в таблице
        Long sizePersonTable = (Long)sql2o
                .createQuery("SELECT count(*) sizePersonTable FROM `users`;")
                .executeScalar();

        //Тут нужен другой запрос, так как таблица с сообщениями немного другая
        final String insertMessage =
                "INSERT INTO\n" +
                        "    `messages` (`sender_id`, `recipient_id`, `date`, `message`)\n" +
                        "VALUES\n" +
                        "    (:sender_id, :recipient_id, :date_param, :message_param);";


        //Добавляю немного случайности
        final String date = (new Date(System.currentTimeMillis())).toString();
        final Integer id1 = Math.abs(random.nextInt() %  (int) (long) sizePersonTable) + 1;
        final Integer id2 = Math.abs(random.nextInt() % (int) (long) sizePersonTable) + 1;

        try(Connection con = sql2o.beginTransaction()) {
            //Добавляем в базу с сообщениями сообщения
            con.createQuery(insertMessage)
                    .addParameter("sender_id", id1)
                    .addParameter("recipient_id", id2)
                    .addParameter("date_param", date)
                    .addParameter("message_param", "loooooooongtext #id" + id1.toString())
                    .executeUpdate();

            con.createQuery(insertMessage)
                    .addParameter("sender_id", id2)
                    .addParameter("recipient_id", id1)
                    .addParameter("date_param", date)
                    .addParameter("message_param", "loooooooongtext #id" + id2.toString())
                    .executeUpdate();

            con.commit();
        }


        /**
         *  Get: Login --> Messages
         *  (naive)
         */
        //ещё чуть-чуть случайности
        final Integer id3 = Math.abs(random.nextInt() % (int) (long) sizePersonTable) + 1;
        //"Сложный" запрос на получение сообщения (одного) по логину
        final String selectSqlM =
                "SELECT message FROM users NATURAL JOIN messages WHERE user_id=" + id3.toString() + ";";
        final String selectSqlP =
                "SELECT login FROM users NATURAL JOIN messages WHERE user_id=" + id3.toString() + ";";

        Object pers = sql2o.createQuery(selectSqlP).executeScalar();
        Object mess = sql2o.createQuery(selectSqlM).executeScalar();

        System.out.println(pers + " <~~ \"" + mess + "\"");

        //Тут проверка для @Test, но она тут всегда true
        assertThat(true, is(equalTo(true)));

    }
}