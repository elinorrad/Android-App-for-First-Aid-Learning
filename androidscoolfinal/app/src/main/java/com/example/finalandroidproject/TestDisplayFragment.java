package com.example.finalandroidproject;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment that runs a test (quiz) for the user.
 * Displays one question at a time with a countdown timer, tracks score, and saves the result to Firebase.
 */
public class TestDisplayFragment extends Fragment {

    private TextView timerText, questionText;
    private Button answer1Button, answer2Button, answer3Button, answer4Button, nextQuestionButton;
    private Button selectedButton;
    private int defaultColor;

    private ArrayList<Quiz> questionsList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 15 * 60 * 1000; // 15 minutes in milliseconds

    /**
     * Initializes the UI, retrieves questions, and starts the timer.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_display, container, false);

        timerText = view.findViewById(R.id.timer_text);
        questionText = view.findViewById(R.id.question_text);
        answer1Button = view.findViewById(R.id.btn_answer_1);
        answer2Button = view.findViewById(R.id.btn_answer_2);
        answer3Button = view.findViewById(R.id.btn_answer_3);
        answer4Button = view.findViewById(R.id.btn_answer_4);
        nextQuestionButton = view.findViewById(R.id.btn_next_question);

        ColorStateList colorStateList = answer1Button.getBackgroundTintList();
        if (colorStateList != null) {
            defaultColor = colorStateList.getDefaultColor();
        }

        questionsList = (ArrayList<Quiz>) getArguments().getSerializable("questions");
        if (questionsList == null || questionsList.isEmpty()) {
            Toast.makeText(getContext(), "No questions found", Toast.LENGTH_SHORT).show();
            return view;
        }

        startTimer();
        displayQuestion();

        View.OnClickListener answerClickListener = v -> {
            Button clickedButton = (Button) v;
            highlightSelectedButton(clickedButton);
            checkAnswer(clickedButton.getText().toString());
        };

        answer1Button.setOnClickListener(answerClickListener);
        answer2Button.setOnClickListener(answerClickListener);
        answer3Button.setOnClickListener(answerClickListener);
        answer4Button.setOnClickListener(answerClickListener);

        nextQuestionButton.setOnClickListener(v -> displayNextQuestion());

        return view;
    }

    /**
     * Starts a 15-minute countdown timer and updates the UI every second.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getContext(), "Time's up!", Toast.LENGTH_SHORT).show();
                endQuiz();
            }
        }.start();
    }

    /**
     * Displays the current question and answers.
     */
    private void displayQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            Quiz currentQuestion = questionsList.get(currentQuestionIndex);
            questionText.setText(currentQuestion.getQuestion());
            answer1Button.setText(currentQuestion.getAnswer1());
            answer2Button.setText(currentQuestion.getAnswer2());
            answer3Button.setText(currentQuestion.getAnswer3());
            answer4Button.setText(currentQuestion.getAnswer4());
            resetButtonStyle();
        } else {
            endQuiz();
        }
    }

    /**
     * Validates if the selected answer is correct and updates score accordingly.
     *
     * @param selectedAnswer The answer text selected by the user
     */
    private void checkAnswer(String selectedAnswer) {
        Quiz currentQuestion = questionsList.get(currentQuestionIndex);
        String[] answers = {
                currentQuestion.getAnswer1(),
                currentQuestion.getAnswer2(),
                currentQuestion.getAnswer3(),
                currentQuestion.getAnswer4()
        };
        String correctAnswer = answers[Integer.parseInt(currentQuestion.getCorrectAnswer()) - 1];

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
        }
        nextQuestionButton.setVisibility(View.VISIBLE);
    }

    /**
     * Highlights the selected answer button and resets others.
     */
    private void highlightSelectedButton(Button clickedButton) {
        resetButtonStyle();
        int lightBlueColor = ContextCompat.getColor(getContext(), R.color.light_blue);
        clickedButton.setBackgroundTintList(ColorStateList.valueOf(lightBlueColor));
        selectedButton = clickedButton;
    }

    /**
     * Resets all answer buttons to their default color.
     */
    private void resetButtonStyle() {
        answer1Button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        answer2Button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        answer3Button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        answer4Button.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
        selectedButton = null;
    }

    /**
     * Advances to the next question.
     */
    private void displayNextQuestion() {
        currentQuestionIndex++;
        nextQuestionButton.setVisibility(View.GONE);
        displayQuestion();
    }

    /**
     * Ends the test, shows the score, and saves it to Firebase under the user's test history.
     */
    private void endQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int percentage = (int) (((double) score / questionsList.size()) * 100);
        Toast.makeText(getContext(), "Test finished! Your score: " + score + " out of " + questionsList.size(), Toast.LENGTH_LONG).show();

        saveTestResultToFirebase(percentage);
    }

    /**
     * Saves the user's test result to Firebase Realtime Database with timestamp as the key.
     *
     * @param percentage The final score as a percentage
     */
    private void saveTestResultToFirebase(int percentage) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Error: no authenticated user found", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("testResults");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        userRef.child(currentDate).setValue(percentage)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Test result saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving result: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
