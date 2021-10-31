package edu.davydov.dao;

import edu.davydov.BookTestData;
import edu.davydov.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static edu.davydov.BookTestData.*;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class BookDaoTest {
    private static final String MYSQL_LATEST_IMAGE = "mysql:latest";
    private static Connection connection;
    private static BookDao dao;

    @Container
    private static JdbcDatabaseContainer mysql = new MySQLContainer<>(MYSQL_LATEST_IMAGE)
            .withInitScript("db/initDB.sql");

    //create connection to container and create DAO object
    @BeforeAll
    public static void setup() throws SQLException {
        connection = DriverManager.getConnection(
                mysql.getJdbcUrl(),
                mysql.getUsername(),
                mysql.getPassword()
        );
        dao = new BookDao(connection);
    }

    //before each test reset database to the initial state
    @BeforeEach
    public void refreshData() throws SQLException {
        Statement statement = connection.createStatement();
        statement.addBatch("DELETE FROM info;");
        statement.addBatch("DELETE FROM book;");
        statement.addBatch("ALTER TABLE book AUTO_INCREMENT = 1;");
        statement.addBatch("""
                INSERT INTO book (title)
                VALUES ('Book1'),
                       ('Book2'),
                       ('Book3');
                """);
        statement.addBatch("""
                INSERT INTO info (book_id, description, year)
                VALUES (1, 'First book', 2001),
                       (2, 'Second book', 2002),
                       (3, 'Third book', 2003);
                       """);
        statement.executeBatch();
    }

    @Test
    void save() {
        Book newBook = getNew();
        Book saved = dao.save(newBook);
        int id = saved.getId();
        newBook.setId(id);
        assertEquals(newBook, dao.get(id));
    }

    @Test
    void update() {
        Book updatedBook = getUpdated();
        dao.save(updatedBook);
        assertEquals(updatedBook, dao.get(BOOK_1_ID));
    }

    @Test
    void delete() {
        assertTrue(dao.delete(BOOK_1_ID));
        assertNull(dao.get(BOOK_1_ID));
    }

    @Test
    void deleteNotFound() {
        assertFalse(dao.delete(NOT_FOUND_ID));
    }

    @Test
    void get() {
        dao.get(BOOK_1_ID);
        assertEquals(book1, dao.get(BOOK_1_ID));
    }

    @Test
    void getNotFound() {
        assertNull(dao.get(NOT_FOUND_ID));
    }

    @Test
    void findAll() {
        List<Book> books = dao.findAll();
        assertEquals(BookTestData.books, books);
    }
}