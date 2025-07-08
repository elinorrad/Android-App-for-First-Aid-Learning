package com.example.finalandroidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for adding, displaying, editing, and deleting quiz questions.
 * Questions can also be uploaded from a JSON file.
 */
public class AddQuestionFragment extends Fragment {

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    private DatabaseReference questionsRef;
    private ListView questionsListView;
    private ArrayAdapter<String> questionsAdapter;
    private List<Quiz> quizList = new ArrayList<>();
    private List<String> questionTitles = new ArrayList<>();

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        String filePath = FileUtils.getPath(getContext(), uri);
                        if (!TextUtils.isEmpty(filePath)) {
                            startJsonUploadService(filePath);
                        } else {
                            Toast.makeText(getContext(), "Error selecting file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    /**
     * Initializes the fragment view, sets up Firebase, UI listeners and question handling.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_question, container, false);

        Button uploadJsonButton = view.findViewById(R.id.btn_upload_json);
        uploadJsonButton.setOnClickListener(v -> openFilePicker());

        questionsListView = view.findViewById(R.id.lv_questions);
        questionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, questionTitles);
        questionsListView.setAdapter(questionsAdapter);

        questionsRef = FirebaseDatabase.getInstance().getReference("Questions");

        Spinner topicSpinner = view.findViewById(R.id.spinner_topic);
        Spinner difficultySpinner = view.findViewById(R.id.spinner_difficulty);

