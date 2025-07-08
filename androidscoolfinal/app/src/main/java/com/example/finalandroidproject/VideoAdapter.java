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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Adapter class for displaying a list of Video items in a ListView.
 * Provides options to play the video via URL or delete it from Firebase.
 */
public class VideoAdapter extends android.widget.ArrayAdapter<Video> {

    private final Context context;
    private final List<Video> videoList;
    private final Fragment fragment;

    /**
     * Constructs a VideoAdapter for displaying video items.
     *
     * @param context   The context (usually the activity)
     * @param videoList The list of videos to display
     * @param fragment  The fragment using this adapter (used for context)
     */
    public VideoAdapter(Context context, List<Video> videoList, Fragment fragment) {
        super(context, 0, videoList);
        this.context = context;
        this.videoList = videoList;
        this.fragment = fragment;
    }

    /**
     * Creates and returns the view for each item in the list.
     *
     * @param position    The position of the item within the adapter's data set
     * @param convertView The recycled view to populate
     * @param parent      The parent view group
     * @return A View corresponding to the video item
     */
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

        // Play video button: opens the video URL in a browser or YouTube app
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Delete video button: prompts for confirmation
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(video));

        return convertView;
    }

    /**
     * Shows a confirmation dialog before deleting the video.
     *
     * @param video The video to be deleted
     */
    private void showDeleteConfirmationDialog(Video video) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton("Yes", (dialog, which) -> deleteVideo(video))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes the video from Firebase and updates the local list.
     *
     * @param video The video to delete
     */
    private void deleteVideo(Video video) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("Videos").child(video.getId());
        videoRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                videoList.remove(video);
                notifyDataSetChanged();
                Toast.makeText(context, "Video deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete video", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
