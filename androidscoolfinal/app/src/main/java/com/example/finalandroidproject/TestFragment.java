package com.example.finalandroidproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;

/**
 * Fragment that allows the user to choose a test difficulty level
 * and loads relevant questions from Firebase Realtime Database.
 */
public class TestFragment extends Fragment {

    private static final String TAG = "TestFragment";

    /**
     * Inflates the view and sets click listeners for difficulty buttons.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        Button easyButton = view.findViewById(R.id.btn_mar);
        Button mediumButton = view.findViewById(R.id.btn_senior_medic);
        Button hardButton = view.findViewById(R.id.btn_paramedic);

        easyButton.setOnClickListener(v -> fetchQuestionsAndNavigate("קל"));
        mediumButton.setOnClickListener(v -> fetchQuestionsAndNavigate("בינוני"));
        hardButton.setOnClickListener(v -> fetchQuestionsAndNavigate("קשה"));

        return view;
    }

    /**
     * Loads questions from Firebase based on the selected difficulty level,
     * shuffles and selects up to 10 questions, then navigates to the test display.
     *
     * @param level The difficulty level selected by the user ("קל", "בינוני", "קשה")
     */
    private void fetchQuestionsAndNavigate(String level) {
        Toast.makeText(getContext(), "Selected level: " + level, Toast.LENGTH_SHORT).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Questions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Quiz> questions = new ArrayList<>();

                for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot levelSnapshot = topicSnapshot.child(level);

                    for (DataSnapshot questionSnapshot : levelSnapshot.getChildren()) {
                        Quiz quiz = questionSnapshot.getValue(Quiz.class);
                        if (quiz != null && quiz.getQuestion() != null) {
                            questions.add(quiz);
                        } else {
                            Log.w(TAG, "Invalid question loaded: " + questionSnapshot.getKey());
                        }
                    }
                }

                Log.d(TAG, "Total questions found: " + questions.size());

                if (!questions.isEmpty()) {
                    Collections.shuffle(questions);
                    ArrayList<Quiz> selectedQuestions = new ArrayList<>(questions.subList(0, Math.min(10, questions.size())));

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("questions", selectedQuestions);
                    bundle.putLong("timeLimit", 15 * 60 * 1000); // 15 minutes

                    TestDisplayFragment testFragment = new TestDisplayFragment();
                    testFragment.setArguments(bundle);

                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, testFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "No questions found for level: " + level, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase access error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
