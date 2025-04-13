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

public class AddGlossaryFragment extends Fragment {

    private EditText conceptEditText, conceptDefinitionEditText;
    private Button addConceptButton;
    private ListView glossaryListView;
    private DatabaseReference conceptsRef;
    private ArrayList<Term> termList;
    private GlossaryAdapter glossaryAdapter;

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

    private void addConcept() {
        String term = conceptEditText.getText().toString().trim();
        String definition = conceptDefinitionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(term) || TextUtils.isEmpty(definition)) {
            Toast.makeText(getContext(), "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // creates unique ID
        String id = conceptsRef.push().getKey();
        if (id == null) {
            Toast.makeText(getContext(), "שגיאה ביצירת מזהה ייחודי", Toast.LENGTH_SHORT).show();
            return;
        }

        Term newTerm = new Term(term, definition);
        newTerm.setId(id);

        // save to firebase with the unique ID
        conceptsRef.child(id).setValue(newTerm).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "המושג נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                conceptEditText.setText("");
                conceptDefinitionEditText.setText("");
            } else {
                Toast.makeText(getContext(), "שגיאה בהוספת המושג", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadConcepts() {
        conceptsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Term> terms = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey(); // the id
                    Term term = snapshot.getValue(Term.class);
                    if (term != null) {
                        term.setId(id); // Save the ID temporarily (not saved in Firebase)
                        terms.add(term);
                    }
                }
                // Update the Adapter with the list
                glossaryAdapter.clear();
                glossaryAdapter.addAll(terms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "שגיאה בטעינת המושגים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editTerm(Term term, String updatedTerm, String updatedDefinition) {
        if (TextUtils.isEmpty(updatedTerm) || TextUtils.isEmpty(updatedDefinition)) {
            Toast.makeText(getContext(), "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        term.setTerm(updatedTerm);
        term.setDefinition(updatedDefinition);

        conceptsRef.child(term.getId()).setValue(term).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "המושג עודכן בהצלחה", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "שגיאה בעדכון המושג", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteTerm(Term term) {
        conceptsRef.child(term.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "המושג נמחק בהצלחה", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "שגיאה במחיקת המושג", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
