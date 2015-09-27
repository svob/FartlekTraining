package cz.fsvoboda.fartlektraining;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Created by Filip on 9.5.2015.
 */

public class MainFragment extends Fragment {

    //private SeekBar seekBar;
    private TextView ahr;
    private TextView timeView;
    private TextView hrTitle;
    private TextView timeTitle;

    AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;


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
        hrTitle = (TextView) rootView.findViewById(R.id.hrTitle);
        timeTitle = (TextView) rootView.findViewById(R.id.timeTitle);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Semibold.ttf");
        hrTitle.setTypeface(type);

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                //timeView.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                //mTextField.setText("done!");
            }
        }.start();

        handleReset();

        return rootView;
    }

    protected void requestAccessToPcc() {
        // starts the plugin UI search
        releaseHandle = AntPlusHeartRatePcc.requestAccess(getActivity(), getActivity(), base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    protected void handleReset() {
        // Release the old access if it exists
        if (releaseHandle != null) {
            releaseHandle.close();
        }

        requestAccessToPcc();
    }

    protected void showDataDisplay(String status) {
        // Reset the display
        ahr.setText("---");
    }

    /**
     * Switches the active view to the data display and subscribes to all the data events
     */
    public void subscribeToHrEvents() {
        hrPcc.subscribeHeartRateDataEvent(new AntPlusHeartRatePcc.IHeartRateDataReceiver() {
            @Override
            public void onNewHeartRateData(long estTimestamp, EnumSet<EventFlag> eventFlags, int computedHeartRate, long heartBeatCount, BigDecimal heartBeatEventTime, AntPlusHeartRatePcc.DataState dataState) {
                // Mark heart rate with asterisk if zero detected
                final String textHeartRate = String.valueOf(computedHeartRate) +((AntPlusHeartRatePcc.DataState.ZERO_DETECTED.equals(dataState)) ? "*" : "");

                // Mark heart beat count and heart beat event time with asterisk if initial value
                final String textHeartBeatCount = String.valueOf(heartBeatCount) + ((AntPlusHeartRatePcc.DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");
                final String textHeartBeatEventTime = String.valueOf(heartBeatEventTime) + ((AntPlusHeartRatePcc.DataState.INITIAL_VALUE.equals(dataState)) ? "*" : "");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ahr.setText(textHeartRate);
                    }
                });
            }
        });
    }

    protected AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc> base_IPluginAccessResultReceiver =
            new AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc>() {
                // Handle the result, connecting to events on success or reporting failure to user.
                @Override
                public void onResultReceived(AntPlusHeartRatePcc antPlusHeartRatePcc, RequestAccessResult requestAccessResult, DeviceState deviceState) {
                    showDataDisplay("Connecting");
                    switch(requestAccessResult) {
                        case SUCCESS:
                            hrPcc = antPlusHeartRatePcc;
                            subscribeToHrEvents();
                            break;
                        case CHANNEL_NOT_AVAILABLE:
                            Toast.makeText(getActivity(), "Channel not available", Toast.LENGTH_SHORT).show();
                            break;
                        case ADAPTER_NOT_DETECTED:
                            Toast.makeText(getActivity(), "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
                            break;
                        case BAD_PARAMS:
                            // Note: Since we compose all the params ourself, we should never see this result
                            Toast.makeText(getActivity(), "Bad request parameters", Toast.LENGTH_SHORT).show();
                            break;
                        case OTHER_FAILURE:
                            Toast.makeText(getActivity(), "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                            break;
                        case DEPENDENCY_NOT_INSTALLED:
                            Toast.makeText(getActivity(), "neco je spatne :(", Toast.LENGTH_SHORT).show();
                            break;
                        case USER_CANCELLED:
                            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                            break;
                        case UNRECOGNIZED:
                            Toast.makeText(getActivity(), "Failed: UNRECOGNIZED. PluginLib Upgrade Required?", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getActivity(), "Unrecognized result: "+requestAccessResult, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

    // Receives state changes and shows it on the status display line
    protected AntPluginPcc.IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
            new AntPluginPcc.IDeviceStateChangeReceiver() {
                @Override
                public void onDeviceStateChange(final DeviceState deviceState) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), hrPcc.getDeviceName() + ": "+ deviceState, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };

    @Override
    public void onDestroy() {
        if (releaseHandle != null) {
            releaseHandle.close();
        }
        super.onDestroy();
    }
}
