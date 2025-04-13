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

public class AdminFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // איתור כפתורים
        Button btnQuestions = view.findViewById(R.id.btn_questions);
        Button btnConcepts = view.findViewById(R.id.btn_concepts);
        Button btnSummaries = view.findViewById(R.id.btn_summaries);
        Button btnVideos = view.findViewById(R.id.btn_videos); // הוספת כפתור הסרטונים

        // מאזין לכפתור השאלות
        btnQuestions.setOnClickListener(v -> navigateToFragment(new AddQuestionFragment()));

        // מאזין לכפתור המושגים
        btnConcepts.setOnClickListener(v -> navigateToFragment(new AddGlossaryFragment()));

        // מאזין לכפתור הסיכומים
        btnSummaries.setOnClickListener(v ->
                Toast.makeText(getContext(), "כפתור זה עדיין אינו פעיל", Toast.LENGTH_SHORT).show()
        );

        // מאזין לכפתור הסרטונים
        btnVideos.setOnClickListener(v -> navigateToFragment(new AddVideoFragment()));

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
