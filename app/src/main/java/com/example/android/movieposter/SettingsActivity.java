package com.example.android.movieposter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class MoviePreferenceFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            // Add Fragment to the Settings screen
            addPreferencesFromResource(R.xml.settings_main);

            // Loop through the Preferences and set their summaries
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen prefScreen = getPreferenceScreen();
            int count = prefScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference p = prefScreen.getPreference(i);
                String value = sharedPreferences.getString(p.getKey(), "");
                setListPreferenceSummary(p, value);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // If the Preference changes, set the new summary as the Preference summary
            Preference preference = findPreference(key);
            if (null != preference) {
                setListPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }

        @Override
        public void onStart() {
            super.onStart();

            // Register the listener
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();

            // Unregister the listener
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        /*
        * Set the summary of the ListPreference
        * @param preference: the ListPreference, the summary of which I want to set
        * @param value: the value that I want to set as the summary of the ListPreference
        */
        public void setListPreferenceSummary(Preference preference, Object value){
            String stringValue = value.toString();
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        }
    }
}
