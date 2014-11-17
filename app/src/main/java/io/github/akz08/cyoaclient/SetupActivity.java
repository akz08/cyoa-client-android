package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class SetupActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_setup_container, new UserHistoryFragment())
                .commit();
        }
    }

}