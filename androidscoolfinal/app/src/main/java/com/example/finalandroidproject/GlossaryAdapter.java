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

/**
 * Adapter class for displaying glossary terms in a ListView.
 * Allows user to view, edit, or delete terms via dialogs.
 */
public class GlossaryAdapter extends ArrayAdapter<Term> {

    private final AddGlossaryFragment fragment;

    /**
     * Constructs a new GlossaryAdapter.
     *
     * INPUT:
     * - context: the context in which the adapter is running
     * - objects: the list of Term objects to display
     * - fragment: reference to the fragment for calling edit/delete actions
     */
    public GlossaryAdapter(@NonNull Context context, @NonNull ArrayList<Term> objects, AddGlossaryFragment fragment) {
        super(context, 0, objects);
        this.fragment = fragment;
    }

    /**
     * Populates the ListView with term data and sets click listeners for each term.
     *
     * INPUT:
     * - position: position of the item in the list
     * - convertView: recycled view
     * - parent: parent ViewGroup
     *
     * OUTPUT:
     * - Returns the populated view for the current row
     */
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

    /**
     * Displays a dialog with options to edit or delete a term.
     *
     * INPUT:
     * - term: the selected term
     * OUTPUT:
     * - Shows a dialog with action buttons
     */
    private void showOptionsDialog(Term term) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(term);
                    } else if (which == 1) {
                        fragment.deleteTerm(term);
                    }
                })
                .show();
    }

    /**
     * Displays a dialog allowing the user to edit a term.
     * On save, calls the fragment's editTerm() method.
     *
     * INPUT:
     * - term: the term to edit
     * OUTPUT:
     * - Shows editable dialog and applies changes
     */
    private void showEditDialog(Term term) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_term, null);
        builder.setView(dialogView);

        TextView termEditText = dialogView.findViewById(R.id.et_term);
        TextView definitionEditText = dialogView.findViewById(R.id.et_definition);

        termEditText.setText(term.getTerm());
        definitionEditText.setText(term.getDefinition());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedTerm = termEditText.getText().toString().trim();
            String updatedDefinition = definitionEditText.getText().toString().trim();
            fragment.editTerm(term, updatedTerm, updatedDefinition);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
