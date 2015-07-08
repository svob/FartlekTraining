package cz.fsvoboda.fartlektraining;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Filip on 9.5.2015.
 */

public class MainFragment extends Fragment {

    //private SeekBar seekBar;
    private TextView ahr;
    private TextView timeView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
/*
        TextView text = (TextView) rootView.findViewById(R.id.pageCount);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int pos = bundle.getInt("position", 0);
            text.setText(Integer.toString(pos));
        }
        */
        ahr = (TextView) rootView.findViewById(R.id.ahr);

        timeView = (TextView) rootView.findViewById(R.id.time);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();


        return rootView;
    }
}
