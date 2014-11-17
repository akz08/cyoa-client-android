package io.github.akz08.cyoaclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SelectionFragment extends Fragment {

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private static final int REAUTH_ACTIVITY_CODE = 100;

    private String fb_user_id = null;
    private String fb_user_first_name = null;
    private String fb_user_last_name = null;
    private final String fb_app_id = Session.getActiveSession().getApplicationId();
    private final String fb_access_token = Session.getActiveSession().getAccessToken();

    public SelectionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the UiLifecycleHelper for managing the facebook session
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        // Check if this is the first time the application is being run
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isFirstRun = prefs.getBoolean("is_first_run", true);
        // DON'T FORGET TO SET THIS PREFERENCE TO FALSE AFTER SETUP IS COMPLETE!!!
        // prefs.edit().putBoolean("is_first_run", false).commit();
        if (isFirstRun) {
            // Check if the user has used the application previously
            new FetchUserHistoryTask().execute();
        }
        else {
            // Go to the main activity of the application
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Check for an open session
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a new callback to handle the response
        Request request = Request.newMeRequest(session,
            new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            fb_user_id = user.getId();
                            fb_user_first_name = user.getFirstName();
                            fb_user_last_name = user.getLastName();
                        }
                    }
                    if (response.getError() != null) {
                        // Handle errors, will do so later
                    }
                }
            });
        request.executeAsync();
    }

    private class FetchUserHistoryTask extends AsyncTask<Void, Void, Boolean> {

        private final String LOG_TAG = FetchUserHistoryTask.class.getSimpleName();

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String authJsonStr = null;
            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://localhost:8000/authenticate");
                URL url = new URL(builtUri.toString());
                // Create the request to the API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("fb_app_id", fb_app_id);
                urlConnection.setRequestProperty("fb_user_id", fb_user_id);
                urlConnection.setRequestProperty("fb_access_token", fb_access_token);
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed buffer for debugging
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty - no point in parsing
                    return null;
                }
                // Set the final JSON result
                authJsonStr = buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping to parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            // Process the JSON string, and return the corresponding boolean flag
            try {
                return getAuthDataFromJson(authJsonStr);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the JSON string
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isAuth) {
            Intent intent = new Intent(getActivity(), SetupActivity.class);
            intent.putExtra("io.github.akz08.cyoaclient.is_auth", isAuth);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }

        private boolean getAuthDataFromJson(String authJsonStr) throws JSONException {
            JSONObject authJsonObj = new JSONObject(authJsonStr);
            return authJsonObj.getBoolean("is_auth");
        }

    }

}