package com.example.finalandroidproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class VideoAdapter extends android.widget.ArrayAdapter<Video> {
    private Context context;
    private List<Video> videoList;
    private Fragment fragment;

    public VideoAdapter(Context context, List<Video> videoList, Fragment fragment) {
        super(context, 0, videoList);
        this.context = context;
        this.videoList = videoList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        }

        Video video = videoList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.tv_video_title);
        TextView categoryTextView = convertView.findViewById(R.id.tv_video_category);
        ImageButton playButton = convertView.findViewById(R.id.btn_play_video);
        ImageButton deleteButton = convertView.findViewById(R.id.btn_delete_video);

        titleTextView.setText(video.getTitle());
        categoryTextView.setText(video.getCategory());

        // כפתור לניגון הסרטון
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // כפתור למחיקת הסרטון
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(video));

        return convertView;
    }

    private void showDeleteConfirmationDialog(Video video) {
        new AlertDialog.Builder(context)
                .setTitle("מחיקת סרטון")
                .setMessage("האם אתה בטוח שברצונך למחוק את הסרטון?")
                .setPositiveButton("כן", (dialog, which) -> deleteVideo(video))
                .setNegativeButton("לא", null)
                .show();
    }

    private void deleteVideo(Video video) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("Videos").child(video.getId());
        videoRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                videoList.remove(video);
                notifyDataSetChanged();
                Toast.makeText(context, "הסרטון נמחק בהצלחה", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "שגיאה במחיקת הסרטון", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
