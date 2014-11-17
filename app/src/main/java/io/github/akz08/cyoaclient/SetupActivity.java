package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SetupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (savedInstanceState == null) {
            // Get response code indicating whether or not the user has existing progress
            String responseCode = getIntent().getStringExtra("io.github.akz08.cyoaclient.response_code");
            if (responseCode.equals("201")) {
                // Notify user of options regarding existing progress
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new UserHistoryFragment())
                    .commit();
            }
            else if (responseCode.equals("200")) {
                // Setup a fresh database
                /*
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new DatabaseSetupFragment())
                    .commit();
                */
            }
            else {
                // Handle error
            }
        }
    }

}