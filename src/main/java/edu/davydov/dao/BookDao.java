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
    public Book save(Book book) {
        try {
            // start transaction block
            connection.setAutoCommit(false);
            //set transaction isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            if (book.isNew()) {
                // handle case: save in DB like new object
                // first step: work with "book" table
                final String insertBookQuery = "INSERT INTO book(title) VALUES (?)";
                final String insertInfoQuery = "INSERT INTO info(book_id, description, year) VALUES (?, ?, ?)";

                try (PreparedStatement insertBookStm = connection
                        .prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement insertInfoStm = connection.prepareStatement(insertInfoQuery)) {
                    insertBookStm.setString(1, book.getTitle());
                    insertBookStm.executeUpdate();

                    //get generated book id
                    try (ResultSet generatedKeys = insertBookStm.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            book.setId(generatedKeys.getInt(1));
                        } else {
                            log.error("Creating book failed, no ID obtained.");
                            throw new SQLException();
                        }
                    }

                    // second step: work with "info" table
                    insertInfoStm.setInt(1, book.getId());
                    insertInfoStm.setString(2, book.getDescription());
                    insertInfoStm.setInt(3, book.getYear());
                    insertInfoStm.executeUpdate();
                }
            } else {
                // handle case: update existed object in DB
                // first step: work with "book" table
                final String updateBookQuery = "UPDATE book SET title=? WHERE id=?";
                final String updateInfoQuery = "UPDATE info SET description=?, year=? WHERE book_id=?";
                try (PreparedStatement updateBookStm = connection.prepareStatement(updateBookQuery);
                     PreparedStatement updateInfoStm = connection.prepareStatement(updateInfoQuery)) {
                    updateBookStm.setString(1, book.getTitle());
                    updateBookStm.setInt(2, book.getId());
                    updateBookStm.executeUpdate();

                    // insert into "info" table
                    updateInfoStm.setString(1, book.getDescription());
                    updateInfoStm.setString(2, String.valueOf(book.getYear()));
                    updateInfoStm.setString(3, String.valueOf(book.getId()));
                    //check that row was updated
                    if (updateInfoStm.executeUpdate() < 1) {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            try {
                connection.rollback();
                log.error("SQLException, transaction rolled back", ex);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return book;
    }

    /**
     * Retrieve all books from DB;
     *
     * @return list of all books
     */
    public List<Book> findAll() {
        final List<Book> books = new ArrayList<>();
        final String sql = """
                SELECT id, title, description, year
                FROM book b
                    JOIN info i ON b.id = i.book_id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            //set transaction isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

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
            log.error("SQLException, error was occur while reading elements", ex);
        }

        return books;
    }

    /**
     * Get book by id.
     *
     * @param id
     * @return book or null if book with this id don't exist.
     */
    public Book get(int id) {
        Book book = null;
        final String sql = """
                SELECT id, title, description, year
                FROM book b
                    JOIN info i ON b.id = i.book_id
                WHERE b.id=?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            //set transaction isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

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
            log.error("SQLException, error occur while attempt to get element by id", ex);
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
        final String sql = "DELETE FROM book WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            //set transaction isolation level
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            statement.setInt(1, id);
            if (statement.executeUpdate() < 1) {
                return false;
            }
        } catch (SQLException ex) {
            log.error("SQLException, error was occur while deleting element", ex);
        }

        return true;
    }
}
