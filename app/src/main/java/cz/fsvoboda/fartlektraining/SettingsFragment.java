package cz.fsvoboda.fartlektraining;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Filip on 9.5.2015.
 */

public class SettingsFragment extends ListFragment {
    private SharedPreferences sharedPref;
    private ViewGroup rootView;
    private List<Integer> values;
    private SettingsArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        this.rootView = rootView;

        final String[] keys = new String[]{"Max HR", "Délka 1. bloku", "Délka 2. bloku", "Délka 3. bloku"};
        values = new ArrayList<Integer>();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateValues();

        adapter = new SettingsArrayAdapter(getActivity(), keys, values);
        setListAdapter(adapter);

        return rootView;
    }

    private void updateValues() {
        values.clear();
        values.add(sharedPref.getInt("hr", 200));
        values.add(sharedPref.getInt("first", 1));
        values.add(sharedPref.getInt("second", 5));
        values.add(sharedPref.getInt("third", 1));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(v.getContext(), ChangeSettingsActivity.class);
        String editingKey;
        if (position == 0)
            editingKey = "hr";
        else if (position == 1)
            editingKey = "first";
        else if (position == 2)
            editingKey = "second";
        else
            editingKey = "third";

        intent.putExtra("key", editingKey);
        startActivityForResult(intent, 1);
        getActivity().overridePendingTransition(0, 0);
/*
        if (!editing) {
        TextView value = (TextView) v.findViewById(R.id.value);
        SpannableStringBuilder sb = new SpannableStringBuilder("< "+value.getText().toString()+" >");
        sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), sb.length()-1, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        value.setText(sb);
            editing = true;
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == getActivity().RESULT_OK) {
                updateValues();
                adapter.notifyDataSetChanged();
            }
        }
    }
}
