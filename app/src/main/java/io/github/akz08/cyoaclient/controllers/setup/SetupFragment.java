package io.github.akz08.cyoaclient.controllers.setup;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.akz08.cyoaclient.R;
import io.github.akz08.cyoaclient.models.Character;
import io.github.akz08.cyoaclient.models.User;
import io.github.akz08.cyoaclient.services.CharacterService;
import io.github.akz08.cyoaclient.services.UserService;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SetupFragment extends Fragment {

    private static final String ARG_PARAM = "resume_progress";

    private final String LOG_TAG = getClass().getSimpleName();
    private OnSetupFragmentInteractionListener mListener;
    private boolean resumeProgress;

    public interface OnSetupFragmentInteractionListener {
        public void onSetupFragmentInteraction();
    }

    public static SetupFragment newInstance(boolean resumeProgress) {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM, resumeProgress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSetupFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnSetupFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resumeProgress = getArguments().getBoolean(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!resumeProgress) {
            resetUser();
        } else {
            setupCharacters();
        }
    }

    private void resetUser() {
        // Build the user request service via Retrofit
        String appServer = getResources().getString(R.string.app_server);
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(appServer)
            .build();
        UserService service = restAdapter.create(UserService.class);

        // Retrieve the user record
        Realm realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();
        long userId = user.getId();
        String apiKey = user.getApiKey();
        Map<String, String> body = new HashMap<String, String>();
        body.put("api_key", apiKey);
        realm.close();

        // Make the request and handle the response
        service.reset(userId, body, new Callback<Void>() {
            public void success(Void successful, Response response) {
                Log.v(LOG_TAG, "User progress reset");
                setupCharacters();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.v(LOG_TAG, "User progress reset error: " + e.getMessage());
            }
        });
    }

    private void setupCharacters() {
        // Build the user request service via Retrofit
        String appServer = getResources().getString(R.string.app_server);
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(appServer)
            .build();
        CharacterService service = restAdapter.create(CharacterService.class);

        // Define the parameters for the request body
        Realm realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("fb_user_id", Long.toString(user.getId()));
        queryMap.put("api_key", user.getApiKey());
        realm.close();

        // Make the request and handle the response
        service.getCharacters(queryMap, new Callback<List<Character>>() {
            public void success(List<Character> characters, Response response) {
                Log.v(LOG_TAG, "Character records retrieved");
                Realm realm = Realm.getInstance(getActivity());
                Gson gson = new Gson();
                for (Character character : characters) {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(character);
                    realm.commitTransaction();
                    Log.v(LOG_TAG, "Character record saved: " + gson.toJson(character));
                }
                realm.close();

                onSetupFinished();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.v(LOG_TAG, "Character records retrieval error: " + e.getMessage());
            }
        });
    }

    private void onSetupFinished() {
        if (mListener != null) {
            mListener.onSetupFragmentInteraction();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}