package com.example.finalandroidproject;

/**
 * Model class representing an educational video.
 * Contains the video's title, URL, category (topic), and a transient ID for in-memory use.
 */
public class Video {

    private String title;
    private String url;
    private String category;

    // Transient ID used for in-memory operations (not stored in Firebase)
    private transient String id;

    /**
     * Empty constructor required for Firebase deserialization.
     */
    public Video() {}

    /**
     * Constructs a new Video object.
     *
     * @param title    The title of the video
     * @param url      The URL of the video (e.g., YouTube link)
     * @param category The topic/category of the video
     */
    public Video(String title, String url, String category) {
        this.title = title;
        this.url = url;
        this.category = category;
    }

    // Getters and Setters

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

    /**
     * Gets the in-memory ID (used locally, not persisted in Firebase).
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the in-memory ID (used locally, not persisted in Firebase).
     */
    public void setId(String id) {
        this.id = id;
    }
}
