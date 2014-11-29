package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class ReturningUserFragment extends Fragment {

    public ReturningUserFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                // Launch a new fragment to setup the database with the corresponding options
                DatabaseSetupFragment databaseSetup = new DatabaseSetupFragment();
                Bundle setupArgs = new Bundle();
                setupArgs.putBoolean("io.github.akz08.cyoaclient.continue_progress", continueProgress);
                setupArgs.putBoolean("io.github.akz08.cyoaclient.redo_tutorial", redoTutorial);
                databaseSetup.setArguments(setupArgs);
                getFragmentManager().beginTransaction()
                    .replace(R.id.activity_setup_container, databaseSetup)
                    .commit();
            }
        });
        return view;
    }

}