package model;

import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private Integer pages;
    private String publishDate;

    private List<String> genres;
    private List<String> awards;
    private List<String> bestsellers;
    private List<String> otherSubjects;

    public Book(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public void setBestsellers(List<String> bestsellers) {
        this.bestsellers = bestsellers;
    }

    public void setOtherSubjects(List<String> otherSubjects) {
        this.otherSubjects = otherSubjects;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", pages=" + pages +
                ", publishDate='" + publishDate + '\'' +
                ", genres=" + genres +
                ", awards=" + awards +
                ", bestsellers=" + bestsellers +
                ", otherSubjects=" + otherSubjects +
                '}';
    }
}
