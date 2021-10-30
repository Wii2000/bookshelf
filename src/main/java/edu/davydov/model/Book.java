package edu.davydov.model;

public class Book {
    private Integer id;
    private final String title;
    private final String description;
    private final int year;

    public Book(String title, String description, int year) {
        this(null, title, description, year);
    }

    public Book(Integer id, String title, String description, int year) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getYear() {
        return year;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                '}';
    }
}
