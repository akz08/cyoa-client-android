package io.github.akz08.cyoaclient.controllers.setup;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import io.github.akz08.cyoaclient.R;

public class ExistingUserFragment extends Fragment {

    private final String LOG_TAG = getClass().getSimpleName();
    private OnExistingUserFragmentInteractionListener mListener;

    public interface OnExistingUserFragmentInteractionListener {
        void onExistingUserFragmentInteraction(boolean resumeProgress);
    }

    public static ExistingUserFragment newInstance() { return new ExistingUserFragment(); }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnExistingUserFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnExistingUserFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_existing_user, container, false);
        // Handle the continue button click event
        Button button = (Button) view.findViewById(R.id.existing_user_continue_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check whether the user wants to continue progress
                CheckBox resumeProgressBox = (CheckBox) getView()
                    .findViewById(R.id.existing_user_resume_progress_checkbox);
                boolean resumeProgress = resumeProgressBox.isChecked();
                // Notify the parent activity of the options selected, and to take further action
                onContinueButtonPressed(resumeProgress);
            }
        });
        return view;
    }

    private void onContinueButtonPressed(boolean resumeProgress) {
        if (mListener != null) {
            mListener.onExistingUserFragmentInteraction(resumeProgress);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}