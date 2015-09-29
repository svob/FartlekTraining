package cz.fsvoboda.fartlektraining;

import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;


public class MainActivity extends FragmentActivity {
    private static final int NUM_PAGES = 2; // pocet stranek (wizard steps)
    public ViewPager mPager;       // Obsluhuje swipe-prechod na dalsi stranku
    private PagerAdapter mPagerAdapter;     // Poskytuje stranky pro ViewPager
    Dpad mDpad = new Dpad();
    private boolean disabledPad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("firstRun", true)) {
            sharedPref.edit().putInt("hr", 200).apply();
            sharedPref.edit().putInt("first", 1).apply();
            sharedPref.edit().putInt("second", 5).apply();
            sharedPref.edit().putInt("third", 1).apply();
            sharedPref.edit().putBoolean("firstRun", false).apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            finish();
            System.exit(0);
            // super.onBackPressed();  // Kdyz je na prvni strance, obslouzi to system -> vypnuti appky ........ nefungovalo vzdy, proto nahrazeno vlastnim vypnutim
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1); // vrati se na predchozi stranku
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Dpad.isDpadDevice(event)) {

            int press = mDpad.getDirectionPressed(event);
            switch (press) {
                case Dpad.LEFT:
                    // Do something for LEFT direction press
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                    return true;
                case Dpad.RIGHT:
                    // Do something for RIGHT direction press
                     mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);     // kdyz to tady nebude, neprovede se metody onBackPressed
    }

    // U narocnejsich fragmentu pouzit FragmentStatePagerAdapter
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainFragment();
                case 1:
                    return new SettingsFragment();
            }

            return new MainFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
