package io.github.akz08.cyoaclient.controllers.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import io.github.akz08.cyoaclient.R;
import io.github.akz08.cyoaclient.controllers.main.MainActivity;
import io.github.akz08.cyoaclient.controllers.setup.SetupActivity;

public class LoginActivity extends Activity {

    private final String LOG_TAG = getClass().getSimpleName();
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        if (isLoggedIn()) {
            onLogin();
        } else {
            setContentView(R.layout.activity_login);

            callbackManager = CallbackManager.Factory.create();

            // Attach additional permissions to the login button
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("email");

            // Attach a callback to the login button that will handle the login request and response
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    Log.v(LOG_TAG, "Login successful");

                    onLogin();
                }

                @Override
                public void onCancel() {
                    Log.v(LOG_TAG, "Login cancelled");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.v(LOG_TAG, "Login error: " + e.getCause().toString());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return (accessToken != null);
    }

    private void onLogin() {
        if (isFirstRun()) {
            AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(fbAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // App code
                        Log.v(LOG_TAG, "Fb user profile request successful");
                        Log.v(LOG_TAG, "Fb request response: " + response.toString());

                        String fbUserProfile = object.toString();
                        goToSetup(fbUserProfile);
                    }
                });
            request.executeAsync();
        } else {
            goToMain();
        }
    }

    private boolean isFirstRun() {
        SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("first_run", true);
    }

    private void goToSetup(String fbUserProfile) {
        Intent intent = new Intent(getBaseContext(), SetupActivity.class);
        intent.putExtra("fb_user_profile", fbUserProfile);
        startActivity(intent);
        finish();
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}