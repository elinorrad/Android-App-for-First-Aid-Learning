package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InstructionsTestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructions_test, container, false);

        // הגדרת כפתור התחלת המבחן
        Button startTestButton = view.findViewById(R.id.btn_start_test);
        startTestButton.setOnClickListener(v -> {
            // מעבר ל-Fragment של מבחן (ליצור מאוחר יותר)
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TestFragment()) // זה יכול להיות TestFragment מותאם
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
