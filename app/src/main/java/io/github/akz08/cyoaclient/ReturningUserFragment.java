package io.github.akz08.cyoaclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class ReturningUserFragment extends Fragment {

    private OnReturningUserFinishListener mCallback;

    public interface OnReturningUserFinishListener {
        public void onReturningUserFinish(boolean continueProgress, boolean redoTutorial);
    }

    public static final ReturningUserFragment newInstance() {
        ReturningUserFragment fragment = new ReturningUserFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            mCallback = (OnReturningUserFinishListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnReturningUserFinishListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_returning_user, container, false);
        // Handle the button click event
        Button button = (Button) getView().findViewById(R.id.returning_user_continue_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check whether the user wants to continue progress
                CheckBox continueProgressBox = (CheckBox) getView().findViewById(R.id.returning_user_continue_progress_checkbox);
                boolean continueProgress = continueProgressBox.isChecked();
                // Check whether the user wants to redo the tutorial
                CheckBox redoTutorialBox = (CheckBox) getView().findViewById(R.id.returning_user_redo_tutorial_checkbox);
                boolean redoTutorial = redoTutorialBox.isChecked();
                // Notify the parent activity of the options selected, and to take further action
                mCallback.onReturningUserFinish(continueProgress, redoTutorial);
            }
        });
        return view;
    }

}