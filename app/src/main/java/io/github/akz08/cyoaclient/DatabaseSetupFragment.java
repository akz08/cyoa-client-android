package io.github.akz08.cyoaclient;

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

import com.facebook.Session;

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

import io.github.akz08.cyoaclient.data.DatabaseHelper;

public class DatabaseSetupFragment extends Fragment {

    public DatabaseSetupFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            boolean continueProgress = bundle.getBoolean("io.github.akz08.cyoaclient.continue_progress");
            boolean redoTutorial = bundle.getBoolean("io.github.akz08.cyoaclient.redo_tutorial");
            if (!continueProgress) {
                new EraseProgressTask().execute();
            }
            else {
                new FetchProgressTask().execute();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_database_setup, container, false);
    }

    private class EraseProgressTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = EraseProgressTask.class.getSimpleName();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        private final String uid = prefs.getString("fb_user_id", "DEFAULT");
        private final String fb_token = Session.getActiveSession().getAccessToken();

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://192.168.1.128:8000/v0.1/messages");
                URL url = new URL(builtUri.toString());
                // Create the request to the API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                // Add the request parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("uid", uid));
                parameters.add(new BasicNameValuePair("fb_token", fb_token));
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
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting to parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Create the database with clean data
            new FetchProgressTask().execute();
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (NameValuePair pair : params) {
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

    private class FetchProgressTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchProgressTask.class.getSimpleName();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        private final String uid = prefs.getString("fb_user_id", "DEFAULT");
        private final String fb_token = Session.getActiveSession().getAccessToken();

        @Override
        protected Void doInBackground(Void... params) {
            // Initialise the database and data structures to hold retrieved data temporarily
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
            String charactersJsonStr = null;
            String scenesJsonStr = null;
            String messagesJsonStr = null;
            String choicesJsonStr = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://192.168.1.128:8000/v0.1/messages");
                URL url = new URL(builtUri.toString());
                // Create the request to the API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                // Add the request parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("uid", uid));
                parameters.add(new BasicNameValuePair("fb_token", fb_token));
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
                charactersJsonStr = urlConnection.getResponseMessage();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting to parse it
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Set the shared preferences to indicate that the application has been run
            prefs.edit().putBoolean("is_first_run", false).apply();
            // Launch the tutorial
            getFragmentManager().beginTransaction()
                .replace(R.id.activity_setup_container, new TutorialFragment())
                .commit();
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (NameValuePair pair : params) {
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