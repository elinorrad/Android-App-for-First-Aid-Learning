package com.example.finalandroidproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GlossaryAdapter extends ArrayAdapter<Term> {

    private final AddGlossaryFragment fragment;

    public GlossaryAdapter(@NonNull Context context, @NonNull ArrayList<Term> objects, AddGlossaryFragment fragment) {
        super(context, 0, objects);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_glossary, parent, false);
        }

        Term term = getItem(position);

        TextView termTextView = convertView.findViewById(R.id.tv_concept);
        TextView definitionTextView = convertView.findViewById(R.id.tv_definition);

        termTextView.setText(term.getTerm());
        definitionTextView.setText(term.getDefinition());

        termTextView.setOnClickListener(v -> showOptionsDialog(term));

        return convertView;
    }

    private void showOptionsDialog(Term term) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("בחר פעולה")
                .setItems(new String[]{"ערוך", "מחק"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(term);
                    } else if (which == 1) {
                        fragment.deleteTerm(term);
                    }
                })
                .show();
    }

    private void showEditDialog(Term term) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_term, null);
        builder.setView(dialogView);

        TextView termEditText = dialogView.findViewById(R.id.et_term);
        TextView definitionEditText = dialogView.findViewById(R.id.et_definition);

        termEditText.setText(term.getTerm());
        definitionEditText.setText(term.getDefinition());

        builder.setPositiveButton("שמור", (dialog, which) -> {
            String updatedTerm = termEditText.getText().toString().trim();
            String updatedDefinition = definitionEditText.getText().toString().trim();
            fragment.editTerm(term, updatedTerm, updatedDefinition);
        });

        builder.setNegativeButton("ביטול", null);
        builder.show();
    }
}
