package com.example.finalandroidproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddVideoFragment extends Fragment {

    private EditText videoTitleEditText, videoUrlEditText;
    private Spinner videoCategorySpinner;
    private Button addVideoButton;
    private ListView videoListView;
    private DatabaseReference videosRef;
    private ArrayList<Video> videoList;
    private VideoAdapter videoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_video, container, false);

        videoTitleEditText = view.findViewById(R.id.et_video_title);
        videoUrlEditText = view.findViewById(R.id.et_video_url);
        videoCategorySpinner = view.findViewById(R.id.spinner_video_category);
        addVideoButton = view.findViewById(R.id.btn_add_video);
        videoListView = view.findViewById(R.id.lv_videos);

        videosRef = FirebaseDatabase.getInstance().getReference("Videos");
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(getContext(), videoList, this);
        videoListView.setAdapter(videoAdapter);

        // טעינת רשימת הנושאים לתוך הספינר
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.topics_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        videoCategorySpinner.setAdapter(adapter);

        addVideoButton.setOnClickListener(v -> addVideo());

        loadVideos();

        return view;
    }

    private void addVideo() {
        String title = videoTitleEditText.getText().toString().trim();
        String url = videoUrlEditText.getText().toString().trim();
        String category = videoCategorySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(url)) {
            Toast.makeText(getContext(), "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = videosRef.push().getKey();
        if (id == null) {
            Toast.makeText(getContext(), "שגיאה ביצירת מזהה", Toast.LENGTH_SHORT).show();
            return;
        }

        Video newVideo = new Video(title, url, category);
        newVideo.setId(id);

        videosRef.child(id).setValue(newVideo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "הסרטון נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                videoTitleEditText.setText("");
                videoUrlEditText.setText("");
                videoCategorySpinner.setSelection(0);
            } else {
                Toast.makeText(getContext(), "שגיאה בהוספת הסרטון", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVideos() {
        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Video> videos = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Video video = snapshot.getValue(Video.class);
                    if (video != null) {
                        video.setId(snapshot.getKey());
                        videos.add(video);
                    }
                }
                videoAdapter.clear();
                videoAdapter.addAll(videos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "שגיאה בטעינת הסרטונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
