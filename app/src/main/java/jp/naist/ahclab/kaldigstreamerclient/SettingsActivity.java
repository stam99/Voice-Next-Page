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

        reset_blurb = (TextView) findViewById(R.id.reset_blurb);
        reset_blurb.setText("Reset server and port\nto original values: ");
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Reset server/port 
                server.setText("silvius-server.voxhub.io"); // Hard coded
                port.setText("8022");
                SimpleActivity.getServerInfo().setAddr(getResources().getString(R.string.default_server_addr));
                SimpleActivity.getServerInfo().setPort(Integer.parseInt(getResources().getString(R.string.default_server_port)));
            }
        });
        overlay = Overlay.getInstance();
        hoverText = (Switch) findViewById(R.id.hoverText);
        hoverText.setChecked(overlay.isVisible());
        btn_back = (Button) findViewById(R.id.btn_back);
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
