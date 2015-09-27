package cz.fsvoboda.fartlektraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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

        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String[] values = new String[]{"Max HR", "Délka 1. bloku", "Délka 2. bloku", "Délka 3. bloku"};

        SettingsArrayAdapter adapter = new SettingsArrayAdapter(getActivity(), values);
        setListAdapter(adapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //String item = (String) getListAdapter().getItem(position);
        //Toast.makeText(getActivity(), item + "selected", Toast.LENGTH_LONG).show();
        TextView value = (TextView) v.findViewById(R.id.value);
        SpannableStringBuilder sb = new SpannableStringBuilder("< "+value.getText().toString()+" >");
        sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), sb.length()-1, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        value.setText(sb);
/*
        Spannable original = new SpannableString(value.getText().toString());
        Spannable left = new SpannableString("<");
        Spannable right = new SpannableString(">");
        left.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), 0, left.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        right.setSpan(new ForegroundColorSpan(Color.rgb(255, 179, 0)), 0, right.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        value.setText(left);
        value.append(original);
        value.append(right);*/
    }
}
