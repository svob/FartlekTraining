package cz.fsvoboda.fartlektraining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by Filip on 9.5.2015.
 */

public class MainFragment extends Fragment {
    private TextView ahr;
    private TextView timeView;
    private TextView instructions;
    private TextView phase;
    private CountDownTimer timer;
    private int maxHr;
    private int first;
    private int second;
    private int third;
    private int hrForFirst;
    private int hrForSecond;
    private boolean accelerating = true;

    AntPlusHeartRatePcc hrPcc = null;
    protected PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        ahr = (TextView) rootView.findViewById(R.id.ahr);
        timeView = (TextView) rootView.findViewById(R.id.time);
        TextView hrTitle = (TextView) rootView.findViewById(R.id.hrTitle);
        TextView timeTitle = (TextView) rootView.findViewById(R.id.timeTitle);
        instructions = (TextView) rootView.findViewById(R.id.instructions);
        phase = (TextView) rootView.findViewById(R.id.phase);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Semibold.ttf");
        hrTitle.setTypeface(type);
        timeTitle.setTypeface(type);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        maxHr = sharedPref.getInt("hr", 200);
        first = sharedPref.getInt("first", 5);
        second = sharedPref.getInt("second", 10);
        third = sharedPref.getInt("third", 5);

        hrForFirst = (int)(maxHr * 0.6);
        hrForSecond = (int)(maxHr * 0.75);

        handleReset();

        return rootView;
    }

    public void startFirstPhase() {
        phase.setText("Rozklusání");
        instructions.setText("Klusej");
        timer = new CountDownTimer(first*60*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeView.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                )));
            }
            public void onFinish() {
                if (Integer.valueOf(ahr.getText().toString()) > (maxHr * 0.6))
                    startSecondPhase();
                else
                    goUntilHrReached(hrForFirst);
            }
        }.start();
    }

    private void startSecondPhase() {
        phase.setText("Trénink");
        timer = new CountDownTimer(second*60*1000, 1000) {

            public void onTick(final long millisUntilFinished) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int hr = Integer.valueOf(ahr.getText().toString());

                        timeView.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                        )));
                        if (accelerating && hr < hrForSecond) {
                            instructions.setText("Přidej");
                        } else if (accelerating && hr > hrForSecond) {
                            instructions.setText("Zpomal");
                            accelerating = false;
                        } else if (!accelerating && hr > hrForFirst) {
                            instructions.setText("Zpomal");
                        } else {
                            instructions.setText("Přidej");
                            accelerating = true;
                        }
                    }
                });
            }
            public void onFinish() {
                startThirdPhase();
            }
        }.start();
    }

    private void startThirdPhase() {
        phase.setText("Vyklusání");
        instructions.setText(" ");
        timer = new CountDownTimer(third*60*1000, 1000) {

            public void onTick(final long millisUntilFinished) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int hr = Integer.valueOf(ahr.getText().toString());

                        timeView.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                        )));
                    }
                });
            }
            public void onFinish() {
                instructions.setText("A máme to za sebou");
            }
        }.start();
    }

    public void goUntilHrReached(final int targetHr) {
        if (Integer.valueOf(ahr.getText().toString()) < targetHr) {
            instructions.setText("Přidej");
            timer = new CountDownTimer(300000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (Integer.valueOf(ahr.getText().toString()) > targetHr) {
                        timer.cancel();
                        startSecondPhase();
                    }
                }
                public void onFinish() {
                    goUntilHrReached(targetHr);
                }
            }.start();
        }
        else {
            instructions.setText("Zpomal");
            timer = new CountDownTimer(300000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (Integer.valueOf(ahr.getText().toString()) < targetHr) {
                        timer.cancel();
                        startSecondPhase();
                    }
                }
                public void onFinish() {
                    goUntilHrReached(targetHr);
                }
            }.start();
        }
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

    /**
     * Switches the active view to the data display and subscribes to all the data events
     */
    public void subscribeToHrEvents() {
        hrPcc.subscribeHeartRateDataEvent(new AntPlusHeartRatePcc.IHeartRateDataReceiver() {
            @Override
            public void onNewHeartRateData(long estTimestamp, EnumSet<EventFlag> eventFlags, int computedHeartRate, long heartBeatCount, BigDecimal heartBeatEventTime, AntPlusHeartRatePcc.DataState dataState) {
                final String textHeartRate = String.valueOf(computedHeartRate);

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
                    switch(requestAccessResult) {
                        case SUCCESS:
                            hrPcc = antPlusHeartRatePcc;
                            subscribeToHrEvents();
                            startFirstPhase();
                            break;
                        case CHANNEL_NOT_AVAILABLE:
                            Toast.makeText(getActivity(), "Channel not available", Toast.LENGTH_SHORT).show();
                            break;
                        case ADAPTER_NOT_DETECTED:
                            Toast.makeText(getActivity(), "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_LONG).show();
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
