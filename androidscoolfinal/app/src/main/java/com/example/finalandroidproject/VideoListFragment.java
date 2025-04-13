package com.example.finalandroidproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;

public class VideoListFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;

    private ListView videoListView;
    private ArrayAdapter<String> videoAdapter;
    private ArrayList<String> videoTitles;
    private HashMap<String, String> videoUrls;
    private DatabaseReference databaseReference;

    public static VideoListFragment newInstance(String category) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        TextView titleTextView = view.findViewById(R.id.tv_category_title);
        titleTextView.setText(category);

        videoListView = view.findViewById(R.id.video_list_view);
        videoTitles = new ArrayList<>();
        videoUrls = new HashMap<>();
        videoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, videoTitles);
        videoListView.setAdapter(videoAdapter);

        loadVideos();

        videoListView.setOnItemClickListener((parent, view1, position, id) -> openVideoInBrowser(videoTitles.get(position)));

        return view;
    }

    private void loadVideos() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Videos");
        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoTitles.clear();
                videoUrls.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String url = snapshot.child("url").getValue(String.class);
                    if (title != null && url != null) {
                        videoTitles.add(title);
                        videoUrls.put(title, url);
                    }
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת הסרטונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openVideoInBrowser(String title) {
        String url = videoUrls.get(title);
        if (url == null || url.isEmpty()) {
            Toast.makeText(getContext(), "קישור לא תקף", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "הסרטון ייפתח בדפדפן", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome"); // נסה לפתוח ישירות בכרום אם קיים
        startActivity(intent);
    }
}
