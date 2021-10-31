DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS info;

CREATE TABLE book
(
    id    INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE info
(
    book_id     INTEGER,
    description VARCHAR(500) NOT NULL,
    year        INTEGER,
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES book (id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

INSERT INTO book (title)
VALUES ('Book1'),
       ('Book2'),
       ('Book3');

INSERT INTO info (book_id, description, year)
VALUES (1, 'First book', 2001),
       (2, 'Second book', 2002),
       (3, 'Third book', 2003);