package io.voxhub.accessibility.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;

public class HelpActivity extends Activity {

    private Button btn_back;
    private TextView title;
    private TextView commands;
    private TextView bodyT;
    private TextView bodyC;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_dictation);
        Intent intent = getIntent();
        btn_back = (Button) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.help_title);
        commands = (TextView) findViewById(R.id.contributors);
        bodyT = (TextView) findViewById(R.id.help_body);
        bodyC = (TextView) findViewById(R.id.commands_body);
        
      //  bodyT.setText("\n");
        bodyC.setText(Html.fromHtml(
              "<b>\"Next Page\"</b> - generates a tap on the right side of the screen<br><br>"
            + "<b>\"Previous Page\"</b> - generates a tap on the left side of the screen<br><br>"
            + "<b>\"Center\"</b> - generates a tap on the center of the screen<br><br>"
            + "<b>\"Foreground\"</b> - brings the app into the main focus<br><br>"
            + "<b>\"Background\"</b> - brings the app out of the main focus<br><br>"
            + "<b>\"Stop Listening\"</b> - stops the recording<br><br>"), 
            TextView.BufferType.SPANNABLE);
        
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open main page
               finish();
            }
        });
    };
}
