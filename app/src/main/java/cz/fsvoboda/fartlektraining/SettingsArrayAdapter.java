package cz.fsvoboda.fartlektraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filip on 1.9.2015.
 */

public class SettingsArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] keys;
    private final List<Integer> values;

    public SettingsArrayAdapter(Context context, String[] keys, List<Integer> values) {
        super(context, R.layout.rowlayout, keys);
        this.context = context;
        this.keys = keys;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView label = (TextView) rowView.findViewById(R.id.label);
        TextView value = (TextView) rowView.findViewById(R.id.value);

        label.setText(keys[position]);
        value.setText(values.get(position).toString());
        if (position != 0)
            value.append(" min.");

        return rowView;
    }
}
