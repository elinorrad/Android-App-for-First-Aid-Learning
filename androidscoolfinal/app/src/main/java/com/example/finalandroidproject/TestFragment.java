package com.example.finalandroidproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class TestFragment extends Fragment {

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        // כפתור מע"ר
        Button btnMar = view.findViewById(R.id.btn_mar);
        btnMar.setOnClickListener(v -> loadQuestions());

        return view;
    }

    private void loadQuestions() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Questions");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Quiz> questionList = new ArrayList<>();

                for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot marSnapshot = topicSnapshot.child("מער");
                    for (DataSnapshot questionSnapshot : marSnapshot.getChildren()) {
                        Quiz question = questionSnapshot.getValue(Quiz.class);
                        if (question != null) {
                            questionList.add(question);
                        }
                    }
                }

                // ערבוב ובחירת 10 שאלות
                Collections.shuffle(questionList);
                ArrayList<Quiz> randomQuestions = new ArrayList<>(questionList.subList(0, Math.min(10, questionList.size())));

                // העברת הנתונים לפרגמנט התצוגה
                Bundle bundle = new Bundle();
                bundle.putSerializable("questions", randomQuestions);
                bundle.putLong("timeLimit", 15 * 60 * 1000); // 15 דקות במילישניות

                TestDisplayFragment testDisplayFragment = new TestDisplayFragment();
                testDisplayFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, testDisplayFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TestFragment", "Failed to load questions: " + databaseError.getMessage());
            }
        });
    }
}
