package io.github.akz08.cyoaclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.facebook.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    public static final LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions("email");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(LOG_TAG, "Logged in...");
            // Check if this is the first time the application is being run
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Boolean isFirstRun = prefs.getBoolean("is_first_run", true);
            if (isFirstRun) {
                // Request user data and store the results
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Get user information
                            String userId = user.getId();
                            String userFirstName = user.getFirstName();
                            String userLastName = user.getLastName();
                            String email = user.getProperty("email").toString();
                            // Store the user information in the shared preferences
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            prefs.edit().putString("fb_user_id", userId).apply();
                            prefs.edit().putString("fb_user_first_name", userFirstName).apply();
                            prefs.edit().putString("fb_user_last_name", userLastName).apply();
                            prefs.edit().putString("fb_user_email", email).apply();
                            // Authenticate the user's login with the server
                            new AuthenticationTask().execute();
                        }
                    }
                }).executeAsync();
            }
            else {
                // Go to the main activity of the application
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        }
        else if (state.isClosed()) {
            Log.i(LOG_TAG, "Logged out...");
        }
    }

    private class AuthenticationTask extends AsyncTask<Void, Void, Integer> {

        private final String LOG_TAG = AuthenticationTask.class.getSimpleName();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            int responseCode = 0;
            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://192.168.1.128:8000/v0.1/auth");
                URL url = new URL(builtUri.toString());
                // Create the request to the API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                // Add the request parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("uid", prefs.getString("fb_user_id", "DEFAULT")));
                parameters.add(new BasicNameValuePair("first_names", prefs.getString("fb_user_first_name", "DEFAULT")));
                parameters.add(new BasicNameValuePair("last_name", prefs.getString("fb_user_last_name", "DEFAULT")));
                parameters.add(new BasicNameValuePair("email", prefs.getString("fb_user_email", "DEFAULT")));
                parameters.add(new BasicNameValuePair("fb_token", Session.getActiveSession().getAccessToken()));
                // Construct the request query
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(parameters));
                writer.flush();
                writer.close();
                os.close();
                // Send the request
                urlConnection.connect();
                responseCode = urlConnection.getResponseCode();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Intent intent = new Intent(getActivity(), SetupActivity.class);
            intent.putExtra("io.github.akz08.cyoaclient.response_code", result);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (NameValuePair pair : params)
            {
                if (first) {
                    first = false;
                }
                else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
            return result.toString();
        }

    }

}