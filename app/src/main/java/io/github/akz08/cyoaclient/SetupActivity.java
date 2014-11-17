package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SetupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (savedInstanceState == null) {
            // Get boolean flag indicating whether or not the user has existing progress
            Boolean isAuth = getIntent().getBooleanExtra("io.github.akz08.cyoaclient.is_auth", false);
            if (isAuth) {
                // Notify user of options regarding existing progress
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new UserHistoryFragment())
                    .commit();
            }
            else {
                // Setup a fresh database
                /*
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new DatabaseSetupFragment())
                    .commit();
                */
            }
        }
    }

}