package edu.davydov.web;

import edu.davydov.dao.BookDao;
import edu.davydov.model.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.davydov.BookTestData.*;
import static edu.davydov.web.BookController.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {
    private static final String EMPTY_STRING = "";
    private static BookDao dao;
    private static HttpServletRequest request;
    private static HttpServletResponse response;
    private static RequestDispatcher dispatcher;
    private static BookController controller;
    // Collection to store HttpServletRequest keys/values attributes
    private static final Map<String, Object> attributes = new HashMap<>();

    @BeforeAll
    public static void setup() throws SQLException {
        //setup BookDao
        dao = mock(BookDao.class);
        when(dao.findAll()).thenReturn(books);
        when(dao.get(BOOK_1_ID)).thenReturn(book1);
        when(dao.delete(anyInt())).thenReturn(true);
        when(dao.save(getNew())).thenReturn(getNew());
        when(dao.save(getUpdated())).thenReturn(getUpdated());
        //setup servlets objects
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        //setup controller
        controller = new BookController(dao);

        //https://stackoverflow.com/a/30726246/16047333
        // Mock setAttribute
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String key = invocation.getArgument(0, String.class);
                Object value = invocation.getArgument(1, Object.class);
                attributes.put(key, value);
                return null;
            }
        }).when(request).setAttribute(Mockito.anyString(), Mockito.any());

        // Mock getAttribute
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String key = invocation.getArgument(0, String.class);
                return attributes.get(key);
            }
        }).when(request).getAttribute(Mockito.anyString());
    }

    @AfterEach
    void resetAttributeStorage() {
        attributes.clear();
    }

    @Test
    void doGet_Action_All() throws Exception {
        //reset only the number of invocations
        clearInvocations(dispatcher);
        //define mocks behavior for our case
        when(request.getParameter("action")).thenReturn(EMPTY_STRING);
        when(request.getRequestDispatcher("/books.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        //check request attributes
        Assertions.assertEquals(books, request.getAttribute("books"));
        // verify called methods
        verify(dispatcher, times(1)).forward(request, response);
    }

    @Test
    void doGet_Action_Delete() throws Exception {
        clearInvocations(response);

        when(request.getParameter("action")).thenReturn(DELETE);
        when(request.getParameter("id")).thenReturn(String.valueOf(BOOK_1_ID));
        when(request.getRequestDispatcher("/books.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(response, times(1)).sendRedirect("books");
    }

    @Test
    void doGet_Action_Create() throws Exception {
        clearInvocations(dispatcher);

        when(request.getParameter("action")).thenReturn(CREATE);
        when(request.getRequestDispatcher("/bookForm.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        Assertions.assertEquals(new Book("", "", 1), request.getAttribute("book"));
        verify(dispatcher, times(1)).forward(request, response);
    }

    @Test
    void doGet_Action_Update() throws Exception {
        clearInvocations(dispatcher);

        when(request.getParameter("action")).thenReturn(UPDATE);
        when(request.getParameter("id")).thenReturn(String.valueOf(BOOK_1_ID));
        when(request.getRequestDispatcher("/bookForm.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        Assertions.assertEquals(book1, request.getAttribute("book"));
        verify(dispatcher, times(1)).forward(request, response);
    }

    @Test
    void doPost_Action_Update() throws Exception {
        clearInvocations(response);

        when(request.getParameter("id")).thenReturn(String.valueOf(BOOK_1_ID));
        when(request.getParameter("title")).thenReturn(book1.getTitle());
        when(request.getParameter("description")).thenReturn(book1.getDescription());
        when(request.getParameter("year")).thenReturn(String.valueOf(book1.getYear()));

        controller.doPost(request, response);

        verify(dao, times(1)).save(book1);
        verify(response, times(1)).sendRedirect("books");
    }

    @Test
    void doPost_Action_Create() throws Exception {
        clearInvocations(response);

        Book newBook = getNew();

        when(request.getParameter("id")).thenReturn(EMPTY_STRING);
        when(request.getParameter("title")).thenReturn(newBook.getTitle());
        when(request.getParameter("description")).thenReturn(newBook.getDescription());
        when(request.getParameter("year")).thenReturn(String.valueOf(newBook.getYear()));

        controller.doPost(request, response);

        verify(dao, times(1)).save(newBook);
        verify(response, times(1)).sendRedirect("books");
    }
}