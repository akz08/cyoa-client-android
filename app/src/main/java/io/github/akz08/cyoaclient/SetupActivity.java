package io.github.akz08.cyoaclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SetupActivity extends FragmentActivity {

    private ReturningUserFragment returningUser;
    private DatabaseSetupFragment databaseSetup;
    private TutorialFragment tutorial;

    private boolean redoTutorial = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (savedInstanceState == null) {
            // Get response code indicating whether or not the user has existing progress
            int responseCode = getIntent().getIntExtra("io.github.akz08.cyoaclient.response_code", 0);
            if (responseCode == 200) {
                // Notify user of options regarding existing progress
                returningUser = ReturningUserFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, returningUser)
                    .commit();
            }
            else if (responseCode == 201) {
                // Setup a fresh database with tutorial to follow
                databaseSetup = DatabaseSetupFragment.newInstance(true);
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_setup_container, databaseSetup)
                    .commit();
            }
        }
    }

    public void onReturningUserFinish(boolean continueProgress, boolean redoTutorial) {
        this.redoTutorial = redoTutorial;
        DatabaseSetupFragment databaseSetup = DatabaseSetupFragment.newInstance(continueProgress);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.activity_setup_container, databaseSetup)
            .commit();
    }

    public void onDatabaseSetupFinish() {
        // Set the shared preferences to indicate that the application has been run
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("is_first_run", false).apply();
        // Handle next transition
        if (redoTutorial) {
            // Launch the tutorial fragment
            tutorial = TutorialFragment.newInstance();
            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            fm.replace(R.id.activity_setup_container, tutorial);
            fm.commit();
        }
        else {
            // Go to the main activity of the application
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void onTutorialFinish() {
        // Go to the main activity of the application
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}