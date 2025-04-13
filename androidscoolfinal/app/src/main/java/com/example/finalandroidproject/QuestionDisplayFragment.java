package com.example.finalandroidproject;

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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionDisplayFragment extends Fragment {

    private TextView timerText, questionText;
    private Button answer1Button, answer2Button, answer3Button, answer4Button, nextQuestionButton;

    private ArrayList<Quiz> questionsList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 15 * 60 * 1000; // 15 דקות במילישניות

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_display, container, false);

        // חיבור ל-IDs
        timerText = view.findViewById(R.id.timer_text);
        questionText = view.findViewById(R.id.question_text);
        answer1Button = view.findViewById(R.id.btn_answer_1);
        answer2Button = view.findViewById(R.id.btn_answer_2);
        answer3Button = view.findViewById(R.id.btn_answer_3);
        answer4Button = view.findViewById(R.id.btn_answer_4);
        nextQuestionButton = view.findViewById(R.id.btn_next_question);

        // קבלת שאלות מה-Bundle
        questionsList = (ArrayList<Quiz>) getArguments().getSerializable("questions");
        if (questionsList == null || questionsList.isEmpty()) {
            Toast.makeText(getContext(), "לא נמצאו שאלות להצגה", Toast.LENGTH_SHORT).show();
            return view;
        }

        // טיימר
        startTimer();

        // הצגת השאלה הראשונה
        displayQuestion();

        // טיפול בכפתורי התשובות
        View.OnClickListener answerClickListener = v -> {
            Button clickedButton = (Button) v;
            checkAnswer(clickedButton.getText().toString());
        };

        answer1Button.setOnClickListener(answerClickListener);
        answer2Button.setOnClickListener(answerClickListener);
        answer3Button.setOnClickListener(answerClickListener);
        answer4Button.setOnClickListener(answerClickListener);

        // כפתור לשאלה הבאה
        nextQuestionButton.setOnClickListener(v -> displayNextQuestion());

        return view;
    }

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
                Toast.makeText(getContext(), "הזמן נגמר!", Toast.LENGTH_SHORT).show();
                endQuiz();
            }
        }.start();
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            Quiz currentQuestion = questionsList.get(currentQuestionIndex);
            questionText.setText(currentQuestion.getQuestion());
            answer1Button.setText(currentQuestion.getAnswer1());
            answer2Button.setText(currentQuestion.getAnswer2());
            answer3Button.setText(currentQuestion.getAnswer3());
            answer4Button.setText(currentQuestion.getAnswer4());
        } else {
            endQuiz();
        }
    }

    private void checkAnswer(String selectedAnswer) {
        Quiz currentQuestion = questionsList.get(currentQuestionIndex);
        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
        }
        nextQuestionButton.setVisibility(View.VISIBLE); // הצגת כפתור הבא
    }

    private void displayNextQuestion() {
        currentQuestionIndex++;
        nextQuestionButton.setVisibility(View.GONE); // הסתרת כפתור הבא
        displayQuestion();
    }

    private void endQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Toast.makeText(getContext(), "המבחן הסתיים! התוצאה שלך: " + score + " מתוך " + questionsList.size(), Toast.LENGTH_LONG).show();

        // ניתן להוסיף ניווט למסך תוצאות
    }
}
