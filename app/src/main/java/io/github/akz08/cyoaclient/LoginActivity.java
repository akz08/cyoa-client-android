package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class LoginActivity extends FragmentActivity {

    private LoginFragment loginFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();
        }
        else {
            // Or set the fragment from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
    }

}