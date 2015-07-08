package cz.fsvoboda.fartlektraining;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Filip on 9.5.2015.
 */

public class SettingsFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
/*
        TextView text = (TextView) rootView.findViewById(R.id.pageCount);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int pos = bundle.getInt("position", 0);
            text.setText(Integer.toString(pos));
        }
*/

        ListView listview = (ListView) rootView.findViewById(android.R.id.list);
        final String[] values = new String[]{"Max HR", "sgasd", "sdgasd"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);

        setListAdapter(adapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO
    }
}
