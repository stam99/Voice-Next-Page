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

public class AboutActivity extends Activity {

    private Button btn_back;
    private TextView title;
    private TextView contributors;
    private TextView bodyT;
    private TextView bodyC;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dictation);
        Intent intent = getIntent();
        btn_back = (Button) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.about_title);
        contributors = (TextView) findViewById(R.id.contributors);
        bodyT = (TextView) findViewById(R.id.about_body);
        bodyC = (TextView) findViewById(R.id.contributors_body);
        
        bodyT.setText("Use speech recognition to turn pages hands-free in ebook reader apps. Commands like \"next page\" and \"previous page\" generate taps on the edges of the screen. Verified to work with Amazon Kindle and Rakuten Kobo ereader apps. Speech recognition requires an internet connection.\n\n");
        bodyC.setText("Serena Tam, Sarah Leventhal, David Williams-King.\n\n");
        
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open main page
               finish();
            }
        });
    };
}
