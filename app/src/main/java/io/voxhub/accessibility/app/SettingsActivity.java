package io.voxhub.accessibility.app;

import android.preference.PreferenceActivity;
//import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {
    
//public class SettingsActivity extends Activity {
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        getFragmentManager().beginTransaction()
//                 .replace(android.R.id.content, new SettingsFragment())
//                 .commit();
    }

}
