package io.voxhub.accessibility.app;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
//import android.app.Activity;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.os.Bundle;
//import androidx.preference.EditTextPreference.SimpleSummaryProvider;

public class SettingsActivity extends PreferenceActivity {
    
//public class SettingsActivity extends Activity {

    private void overlayChanged(boolean enabled) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("overlay_enabled", enabled);
        editor.apply();
    }

    private void serverPortChanged(String server, String port) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("server", server);
        editor.putString("port", port);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        editTextPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
//        getFragmentManager().beginTransaction()
//                 .replace(android.R.id.content, new SettingsFragment())
//                 .commit();
        
        Overlay overlay = null;
        final SwitchPreference hovertext = (SwitchPreference) findPreference(this.getResources()
                                           .getString(R.string.hover_text)); 
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        hovertext.setChecked(pref.getBoolean("overlay_enabled", true));
        hovertext.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                overlayChanged((Boolean)newValue);
                return true;
            }
        });

        final EditTextPreference servertext = (EditTextPreference)findPreference("server"); 
        final EditTextPreference porttext = (EditTextPreference)findPreference("port"); 

        servertext.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                serverPortChanged((String)newValue, porttext.getText().toString());
                return true;
            }
        });

        porttext.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                serverPortChanged(servertext.getText().toString(), (String)newValue);
                return true;
            }
        });
        
        if(overlay.getOverlayExists())
            overlay = Overlay.getInstance();
        
        if (hovertext.isChecked()) {
            if(overlay.getOverlayExists())
                overlay.show();
        } 
        else {
            if(overlay.getOverlayExists())
                overlay.hide();
        }

    }
}
