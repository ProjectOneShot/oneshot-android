package application.oneshot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import application.oneshot.constants.Preferences;

public class SettingsActivity
        extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    public static class GeneralPreferenceFragment
            extends PreferenceFragmentCompat {

//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            final PreferenceManager preferenceManager = getPreferenceManager();
            preferenceManager.setSharedPreferencesName(Preferences.SHARED_PREFERENCE);

            addPreferencesFromResource(R.xml.pref_general);

            final ListPreference listPreference = (ListPreference) findPreference(Preferences.USE_NIGHT_MODE);
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AppCompatDelegate.setDefaultNightMode(Integer.parseInt(newValue.toString()));

                    getActivity().recreate();

                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));

                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
}
