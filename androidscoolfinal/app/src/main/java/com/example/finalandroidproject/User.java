package com.example.finalandroidproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Model class representing a user in the application.
 * Stores user information including name, admin status, test results, and profile image.
 */
public class User {

    private String userId;
    private String name;
    private boolean isAdmin;
    private HashMap<String, Integer> testResults;
    private String profileImageUrl;

    /**
     * Constructs a new User with the given ID and name.
     * Initializes test results and sets a default profile image.
     *
     * @param userId The unique ID of the user (typically from Firebase Auth)
     * @param name   The display name of the user
     */
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.isAdmin = false;
        this.testResults = new HashMap<>();
        this.profileImageUrl = "https://cdn1.iconfinder.com/data/icons/basic-ui-set-v5-user-outline/64/Account_profile_user_avatar_rounded-512.png";
        addInitialTestResult();
    }

    /**
     * Adds an initial test result with 0% success rate at the current date and time.
     * Used when the user is first created.
     */
    private void addInitialTestResult() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        testResults.put(currentDate, 0);
    }

    /**
     * Saves the current User object to Firebase Realtime Database.
     * The user is stored under "Users/{userId}".
     */
    public void saveToFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.setValue(this);
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public HashMap<String, Integer> getTestResults() {
        return testResults;
    }

    public void setTestResults(HashMap<String, Integer> testResults) {
        this.testResults = testResults;
    }

    /**
     * Adds a test result to the user’s history.
     *
     * @param date  The date of the test (formatted as string)
     * @param score The score achieved (percentage)
     */
    public void addTestResult(String date, int score) {
        this.testResults.put(date, score);
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
