package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Fragment that displays a list of unique video categories loaded from Firebase.
 * Selecting a category navigates the user to a list of videos in that category.
 */
public class VideosFragment extends Fragment {

    private ListView categoryListView;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayList<String> categoryList;
    private DatabaseReference databaseReference;

    /**
     * Inflates the view and initializes the ListView of categories.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        categoryListView = view.findViewById(R.id.category_list_view);
        categoryList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, categoryList);
        categoryListView.setAdapter(categoryAdapter);

        loadCategories();

        categoryListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCategory = categoryList.get(position);
            openVideoListFragment(selectedCategory);
        });

        return view;
    }

    /**
     * Loads all unique video categories from the "Videos" node in Firebase.
     * Updates the list view with the retrieved categories.
     */
    private void loadCategories() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Videos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashSet<String> categoriesSet = new HashSet<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot videoSnapshot : dataSnapshot.getChildren()) {
                        String category = videoSnapshot.child("category").getValue(String.class);
                        if (category != null) {
                            categoriesSet.add(category);
                        }
                    }
                }

                categoryList.clear();
                categoryList.addAll(categoriesSet);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load categories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Opens a new fragment that displays all videos within the selected category.
     *
     * @param category The selected video category
     */
    private void openVideoListFragment(String category) {
        VideoListFragment videoListFragment = VideoListFragment.newInstance(category);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, videoListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
