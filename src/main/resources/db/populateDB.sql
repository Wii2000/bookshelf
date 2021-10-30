DELETE FROM info;
DELETE FROM book;

INSERT INTO book (title)
VALUES ('Book №1'),
       ('Book №2'),
       ('Book №3');

INSERT INTO info (book_id, description, year)
VALUES (1, 'First book', 2001),
       (2, 'Second book', 2002),
       (3, 'Third book', 2003);