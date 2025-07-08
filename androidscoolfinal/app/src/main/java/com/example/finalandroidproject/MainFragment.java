package com.example.finalandroidproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * MainFragment is the default fragment loaded in the app.
 * Typically used for displaying a home screen or welcome interface.
 */
public class MainFragment extends Fragment {

    /**
     * Inflates the layout for the main screen.
     *
     * @param inflater Used to inflate the XML layout
     * @param container Optional parent view
     * @param savedInstanceState Previously saved state, if any
     * @return The root view of this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
