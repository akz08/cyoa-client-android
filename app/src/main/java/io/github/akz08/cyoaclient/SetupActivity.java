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
            String responseCode = getIntent().getStringExtra("io.github.akz08.cyoaclient.response_code");
            if (responseCode.equals("200")) {
                /*
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean("is_first_run", false).apply();
                */
                // Notify user of options regarding existing progress
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, new UserHistoryFragment())
                    .commit();
            }
            else if (responseCode.equals("201")) {
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