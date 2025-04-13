package com.example.finalandroidproject;

public class Video {
    private String title;
    private String url;
    private String category; // נושא הסרטון

    private transient String id; // מזהה זמני שאינו נשמר בפיירבייס

    // בנאי ריק (נדרש עבור Firebase)
    public Video() {}

    // בנאי מלא
    public Video(String title, String url, String category) {
        this.title = title;
        this.url = url;
        this.category = category;
    }

    // Getters ו-Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
