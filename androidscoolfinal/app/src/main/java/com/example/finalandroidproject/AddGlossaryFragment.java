package com.example.finalandroidproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Fragment that allows the user to add new glossary terms,
 * display existing terms from Firebase Realtime Database,
 * and edit or delete them.
 */
public class AddGlossaryFragment extends Fragment {

    private EditText conceptEditText, conceptDefinitionEditText;
    private Button addConceptButton;
    private ListView glossaryListView;
    private DatabaseReference conceptsRef;
    private ArrayList<Term> termList;
    private GlossaryAdapter glossaryAdapter;

    /**
     * Initializes the view, Firebase reference, adapter, and event listeners.
     *
     * @param inflater LayoutInflater object to inflate views
     * @param container Parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, fragment is being re-constructed from previous state
     * @return The root view of the fragment's layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_glossary, container, false);

        conceptEditText = view.findViewById(R.id.et_concept);
        conceptDefinitionEditText = view.findViewById(R.id.et_concept_definition);
        addConceptButton = view.findViewById(R.id.btn_add_concept);
        glossaryListView = view.findViewById(R.id.lv_glossary);

        conceptsRef = FirebaseDatabase.getInstance().getReference("Concepts");
        termList = new ArrayList<>();
        glossaryAdapter = new GlossaryAdapter(getContext(), termList, this);

        glossaryListView.setAdapter(glossaryAdapter);

        addConceptButton.setOnClickListener(v -> addConcept());

        loadConcepts();

        return view;
    }

    /**
     * Adds a new concept (term + definition) to the Firebase database.
     * Validates input fields before saving.
     *
     * INPUT: None (reads from EditText fields)
     * OUTPUT: None (saves to Firebase and shows feedback to user)
     */
    private void addConcept() {
        String term = conceptEditText.getText().toString().trim();
        String definition = conceptDefinitionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(term) || TextUtils.isEmpty(definition)) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = conceptsRef.push().getKey();
        if (id == null) {
            Toast.makeText(getContext(), "Error generating unique ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Term newTerm = new Term(term, definition);
        newTerm.setId(id);

        conceptsRef.child(id).setValue(newTerm).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Term added successfully", Toast.LENGTH_SHORT).show();
                conceptEditText.setText("");
                conceptDefinitionEditText.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to add term", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads all concepts from Firebase and updates the ListView.
     * Listens for any changes in real-time and updates accordingly.
     *
     * INPUT: None
     * OUTPUT: Populates the ListView with Term objects
     */
    private void loadConcepts() {
        conceptsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Term> terms = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    Term term = snapshot.getValue(Term.class);
                    if (term != null) {
                        term.setId(id);
                        terms.add(term);
                    }
                }
                glossaryAdapter.clear();
                glossaryAdapter.addAll(terms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading terms", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates an existing term in the Firebase database.
     * Validates input before updating.
     *
     * INPUT: term - the Term object to update
     *        updatedTerm - the new term name
     *        updatedDefinition - the new definition
     * OUTPUT: None (updates Firebase and shows user feedback)
     */
    public void editTerm(Term term, String updatedTerm, String updatedDefinition) {
        if (TextUtils.isEmpty(updatedTerm) || TextUtils.isEmpty(updatedDefinition)) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        term.setTerm(updatedTerm);
        term.setDefinition(updatedDefinition);

        conceptsRef.child(term.getId()).setValue(term).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Term updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update term", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Deletes a term from the Firebase database.
     *
     * INPUT: term - the Term object to delete
     * OUTPUT: None (removes from Firebase and shows user feedback)
     */
    public void deleteTerm(Term term) {
        conceptsRef.child(term.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Term deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete term", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
