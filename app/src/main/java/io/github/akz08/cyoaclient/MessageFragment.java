package io.github.akz08.cyoaclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFragment extends Fragment {

    private ArrayAdapter<String> mMessageAdaptor;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        // Create some mock data for the ListView
        String[] data = {"To be?", "Yes.", "Or not to be?", "No"};
        List<String> messageText = new ArrayList<String>(Arrays.asList(data));

        mMessageAdaptor =
            new ArrayAdapter<String>(
                getActivity(),                      // the current context (this activity)
                R.layout.list_item_message,         // the name of the layout ID
                R.id.list_item_message_textview,    // the ID of the textview to populate
                messageText                      // the String array of data for the adaptor
            );

        // Get a reference to the ListView, and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_messages);
        listView.setAdapter(mMessageAdaptor);

        return rootView;
    }

}