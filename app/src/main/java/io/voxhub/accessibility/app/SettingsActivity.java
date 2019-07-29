package io.voxhub.accessibility.app;

import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {
    
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

    private void borderChanged(String border) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("border", border);
        editor.apply();
    }

    private void autoBackgroundChanged(boolean enabled) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("autoBackground", enabled);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        //Overlay overlay = null;
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
        
        final EditTextPreference bordertext = (EditTextPreference)findPreference("border"); 

        bordertext.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                borderChanged((String) newValue);
                return true;
            }
        });

        final SwitchPreference autoBackground = 
            (SwitchPreference) findPreference("autoBackground"); 
        autoBackground.setChecked(pref.getBoolean("autoBackground", true));
        autoBackground
            .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                autoBackgroundChanged((Boolean) newValue);
                return true;
            }
        });

        //if(overlay.getOverlayExists())
        //    overlay = Overlay.getInstance();
        //
        //if (hovertext.isChecked()) {
        //    if(overlay.getOverlayExists())
        //        overlay.show();
        //} 
        //else {
        //    if(overlay.getOverlayExists())
        //        overlay.hide();
        //}

    }
}
