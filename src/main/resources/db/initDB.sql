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

