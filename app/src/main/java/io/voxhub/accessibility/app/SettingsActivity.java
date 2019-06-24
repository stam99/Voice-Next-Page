package io.voxhub.accessibility.app;

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
import android.widget.Toast;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    private TextView serverT;
    private TextView portT;
    private EditText server;
    private EditText port;
    private TextView reset_blurb;
    private Button btn_reset;
    private Switch hoverText;
    private Overlay overlay;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_dictation);
        Intent intent = getIntent();
        
        serverT = (TextView) findViewById(R.id.server_title);
        portT = (TextView) findViewById(R.id.port_title);
        server = (EditText) findViewById(R.id.server);
        port = (EditText) findViewById(R.id.port);

        server.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    SimpleActivity.getServerInfo().setAddr(server.getText().toString());
                }
            }
        });
        port.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    SimpleActivity.getServerInfo().setAddr(port.getText().toString());
                }
            }
        });
        reset_blurb = (TextView) findViewById(R.id.reset_blurb);
        reset_blurb.setText("Reset server and port\nto original values: ");
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Reset server/port 
                Log.i("SettingsActivity", "reset button clicked");
                server.setText("silvius-server.voxhub.io"); // Hard coded
                port.setText("8022");
                SimpleActivity.getServerInfo().setAddr(getResources().getString(R.string.default_server_addr));
                SimpleActivity.getServerInfo().setPort(Integer.parseInt(getResources().getString(R.string.default_server_port)));
            }
        });
        hoverText = (Switch) findViewById(R.id.hoverText);
        btn_back = (Button) findViewById(R.id.btn_back);
        if(overlay.getOverlayExists()){
            overlay = Overlay.getInstance();
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
        }
        else{
            Toast.makeText(getApplicationContext(), "ACTION_MANAGE_OVERLAY_PERMISSION Permiss    ion Needed", Toast.LENGTH_SHORT).show();
        }
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open main page
               // Intent intent = new Intent(SettingsActivity.this, SimpleActivity.class);
               // startActivity(intent);
               finish();
            }
        });
    };
}
