package jp.naist.ahclab.kaldigstreamerclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private Button btn_back;
    private Switch hoverText;
    private Overlay overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dictation);
        Intent intent = getIntent();
        overlay = Overlay.getInstance();
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open settings page
               // Intent intent = new Intent(SettingsActivity.this, SimpleActivity.class);
               // startActivity(intent);
               finish();
            }
        });
        hoverText = (Switch) findViewById(R.id.hoverText);
        hoverText.setChecked(overlay.isVisible());
        hoverText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    overlay.show();
                } 
                else {
                    overlay.hide();
                }
            }
        });
    };
}
