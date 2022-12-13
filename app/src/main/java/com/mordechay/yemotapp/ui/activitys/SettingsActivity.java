package com.mordechay.yemotapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.mordechay.yemotapp.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("language").setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String lang = null;
            if (preference.getKey().equals("language")) {


                lang = (String) newValue;
                Configuration config;
                config = getActivity().getBaseContext().getResources().getConfiguration();
                Locale locale;
                if (!lang.equals("default")) {


                    locale = new Locale(lang);

                } else {
                    locale = new Locale(Locale.getDefault().getLanguage());

                }
                Locale.setDefault(locale);
                config.setLocale(locale);

                getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

            }
            startActivity(new Intent(getActivity(), getActivity().getClass()));
            getActivity().finish();
            return true;
        }
    }
}