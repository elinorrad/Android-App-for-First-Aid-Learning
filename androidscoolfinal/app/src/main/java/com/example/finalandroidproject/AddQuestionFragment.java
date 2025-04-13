package com.example.finalandroidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
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
                            Toast.makeText(getContext(), "שגיאה בבחירת קובץ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_question, container, false);

        //
        Button uploadJsonButton = view.findViewById(R.id.btn_upload_json);
        uploadJsonButton.setOnClickListener(v -> openFilePicker());

        // אתחול ListView
        questionsListView = view.findViewById(R.id.lv_questions);
        questionsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, questionTitles);
        questionsListView.setAdapter(questionsAdapter);

        // אתחול DatabaseReference
        questionsRef = FirebaseDatabase.getInstance().getReference("Questions");

        // אתחול Spinner וניהול נושא וקושי
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

        // אתחול שדות ה-EditText
        EditText questionEditText = view.findViewById(R.id.et_question);
        EditText answer1EditText = view.findViewById(R.id.et_answer1);
        EditText answer2EditText = view.findViewById(R.id.et_answer2);
        EditText answer3EditText = view.findViewById(R.id.et_answer3);
        EditText answer4EditText = view.findViewById(R.id.et_answer4);
        EditText correctAnswerEditText = view.findViewById(R.id.et_correct_answer);

        // אתחול כפתור הוספת השאלה
        View addQuestionButton = view.findViewById(R.id.btn_add_question);
        addQuestionButton.setOnClickListener(v -> {
            String question = questionEditText.getText().toString();
            String answer1 = answer1EditText.getText().toString();
            String answer2 = answer2EditText.getText().toString();
            String answer3 = answer3EditText.getText().toString();
            String answer4 = answer4EditText.getText().toString();
            String correctAnswer = correctAnswerEditText.getText().toString();

            // בדיקה שכל השדות מלאים
            if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer1) || TextUtils.isEmpty(answer2) ||
                    TextUtils.isEmpty(answer3) || TextUtils.isEmpty(answer4) || TextUtils.isEmpty(correctAnswer) ||
                    selectedTopic[0] == null || selectedDifficulty[0] == null) {
                Toast.makeText(getContext(), "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            // יצירת אובייקט Quiz
            Quiz quiz = new Quiz(question, answer1, answer2, answer3, answer4, correctAnswer, selectedTopic[0], selectedDifficulty[0]);

            // שמירת השאלה ב-Firebase
            String questionId = questionsRef.push().getKey();
            if (questionId != null) {
                questionsRef
                        .child(selectedTopic[0])
                        .child(selectedDifficulty[0])
                        .child(questionId)
                        .setValue(quiz)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "השאלה נוספה בהצלחה", Toast.LENGTH_SHORT).show();
                                questionEditText.setText("");
                                answer1EditText.setText("");
                                answer2EditText.setText("");
                                answer3EditText.setText("");
                                answer4EditText.setText("");
                                correctAnswerEditText.setText("");
                            } else {
                                Toast.makeText(getContext(), "הייתה שגיאה בהוספת השאלה", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        questionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear(); // נקה את הרשימה
                questionTitles.clear(); // נקה את הכותרות

                for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                    String topic = topicSnapshot.getKey(); // קבל את הנושא
                    for (DataSnapshot difficultySnapshot : topicSnapshot.getChildren()) {
                        String difficulty = difficultySnapshot.getKey(); // קבל את רמת הקושי
                        for (DataSnapshot questionSnapshot : difficultySnapshot.getChildren()) {
                            Quiz quiz = questionSnapshot.getValue(Quiz.class);
                            if (quiz != null) {
                                quiz.setId(questionSnapshot.getKey()); // שמירת מזהה ייחודי
                                quiz.setTopic(topic); // שמירת הנושא
                                quiz.setDifficulty(difficulty); // שמירת רמת הקושי

                                quizList.add(quiz); // הוסף לרשימת השאלות
                                questionTitles.add(quiz.getQuestion()); // הוסף כותרת לרשימה
                            }
                        }
                    }
                }

                // עדכון הממשק הגרפי
                questionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת השאלות", Toast.LENGTH_SHORT).show();
            }
        });


        // טיפול בלחיצה על פריט ברשימה
        questionsListView.setOnItemClickListener((parent, view1, position, id) -> {
            Quiz selectedQuiz = quizList.get(position);
            onQuestionClicked(selectedQuiz);
        });

        return view;
    }


    private void onQuestionClicked(Quiz quiz) {
        // הצגת תפריט עריכה/מחיקה
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("בחר פעולה");
        builder.setItems(new String[]{"עריכה", "מחיקה"}, (dialog, which) -> {
            if (which == 0) {
                editQuestion(quiz);
            } else if (which == 1) {
                deleteQuestion(quiz);
            }
        });
        builder.show();
    }

    private void editQuestion(Quiz quiz) {
        // פתיחת דיאלוג עריכה
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
                .setTitle("ערוך שאלה")
                .setView(editView)
                .setPositiveButton("שמור", (dialog, which) -> {
                    quiz.setQuestion(questionEditText.getText().toString().trim());
                    quiz.setAnswer1(answer1EditText.getText().toString().trim());
                    quiz.setAnswer2(answer2EditText.getText().toString().trim());
                    quiz.setAnswer3(answer3EditText.getText().toString().trim());
                    quiz.setAnswer4(answer4EditText.getText().toString().trim());
                    quiz.setCorrectAnswer(correctAnswerEditText.getText().toString().trim());

                    // נתיב מלא ב-Firebase
                    questionsRef.child(quiz.getTopic())
                            .child(quiz.getDifficulty())
                            .child(quiz.getId())
                            .setValue(quiz)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "השאלה עודכנה בהצלחה", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "שגיאה בעדכון השאלה", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    private void deleteQuestion(Quiz quiz) {
        // נתיב מלא ב-Firebase
        questionsRef.child(quiz.getTopic())
                .child(quiz.getDifficulty())
                .child(quiz.getId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "השאלה נמחקה בהצלחה", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "שגיאה במחיקת השאלה", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "בחר קובץ JSON"));
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "בחר קובץ JSON"));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = FileUtils.getPath(getContext(), uri);
                if (!TextUtils.isEmpty(filePath)) {
                    startJsonUploadService(filePath);
                } else {
                    Toast.makeText(getContext(), "שגיאה בבחירת קובץ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startJsonUploadService(String filePath) {
        Intent serviceIntent = new Intent(getContext(), JsonUploadService.class);
        serviceIntent.putExtra("filePath", filePath);
        getContext().startService(serviceIntent);
        Toast.makeText(getContext(), "טעינת JSON מתבצעת...", Toast.LENGTH_SHORT).show();
    }
}
