package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment that displays test instructions and a button to start the test.
 * Once the user clicks the button, it navigates to the TestFragment.
 */
public class InstructionsTestFragment extends Fragment {

    /**
     * Inflates the layout and sets up the "Start Test" button.
     *
     * @param inflater LayoutInflater to inflate the view
     * @param container Optional parent view
     * @param savedInstanceState Previously saved instance state
     * @return the view for the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructions_test, container, false);

        Button startTestButton = view.findViewById(R.id.btn_start_test);
        startTestButton.setOnClickListener(v -> {
            // Navigate to the test fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TestFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
