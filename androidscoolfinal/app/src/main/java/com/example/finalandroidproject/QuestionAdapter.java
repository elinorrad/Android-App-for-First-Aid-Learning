package com.example.finalandroidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for displaying a list of Quiz questions.
 * Allows click events via the OnQuestionClickListener interface.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final List<Quiz> quizList;
    private final OnQuestionClickListener listener;

    /**
     * Interface for handling question item click events.
     */
    public interface OnQuestionClickListener {
        void onQuestionClick(Quiz quiz);
    }

    /**
     * Constructs the adapter with a list of Quiz objects and a click listener.
     *
     * @param quizList The list of questions to display
     * @param listener The listener to handle item clicks
     */
    public QuestionAdapter(List<Quiz> quizList, OnQuestionClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }

    /**
     * Inflates the item view layout for a single question.
     */
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    /**
     * Binds a Quiz object to the corresponding view holder.
     *
     * @param holder The view holder
     * @param position The position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.questionTextView.setText(quiz.getQuestion());
        holder.itemView.setOnClickListener(v -> listener.onQuestionClick(quiz));
    }

    /**
     * Returns the number of questions in the list.
     */
    @Override
    public int getItemCount() {
        return quizList.size();
    }

    /**
     * ViewHolder class for individual question items.
     */
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.tv_question);
        }
    }
}
