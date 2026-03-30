package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<Book> books = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public List<Book> getBooks() {
        return books;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", books=" + books +
                '}';
    }
}
