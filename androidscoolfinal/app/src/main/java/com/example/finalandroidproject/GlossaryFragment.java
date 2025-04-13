package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class GlossaryFragment extends Fragment {

    private ListView glossaryListView;
    private ArrayAdapter<String> glossaryAdapter;
    private ArrayList<String> glossaryList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glossary, container, false);

        glossaryListView = view.findViewById(R.id.glossary_list_view);
        glossaryList = new ArrayList<>();
        glossaryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, glossaryList);
        glossaryListView.setAdapter(glossaryAdapter);

        loadGlossary();

        return view;
    }

    private void loadGlossary() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Concepts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                glossaryList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot conceptSnapshot : dataSnapshot.getChildren()) {
                        String term = conceptSnapshot.child("term").getValue(String.class);
                        String definition = conceptSnapshot.child("definition").getValue(String.class);

                        if (term != null && definition != null) {
                            glossaryList.add(term + ": " + definition);
                        } else {
                            glossaryList.add("Error: Missing term or definition");
                        }
                    }
                } else {
                    System.out.println("Debug: No data found in 'Concepts' reference");
                }

                glossaryAdapter.notifyDataSetChanged();

                if (glossaryList.isEmpty()) {
                    Toast.makeText(requireContext(), "No concepts found in the glossary.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load glossary: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
