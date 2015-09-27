package cz.fsvoboda.fartlektraining;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Filip on 1.9.2015.
 */

public class SettingsArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String[] val = new String[]{"198", "5 min.", "10 min.", "2 min."};

    public SettingsArrayAdapter(Context context, String[] values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView label = (TextView) rowView.findViewById(R.id.label);
        TextView value = (TextView) rowView.findViewById(R.id.value);

        label.setText(values[position]);
        value.setText(val[position]);


        return rowView;
    }
}
