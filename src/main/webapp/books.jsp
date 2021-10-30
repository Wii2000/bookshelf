<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Books</title>
</head>
<body>
<section>
    <a href="books?action=create">Add book</a>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Title</th>
            <th>Description</th>
            <th>Year</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach var="book" items="${books}">
            <tr>
                <td>${book.title}</td>
                <td>${book.description}</td>
                <td>${book.year}</td>
                <td><a href="books?action=update&id=${book.id}">Update</a></td>
                <td><a href="books?action=delete&id=${book.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>