package edu.davydov;

import edu.davydov.model.Book;

import java.util.List;

public class BookTestData {
    public static final Integer BOOK_1_ID = 1;
    public static final Integer BOOK_2_ID = 2;
    public static final Integer BOOK_3_ID = 3;
    public static final Integer NOT_FOUND_ID = 1000;
    public static final Book book1 = new Book(BOOK_1_ID, "Book1", "First book", 2001);
    public static final Book book2 = new Book(BOOK_2_ID, "Book2", "Second book", 2002);
    public static final Book book3 = new Book(BOOK_3_ID, "Book3", "Third book", 2003);
    public static final List<Book> books = List.of(book1, book2, book3);

    public static Book getNew() {
        return new Book("New", "New", 2000);
    }

    public static Book getUpdated() {
        return new Book(BOOK_1_ID, "Updated", "Updated", 2000);
    }
}
