package com.example.finalandroidproject;

import java.io.Serializable;

/**
 * Model class representing a single quiz question.
 * Includes the question text, multiple answers, the correct answer,
 * topic, and difficulty level. Implements Serializable for passing via Bundles.
 */
public class Quiz implements Serializable {

    private String id; // Unique identifier (used in Firebase)
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String correctAnswer;
    private String topic;
    private String difficulty;

    /**
     * Empty constructor required for Firebase.
     */
    public Quiz() {
    }

    /**
     * Constructor without ID, used when creating a new quiz.
     *
     * @param question       The question text
     * @param answer1        First answer option
     * @param answer2        Second answer option
     * @param answer3        Third answer option
     * @param answer4        Fourth answer option
     * @param correctAnswer  The correct answer
     * @param topic          The topic of the question
     * @param difficulty     The difficulty level
     */
    public Quiz(String question, String answer1, String answer2, String answer3,
                String answer4, String correctAnswer, String topic, String difficulty) {
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.topic = topic;
        this.difficulty = difficulty;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Returns a string representation of the Quiz object (for debugging/logging).
     */
    @Override
    public String toString() {
        return "Quiz{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