        final String[] selectedTopic = {null};
        final String[] selectedDifficulty = {null};

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTopic[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTopic[0] = null;
            }
        });

        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDifficulty[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDifficulty[0] = null;
            }
        });

        EditText questionEditText = view.findViewById(R.id.et_question);
        EditText answer1EditText = view.findViewById(R.id.et_answer1);
        EditText answer2EditText = view.findViewById(R.id.et_answer2);
        EditText answer3EditText = view.findViewById(R.id.et_answer3);
        EditText answer4EditText = view.findViewById(R.id.et_answer4);
        EditText correctAnswerEditText = view.findViewById(R.id.et_correct_answer);

        View addQuestionButton = view.findViewById(R.id.btn_add_question);
        addQuestionButton.setOnClickListener(v -> {
            String question = questionEditText.getText().toString();
            String answer1 = answer1EditText.getText().toString();
            String answer2 = answer2EditText.getText().toString();
            String answer3 = answer3EditText.getText().toString();
            String answer4 = answer4EditText.getText().toString();
            String correctAnswer = correctAnswerEditText.getText().toString();

            if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer1) || TextUtils.isEmpty(answer2) ||
                    TextUtils.isEmpty(answer3) || TextUtils.isEmpty(answer4) || TextUtils.isEmpty(correctAnswer) ||
                    selectedTopic[0] == null || selectedDifficulty[0] == null) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Quiz quiz = new Quiz(question, answer1, answer2, answer3, answer4, correctAnswer, selectedTopic[0], selectedDifficulty[0]);

            String questionId = questionsRef.push().getKey();
            if (questionId != null) {
                questionsRef
                        .child(selectedTopic[0])
                        .child(selectedDifficulty[0])
                        .child(questionId)
                        .setValue(quiz)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Question added successfully", Toast.LENGTH_SHORT).show();
                                questionEditText.setText("");
                                answer1EditText.setText("");
                                answer2EditText.setText("");
                                answer3EditText.setText("");
                                answer4EditText.setText("");
                                correctAnswerEditText.setText("");
                            } else {
                                Toast.makeText(getContext(), "Error adding question", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        questionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();
                questionTitles.clear();

                for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                    String topic = topicSnapshot.getKey();
                    for (DataSnapshot difficultySnapshot : topicSnapshot.getChildren()) {
                        String difficulty = difficultySnapshot.getKey();
                        for (DataSnapshot questionSnapshot : difficultySnapshot.getChildren()) {
                            Quiz quiz = questionSnapshot.getValue(Quiz.class);
                            if (quiz != null) {
                                quiz.setId(questionSnapshot.getKey());
                                quiz.setTopic(topic);
                                quiz.setDifficulty(difficulty);

                                quizList.add(quiz);
                                questionTitles.add(quiz.getQuestion());
                            }
                        }
                    }
                }
                questionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading questions", Toast.LENGTH_SHORT).show();
            }
        });

        questionsListView.setOnItemClickListener((parent, view1, position, id) -> {
            Quiz selectedQuiz = quizList.get(position);
            onQuestionClicked(selectedQuiz);
        });

        return view;
    }

    /**
     * Shows a dialog allowing the user to edit or delete the selected quiz question.
     *
     * INPUT: quiz - the selected quiz question
     * OUTPUT: Shows dialog with actions
     */
    private void onQuestionClicked(Quiz quiz) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Action");
        builder.setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
            if (which == 0) {
                editQuestion(quiz);
            } else if (which == 1) {
                deleteQuestion(quiz);
            }
        });
        builder.show();
    }

    /**
     * Opens a dialog that allows the user to edit a selected quiz question.
     *
     * INPUT: quiz - the quiz to edit
     * OUTPUT: Updates Firebase on save
     */
    private void editQuestion(Quiz quiz) {
        View editView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_question, null);
        EditText questionEditText = editView.findViewById(R.id.et_question);
        EditText answer1EditText = editView.findViewById(R.id.et_answer1);
        EditText answer2EditText = editView.findViewById(R.id.et_answer2);
        EditText answer3EditText = editView.findViewById(R.id.et_answer3);
        EditText answer4EditText = editView.findViewById(R.id.et_answer4);
        EditText correctAnswerEditText = editView.findViewById(R.id.et_correct_answer);

        questionEditText.setText(quiz.getQuestion());
        answer1EditText.setText(quiz.getAnswer1());
        answer2EditText.setText(quiz.getAnswer2());
        answer3EditText.setText(quiz.getAnswer3());
        answer4EditText.setText(quiz.getAnswer4());
        correctAnswerEditText.setText(quiz.getCorrectAnswer());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Question")
                .setView(editView)
                .setPositiveButton("Save", (dialog, which) -> {
                    quiz.setQuestion(questionEditText.getText().toString().trim());
                    quiz.setAnswer1(answer1EditText.getText().toString().trim());
                    quiz.setAnswer2(answer2EditText.getText().toString().trim());
                    quiz.setAnswer3(answer3EditText.getText().toString().trim());
                    quiz.setAnswer4(answer4EditText.getText().toString().trim());
                    quiz.setCorrectAnswer(correctAnswerEditText.getText().toString().trim());

                    questionsRef.child(quiz.getTopic())
                            .child(quiz.getDifficulty())
                            .child(quiz.getId())
                            .setValue(quiz)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Question updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error updating question", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the selected quiz question from Firebase.
     *
     * INPUT: quiz - the quiz to delete
     * OUTPUT: Firebase entry is removed
     */
    private void deleteQuestion(Quiz quiz) {
        questionsRef.child(quiz.getTopic())
                .child(quiz.getDifficulty())
                .child(quiz.getId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Question deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error deleting question", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Opens file picker to allow user to select a JSON file.
     *
     * INPUT: None
     * OUTPUT: Launches file picker
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select JSON File"));
    }

    /**
     * Starts the background service to upload questions from a JSON file.
     *
     * INPUT: filePath - path of the selected JSON file
     * OUTPUT: Starts upload service
     */
    private void startJsonUploadService(String filePath) {
        Intent serviceIntent = new Intent(getContext(), JsonUploadService.class);
        serviceIntent.putExtra("filePath", filePath);
        getContext().startService(serviceIntent);
        Toast.makeText(getContext(), "Uploading JSON...", Toast.LENGTH_SHORT).show();
    }
}
