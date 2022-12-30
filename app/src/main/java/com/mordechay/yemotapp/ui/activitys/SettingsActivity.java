package com.mordechay.yemotapp.ui.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

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
        SwitchPreferenceCompat betaPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("language").setOnPreferenceChangeListener(this);
            betaPreference = findPreference("beta");
            betaPreference.setOnPreferenceChangeListener(this);
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


                startActivity(new Intent(getActivity(), getActivity().getClass()));
                getActivity().finish();
                return true;
            } else if (preference.getKey().equals("beta")) {
                boolean beta = (boolean) newValue;
                if (beta) {
                    // Create the AlertDialog.Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("האם אתה בטוח?");
                    builder.setMessage(
                            "בהפעלת תוכנות בטא xxxxxxx" +
                            " \n " +

                                    "ייתכן שהתכונות לא יפעלו כמצופה."+
                            "\n"+ "האם אתה בטוח שברצונך להמשיך?");

                    // Add the buttons
                    builder.setPositiveButton("אישור", (dialog, id) -> {
                        // User clicked confirm button
                        betaPreference.setChecked(true);
                    });
                    builder.setNegativeButton("ביטול", null);
                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return false;
                }
                }
            return true;
        }
    }
    }