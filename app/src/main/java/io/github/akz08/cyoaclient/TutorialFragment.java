package io.github.akz08.cyoaclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TutorialFragment extends Fragment {

    private OnTutorialFinishListener mCallback;

    public interface OnTutorialFinishListener {
        public void onTutorialFinish();
    }

    public static final TutorialFragment newInstance() {
        TutorialFragment fragment = new TutorialFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTutorialFinishListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTutorialFinishListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        // Handle the button click event
        Button button = (Button) getView().findViewById(R.id.tutorial_skip_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCallback.onTutorialFinish();
            }
        });
        return view;
    }

}