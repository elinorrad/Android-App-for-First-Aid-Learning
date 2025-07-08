package com.example.finalandroidproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Service for uploading a JSON file containing quiz questions to Firebase.
 * Runs in the background and displays a notification on success or failure.
 */
public class JsonUploadService extends Service {

    private static final String TAG = "JsonUploadService";
    private static final String CHANNEL_ID = "json_upload_channel";
    private Handler handler;

    /**
     * This service is not designed to be bound.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is first created. Initializes the handler and notification channel.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    /**
     * Called when the service is started. Begins uploading JSON if filePath is provided.
     *
     * INPUT:
     * - intent: Contains "filePath" extra (path to local JSON file)
     * OUTPUT:
     * - Starts file upload and returns START_NOT_STICKY (service does not restart automatically)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("filePath")) {
            String filePath = intent.getStringExtra("filePath");
            uploadJsonToFirebase(filePath);
        }
        return START_NOT_STICKY;
    }

    /**
     * Parses the JSON file, creates Quiz objects, and uploads them to Firebase.
     *
     * INPUT:
     * - filePath: Absolute path to the local JSON file
     * OUTPUT:
     * - Inserts data into Firebase and displays success/failure notification
     */
    private void uploadJsonToFirebase(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "File not found");
            showNotification("Error", "File not found");
            return;
        }

        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("questions");

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Questions");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);

                Quiz quiz = new Quiz(
                        questionObject.getString("question"),
                        questionObject.getString("answer1"),
                        questionObject.getString("answer2"),
                        questionObject.getString("answer3"),
                        questionObject.getString("answer4"),
                        questionObject.getString("correctAnswer"),
                        questionObject.getString("topic"),
                        questionObject.getString("difficulty")
                );

                String topic = quiz.getTopic();
                String difficulty = quiz.getDifficulty();
                String questionId = databaseRef.child(topic).child(difficulty).push().getKey();

                if (questionId != null) {
                    databaseRef.child(topic).child(difficulty).child(questionId).setValue(quiz);
                }
            }

            Log.d(TAG, "JSON upload to Firebase completed successfully.");
            showNotification("Success", "JSON upload completed successfully!");

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error reading file or uploading data", e);
            showNotification("Error", "Failed to upload JSON: " + e.getMessage());
        }
    }

    /**
     * Displays a system notification with the provided title and message.
     *
     * INPUT:
     * - title: Notification title
     * - message: Notification content
     */
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Ensure this icon exists in res/drawable
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Notification permission not granted (Android 13+)
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    /**
     * Creates a notification channel for Android O+ so that notifications can be displayed.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "JSON Upload Channel";
            String description = "Channel for JSON upload notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
