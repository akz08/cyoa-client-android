package io.github.akz08.cyoaclient;

import android.app.Activity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private OnDatabaseSetupFinishListener mCallback;

    public interface OnDatabaseSetupFinishListener {
        public void onDatabaseSetupFinish();
    }

    public static final DatabaseSetupFragment newInstance(boolean continueProgress) {
        DatabaseSetupFragment fragment = new DatabaseSetupFragment();
        Bundle args = new Bundle();
        args.putBoolean("io.github.akz08.cyoaclient.continue_progress", continueProgress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDatabaseSetupFinishListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDatabaseSetupFinishListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            boolean continueProgress = bundle.getBoolean("io.github.akz08.cyoaclient.continue_progress");
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
            deleteProgressFromTable("characters");
            deleteProgressFromTable("scenes");
            deleteProgressFromTable("messages");
            deleteProgressFromTable("choices");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
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

        private void deleteProgressFromTable(String tableName) {
            HttpURLConnection urlConnection = null;
            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://192.168.1.128:8000/v0.1/" + tableName);
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
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

    }

    private class FetchProgressTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchProgressTask.class.getSimpleName();

        private DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        private final String uid = prefs.getString("fb_user_id", "DEFAULT");
        private final String fb_token = Session.getActiveSession().getAccessToken();

        @Override
        protected Void doInBackground(Void... params) {
            // Character table
            String characterJsonStr = fetchProgressFromTable("characters");
            String[][] characterArray = null;
            try {
                characterArray = parseCharacterJsonStr(characterJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            String characterId;
            String characterName;
            String characterAge;
            String characterDescription;
            for (int i = 0 ; i < characterArray.length ; ++i) {
                characterId = characterArray[i][0];
                characterName = characterArray[i][1];
                characterAge = characterArray[i][2];
                characterDescription = characterArray[i][3];
                databaseHelper.insertCharacterIntoDatabase(characterId, characterName, characterAge, characterDescription);
            }
            // Scene table
            String sceneJsonStr = fetchProgressFromTable("scenes");
            String[][] sceneArray = null;
            try {
                sceneArray = parseSceneJsonStr(sceneJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            String sceneId;
            String sceneInformation;
            for (int i = 0 ; i < sceneArray.length ; ++i) {
                sceneId = sceneArray[i][0];
                characterId = sceneArray[i][1];
                sceneInformation = sceneArray[i][2];
                databaseHelper.insertSceneIntoDatabase(sceneId, characterId, sceneInformation);
            }
            // Message table
            String messageJsonStr = fetchProgressFromTable("messages");
            String[][] messageArray = null;
            try {
                messageArray = parseMessageJsonStr(messageJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            String messageId;
            String messageText;
            for (int i = 0 ; i < messageArray.length ; ++i) {
                messageId = messageArray[i][0];
                sceneId = messageArray[i][1];
                messageText = messageArray[i][2];
                databaseHelper.insertMessageIntoDatabase(messageId, sceneId, messageText);
            }
            // Choice table
            String choiceJsonStr = fetchProgressFromTable("choices");
            String[][] choiceArray = null;
            try {
                choiceArray = parseChoiceJsonStr(choiceJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            String choiceId;
            String choiceText;
            for (int i = 0 ; i < choiceArray.length ; ++i) {
                choiceId = choiceArray[i][0];
                messageId = choiceArray[i][1];
                choiceText = choiceArray[i][2];
                databaseHelper.insertChoiceIntoDatabase(choiceId, messageId, choiceText);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onDatabaseSetupFinish();
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

        private String fetchProgressFromTable(String tableName) {
            HttpURLConnection urlConnection = null;
            BufferedWriter writer = null;
            BufferedReader reader = null;
            try {
                // Construct the URL for the API query
                Uri builtUri = Uri.parse("http://192.168.1.128:8000/v0.1/" + tableName);
                URL url = new URL(builtUri.toString());
                // Create the request to the API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // Add the request parameters
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("uid", uid));
                parameters.add(new BasicNameValuePair("fb_token", fb_token));
                // Construct the request query
                OutputStream os = urlConnection.getOutputStream();
                writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(parameters));
                writer.flush();
                writer.close();
                os.close();
                // Send the request
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Makes debugging easier if print out the completed buffer for debugging
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                return buffer.toString();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
        }

        private String[][] parseCharacterJsonStr(String jsonStr) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("characters");
            String[][] characterArray = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                characterArray[i][0] = jsonArray.getJSONObject(i).getString("character_id");
                characterArray[i][1] = jsonArray.getJSONObject(i).getString("character_name");
                characterArray[i][2] = jsonArray.getJSONObject(i).getString("character_age");
                characterArray[i][3] = jsonArray.getJSONObject(i).getString("character_description");
            }
            for (String[] character : characterArray) {
                Log.v(LOG_TAG, "Character entry: " + character[0] + ", " + character[1] + ", " + character[2] + ", " + character[3]);
            }
            return characterArray;
        }

        private String[][] parseSceneJsonStr(String jsonStr) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("scenes");
            String[][] sceneArray = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                sceneArray[i][0] = jsonArray.getJSONObject(i).getString("scene_id");
                sceneArray[i][1] = jsonArray.getJSONObject(i).getString("character_id");
                sceneArray[i][2] = jsonArray.getJSONObject(i).getString("scene_information");
            }
            for (String[] scene : sceneArray) {
                Log.v(LOG_TAG, "Scene entry: " + scene[0] + ", " + scene[1] + ", " + scene[2]);
            }
            return sceneArray;
        }

        private String[][] parseMessageJsonStr(String jsonStr) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            String[][] messageArray = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                messageArray[i][0] = jsonArray.getJSONObject(i).getString("message_id");
                messageArray[i][1] = jsonArray.getJSONObject(i).getString("scene_id");
                messageArray[i][2] = jsonArray.getJSONObject(i).getString("message_text");
            }
            for (String[] message : messageArray) {
                Log.v(LOG_TAG, "Message entry: " + message[0] + ", " + message[1] + ", " + message[2]);
            }
            return messageArray;
        }

        private String[][] parseChoiceJsonStr(String jsonStr) throws JSONException {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("choices");
            String[][] choiceArray = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                choiceArray[i][0] = jsonArray.getJSONObject(i).getString("choice_id");
                choiceArray[i][1] = jsonArray.getJSONObject(i).getString("message_id");
                choiceArray[i][2] = jsonArray.getJSONObject(i).getString("choice_text");
            }
            for (String[] choice : choiceArray) {
                Log.v(LOG_TAG, "Scene entry: " + choice[0] + ", " + choice[1] + ", " + choice[2]);
            }
            return choiceArray;
        }

    }

}