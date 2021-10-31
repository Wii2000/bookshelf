DELETE FROM info;
DELETE FROM book;

ALTER TABLE book AUTO_INCREMENT = 1;

INSERT INTO book (title)
VALUES ('Book1'),
       ('Book2'),
       ('Book3');

INSERT INTO info (book_id, description, year)
VALUES (1, 'First book', 2001),
       (2, 'Second book', 2002),
       (3, 'Third book', 2003);