package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SetupActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (savedInstanceState == null) {
            // Get response code indicating whether or not the user has existing progress
            int responseCode = getIntent().getIntExtra("io.github.akz08.cyoaclient.response_code", 0);
            if (responseCode == 200) {
                // Notify user of options regarding existing progress
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new ReturningUserFragment())
                    .commit();
            }
            else if (responseCode == 201) {
                // Setup a fresh database with tutorial to follow
                DatabaseSetupFragment databaseSetup = new DatabaseSetupFragment();
                Bundle setupArgs = new Bundle();
                setupArgs.putBoolean("io.github.akz08.cyoaclient.continue_progress", true);
                setupArgs.putBoolean("io.github.akz08.cyoaclient.redo_tutorial", true);
                databaseSetup.setArguments(setupArgs);
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, databaseSetup)
                    .commit();
            }
        }
    }

}