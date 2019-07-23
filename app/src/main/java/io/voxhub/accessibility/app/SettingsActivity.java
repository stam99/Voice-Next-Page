package io.voxhub.accessibility.app;

import android.preference.PreferenceActivity;
//import android.app.Activity;
import android.preference.SwitchPreference;
import android.os.Bundle;
//import androidx.preference.EditTextPreference.SimpleSummaryProvider;

public class SettingsActivity extends PreferenceActivity {
    
//public class SettingsActivity extends Activity {

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
