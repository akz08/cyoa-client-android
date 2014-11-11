package io.github.akz08.cyoaclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterSelectFragment extends Fragment {

    private ArrayAdapter<String> mCharacterAdaptor;

    public CharacterSelectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_character_select, container, false);

        // Create some mock data for the ListView
        String[] data = {"Claire", "Kyle", "Amy", "Otis", "Gabrielle"};
        List<String> characterNames = new ArrayList<String>(Arrays.asList(data));

        mCharacterAdaptor =
            new ArrayAdapter<String>(
                getActivity(),                      // the current context (this activity)
                R.layout.list_item_character,       // the name of the layout ID
                R.id.list_item_character_textview,  // the ID of the textview to populate
                characterNames                      // the String array of data for the adaptor
            );

        // Get a reference to the ListView, and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_characters);
        listView.setAdapter(mCharacterAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String character = mCharacterAdaptor.getItem(position);
                Intent intent = new Intent(getActivity(), MessageActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, character);
                startActivity(intent);
            }
        });

        return rootView;
    }

}