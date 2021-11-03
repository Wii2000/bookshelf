package edu.davydov.web;

import edu.davydov.dao.BookDao;
import edu.davydov.model.Book;
import edu.davydov.util.DataSourceFactory;
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
    public static final String DELETE = "delete";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String ALL = "all";
    private final BookDao bookDao;

    public BookController() {
        bookDao = new BookDao(DataSourceFactory.getConnection());
    }

    public BookController(BookDao dao) {
        bookDao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? ALL : action) {
            case DELETE -> {
                int id = getId(request);
                bookDao.delete(id);
                response.sendRedirect("books");
            }
            case CREATE, UPDATE -> {
                final Book book = CREATE.equals(action) ?
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
