package edu.davydov.web;

import edu.davydov.dao.BookDao;
import edu.davydov.model.Book;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class BookController extends HttpServlet {
    private static final Logger log = getLogger(BookController.class);
    private final BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete" -> {
                int id = getId(request);
                bookDao.delete(id);
                response.sendRedirect("books");
            }
            case "create", "update" -> {
                final Book book = "create".equals(action) ?
                        new Book("", "", 1) :
                        bookDao.get(getId(request));
                request.setAttribute("book", book);
                request.getRequestDispatcher("/bookForm.jsp").forward(request, response);
            }
            default -> {
                request.setAttribute("books", bookDao.findAll());
                request.getRequestDispatcher("/books.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Book book = new Book(id.isEmpty() ? null : Integer.valueOf(id),
                request.getParameter("title"),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("year")));

        log.info(book.isNew() ? "Create {}" : "Update {}", book);
        bookDao.save(book);
        response.sendRedirect("books");
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
