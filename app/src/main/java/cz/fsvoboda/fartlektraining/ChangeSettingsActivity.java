package cz.fsvoboda.fartlektraining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class ChangeSettingsActivity extends Activity {
    private TextView valueView;
    private Dpad dpad = new Dpad();
    private int value;
    private String key;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_settings);

        ((TextView) findViewById(R.id.leftArrow)).setText("<");

        valueView = (TextView) findViewById(R.id.value);

        Intent intent = getIntent();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        key = intent.getStringExtra("key");
        value = sharedPref.getInt(key, 0);
        valueView.setText(Integer.toString(value));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 23) {
            sharedPref.edit().putInt(key, value).apply();
            setResult(Activity.RESULT_OK);
            finish();
        }

        if (keyCode == 26) {
            finish();
        }

        if (Dpad.isDpadDevice(event)) {

            int press = dpad.getDirectionPressed(event);
            switch (press) {
                case Dpad.LEFT:
                    decreaseValue();
                    return true;
                case Dpad.RIGHT:
                    increaseValue();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);     // kdyz to tady nebude, neprovede se metody onBackPressed
    }

    public void increaseValue() {
        value++;
        valueView.setText(Integer.toString(value));
    }

    public void decreaseValue() {
        value--;
        valueView.setText(Integer.toString(value));
    }
}
