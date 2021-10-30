<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Book</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<section>
    <h2>${param.action == 'create' ? 'Add book' : 'Edit book'}</h2>
    <jsp:useBean id="book" type="edu.davydov.model.Book" scope="request"/>
    <form method="post" action="books">
        <input type="hidden" name="id" value="${book.id}">
        <dl>
            <dt>Title:</dt>
            <dd><input type="text" value="${book.title}" name="title" required></dd>
        </dl>
        <dl>
            <dt>Description:</dt>
            <dd><input type="text" value="${book.description}" size=100 name="description" required></dd>
        </dl>
        <dl>
            <dt>Year:</dt>
            <dd><input type="number" value="${book.year}" min="1" max="2021" name="year" required></dd>
        </dl>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button">Cancel</button>
    </form>
</section>
</body>
</html>
