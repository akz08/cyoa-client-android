package io.github.akz08.cyoaclient.controllers.setup;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import io.github.akz08.cyoaclient.R;
import io.github.akz08.cyoaclient.models.User;
import io.github.akz08.cyoaclient.pojos.ApiKey;
import io.github.akz08.cyoaclient.services.UserService;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AuthenticationFragment extends Fragment {

    private final String LOG_TAG = getClass().getSimpleName();
    private OnAuthenticationFragmentInteractionListener mListener;

    public interface OnAuthenticationFragmentInteractionListener {
        void onAuthenticationFragmentInteraction(int authenticationStatus);
    }

    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAuthenticationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnAuthenticationFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authentication, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Build the user request service via Retrofit
        String appServer = getResources().getString(R.string.app_server);
        RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(appServer)
            .build();
        UserService service = restAdapter.create(UserService.class);

        // Define the parameters for the request body
        Realm realm = Realm.getInstance(getActivity());
        User user = realm.where(User.class).findFirst();
        long userId = user.getId();
        Map<String, String> body = new HashMap<String, String>();
        body.put("first_name", user.getFirstName());
        body.put("last_name", user.getLastName());
        body.put("email", user.getEmail());
        realm.close();

        // Make the request and handle the response
        service.authenticate(userId, body, new Callback<ApiKey>() {
            public void success(ApiKey apiKeyObj, Response response) {
                int authenticationStatus = response.getStatus();
                String apiKey = apiKeyObj.getApiKey();

                Log.v(LOG_TAG, "User authentication successful: " + authenticationStatus);

                // Update api key field on user record
                Realm realm = Realm.getInstance(getActivity());
                User user = realm.where(User.class).findFirst();
                realm.beginTransaction();
                user.setApiKey(apiKey);
                realm.commitTransaction();
                Log.v(LOG_TAG, "Api key saved to user record: " + apiKey);
                realm.close();

                onAuthenticationFinish(authenticationStatus);
            }

            @Override
            public void failure(RetrofitError e) {
                final String error = "User authentication error: ";

                if (e.getKind() == RetrofitError.Kind.CONVERSION) {
                    Log.v(LOG_TAG, error + "conversion error");
                } else if (e.getKind() == RetrofitError.Kind.HTTP) {
                    Log.v(LOG_TAG, error + "HTTP error");
                } else if (e.getKind() == RetrofitError.Kind.NETWORK) {
                    Log.v(LOG_TAG, error + "network error");
                } else if (e.getKind() == RetrofitError.Kind.UNEXPECTED) {
                    Log.v(LOG_TAG, error + "unexpected error");
                }
            }
        });
    }

    private void onAuthenticationFinish(int authenticationStatus) {
        if (mListener != null) {
            mListener.onAuthenticationFragmentInteraction(authenticationStatus);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}