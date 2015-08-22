package io.github.akz08.cyoaclient.controllers.setup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.akz08.cyoaclient.R;
import io.github.akz08.cyoaclient.controllers.main.MainActivity;
import io.github.akz08.cyoaclient.models.User;
import io.realm.Realm;

public class SetupActivity extends Activity implements
    AuthenticationFragment.OnAuthenticationFragmentInteractionListener,
    ExistingUserFragment.OnExistingUserFragmentInteractionListener,
    SetupFragment.OnSetupFragmentInteractionListener {

    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Retrieve the Fb access token and user profile from intent
        Bundle extras = getIntent().getExtras();
        String fbUserProfile = extras.getString("fb_user_profile");
        //String fbUserProfile = "{\"id\":\"347256552119472\",\"first_name\":\"Test\",\"timezone\":0,\"email\":\"test_wbckqia_user@tfbnw.net\",\"verified\":false,\"name\":\"Test User\",\"locale\":\"en_GB\",\"link\":\"https:\\/\\/www.facebook.com\\/app_scoped_user_id\\/347256552119472\\/\",\"last_name\":\"User\",\"gender\":\"male\",\"updated_time\":\"2014-11-16T14:40:19+0000\"}";

        // Create and persist the user record via GSON by using passed Fb profile data JSON string
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        User user = gson.fromJson(fbUserProfile, User.class);
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
        Log.v(LOG_TAG, "User record saved: " + gson.toJson(user));
        realm.close();

        AuthenticationFragment authenticationFragment = AuthenticationFragment.newInstance();
        getFragmentManager().beginTransaction()
            .add(R.id.activity_setup, authenticationFragment)
            .commit();
    }

    @Override
    public void onAuthenticationFragmentInteraction(int authenticationStatus) {
        // Launch the progress fragment if the user exists, and the setup fragment otherwise
        if (authenticationStatus == 200) {
            ExistingUserFragment existingUserFragment = ExistingUserFragment.newInstance();
            getFragmentManager().beginTransaction()
                .replace(R.id.activity_setup, existingUserFragment)
                .commit();
        } else {
            SetupFragment setupFragment = SetupFragment.newInstance(true);
            getFragmentManager().beginTransaction()
                .replace(R.id.activity_setup, setupFragment)
                .commit();
        }
    }

    @Override
    public void onExistingUserFragmentInteraction(boolean resumeProgress) {
        SetupFragment setupFragment = SetupFragment.newInstance(resumeProgress);
        getFragmentManager().beginTransaction()
            .replace(R.id.activity_setup, setupFragment)
            .commit();
    }

    @Override
    public void onSetupFragmentInteraction() {
        // Update shared preferences to indicate that app has now been run before
        SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        prefs.edit()
            .putBoolean("first_run", false)
            .apply();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}