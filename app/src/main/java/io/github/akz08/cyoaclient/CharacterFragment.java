package io.github.akz08.cyoaclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CharacterFragment extends Fragment {

    private ArrayAdapter<String> mCharacterAdaptor;

    public CharacterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_character_select, container, false);

        mCharacterAdaptor =
            new ArrayAdapter<String>(
                getActivity(),                      // the current context (this activity)
                R.layout.list_item_character,       // the name of the layout ID.
                R.id.list_item_character_textview,  // the ID of the textview to populate.
                new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
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

    private void updateCharacters() {
        FetchCharactersTask charactersTask = new FetchCharactersTask();
        charactersTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCharacters();
    }

    public class FetchCharactersTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            String[] characterNames = new String[] {"Claire"};
            return characterNames;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mCharacterAdaptor.clear();
                for(String characterName : result) {
                    mCharacterAdaptor.add(characterName);
                }
            }
        }

    }

}