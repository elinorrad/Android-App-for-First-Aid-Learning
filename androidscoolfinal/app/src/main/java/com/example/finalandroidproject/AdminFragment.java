package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * AdminFragment is the main dashboard for administrators.
 * It allows navigation to the fragments for managing:
 * - Quiz questions
 * - Glossary terms
 * - Educational videos
 * - (Placeholder for summaries)
 */
public class AdminFragment extends Fragment {

    /**
     * Inflates the admin dashboard layout, sets up buttons and their listeners.
     *
     * @param inflater LayoutInflater to inflate the layout
     * @param container Optional parent view
     * @param savedInstanceState Previous saved state if available
     * @return The view representing the admin dashboard
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        Button btnQuestions = view.findViewById(R.id.btn_questions);
        Button btnConcepts = view.findViewById(R.id.btn_concepts);
        Button btnSummaries = view.findViewById(R.id.btn_summaries);
        Button btnVideos = view.findViewById(R.id.btn_videos);

        btnQuestions.setOnClickListener(v -> navigateToFragment(new AddQuestionFragment()));
        btnConcepts.setOnClickListener(v -> navigateToFragment(new AddGlossaryFragment()));
        btnSummaries.setOnClickListener(v ->
                Toast.makeText(getContext(), "This button is not yet active", Toast.LENGTH_SHORT).show()
        );
        btnVideos.setOnClickListener(v -> navigateToFragment(new AddVideoFragment()));

        return view;
    }

    /**
     * Replaces the current fragment with the specified one.
     *
     * INPUT: fragment - the new fragment to navigate to
     * OUTPUT: Displays the selected fragment on screen
     */
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
