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

public class JsonUploadService extends Service {

    private static final String TAG = "JsonUploadService";
    private static final String CHANNEL_ID = "json_upload_channel";
    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("filePath")) {
            String filePath = intent.getStringExtra("filePath");
            uploadJsonToFirebase(filePath);
        }
        return START_NOT_STICKY;
    }

    private void uploadJsonToFirebase(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "הקובץ לא נמצא");
            showNotification("שגיאה", "הקובץ לא נמצא");
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

                // יצירת אובייקט Quiz
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

            Log.d(TAG, "טעינת JSON ל-Firebase הושלמה");
            showNotification("הצלחה", "העלאת JSON הסתיימה בהצלחה!");

        } catch (IOException | JSONException e) {
            Log.e(TAG, "שגיאה בקריאת הקובץ או העלאת הנתונים", e);
            showNotification("שגיאה", "שגיאה בהעלאת JSON: " + e.getMessage());
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // ודא שיש לך אייקון מתאים בתיקיית res/drawable
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Json Upload Channel";
            String description = "ערוץ להתראות העלאת JSON";
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
