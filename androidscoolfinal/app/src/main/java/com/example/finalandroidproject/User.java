package com.example.finalandroidproject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class User {
    private String userId;
    private String name;
    private boolean isAdmin;
    private HashMap<String, Integer> testResults;
    private String profileImageUrl;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.isAdmin = false;
        this.testResults = new HashMap<>();
        this.profileImageUrl = "https://cdn1.iconfinder.com/data/icons/basic-ui-set-v5-user-outline/64/Account_profile_user_avatar_rounded-512.png";
        addInitialTestResult();
    }

    private void addInitialTestResult() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        testResults.put(currentDate, 0); // התחלה עם 0 אחוזי הצלחה
    }

    public void saveToFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.setValue(this);
    }

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
