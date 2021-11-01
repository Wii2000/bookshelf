package edu.davydov.dao;

import edu.davydov.model.Book;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Class that respons for interaction with database.
 */
public class BookDao {
    private static final Logger log = getLogger(BookDao.class);
    private final Connection connection;

    public BookDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Save if object is new or update if not.
     *
     * @return saved object or null if can't save.
     */
    //TODO: add transactions
    public Book save(Book book) {
        try {
            //check if object has id
            if (book.isNew()) {
                // handle case: save in DB like new object
                // first step: work with "book" table
                String sql = "INSERT INTO book(title) VALUES (?)";
                try (PreparedStatement statement = connection.
                        prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, book.getTitle());
                    statement.executeUpdate();

                    //get generated book id
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            book.setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating book failed, no ID obtained.");
                        }
                    }
                }

                // second step: work with "info" table
                sql = "INSERT INTO info(book_id, description, year) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, book.getId());
                    statement.setString(2, book.getDescription());
                    statement.setInt(3, book.getYear());
                    statement.executeUpdate();
                }
            } else {
                // handle case: update existed object in DB
                // first step: work with "book" table
                String sql = "UPDATE book SET title=? WHERE id=?";
                try (PreparedStatement statement = connection.
                        prepareStatement(sql)) {
                    statement.setString(1, book.getTitle());
                    statement.setInt(2, book.getId());
                    statement.executeUpdate();
                }

                // insert into "info" table
                sql = "UPDATE info SET description=?, year=? WHERE book_id=?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, book.getDescription());
                    statement.setString(2, String.valueOf(book.getYear()));
                    statement.setString(3, String.valueOf(book.getId()));
                    //check that row was updated
                    if (statement.executeUpdate() < 1) {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("SQLException", ex);
        }
        return book;
    }

    /**
     * Retrieve all books from DB;
     *
     * @return list of all books
     */
    //TODO: add transactions
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = """
                SELECT id, title, description, year
                FROM book b
                    JOIN info i ON b.id = i.book_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("year")
                );
                books.add(book);
            }

        } catch (SQLException ex) {
            log.error("SQLException", ex);
        }

        return books;
    }

    /**
     * Get book by id.
     *
     * @param id
     * @return book or null if book with this id don't exist.
     */
    //TODO: add transactions
    public Book get(int id) {
        Book book = null;
        String sql = """
                SELECT id, title, description, year
                FROM book b
                    JOIN info i ON b.id = i.book_id
                WHERE b.id=?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("year")
                );
            }

        } catch (SQLException ex) {
            log.error("SQLException", ex);
        }

        return book;
    }

    /**
     * Delete element by id.
     *
     * @param id
     * @return true if element was deleted, false if not.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM book WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() < 1) {
                return false;
            }
        } catch (SQLException ex) {
            log.error("SQLException", ex);
        }

        return true;
    }
}
