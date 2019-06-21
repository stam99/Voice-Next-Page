package io.voxhub.accessibility.app;
import jp.naist.ahclab.speechkit.logs.MyLog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jp.naist.ahclab.speechkit.Recognizer;
import jp.naist.ahclab.speechkit.ServerInfo;
import jp.naist.ahclab.speechkit.SpeechKit;
import jp.naist.ahclab.speechkit.view.ListeningDialog;

import java.util.List;
import java.util.ArrayList;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.graphics.Color;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

public class SimpleActivity extends Activity implements Recognizer.Listener{

    final String TAG = "SimpleActivity";

    private AccessibilityManager manager;

    private Button btn_start;
    private Button btn_setting;
    private Button btn_stop;
    private Button btn_enable;
    private Button btn_about;
    private ProgressBar progress;
    private Overlay overlay = null;
    private EditText ed_result;
    private boolean requestListen = false;

    private static ServerInfo serverInfo = new ServerInfo();
    private static Recognizer _currentRecognizer;

    void init_speechkit(ServerInfo serverInfo){
        SpeechKit _speechKit = SpeechKit.initialize(getApplication().getApplicationContext(), "", "", serverInfo);
        _currentRecognizer = _speechKit.createRecognizer(SimpleActivity.this);
        _currentRecognizer.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("onCreate has been entered");
        manager = (AccessibilityManager)this.getSystemService(Context.ACCESSIBILITY_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_setting = (Button)findViewById(R.id.btn_setting);
        btn_stop = (Button) this.findViewById(R.id.btn_stop);
        btn_enable = (Button) this.findViewById(R.id.btn_enable);
        btn_about = (Button) this.findViewById(R.id.btn_about);
        progress = (ProgressBar)findViewById(R.id.progress_listening);
        ed_result = (EditText)findViewById(R.id.ed_result);
        serverInfo.setAddr(this.getResources().getString(R.string.default_server_addr));
        serverInfo.setPort(Integer.parseInt(this.getResources().getString(R.string.default_server_port)));
        serverInfo.setAppSpeech(this.getResources().getString(R.string.default_server_app_speech));
        serverInfo.setAppStatus(this.getResources().getString(R.string.default_server_app_status));

        init_speechkit(serverInfo);

//        btn_start.setText(manager.isEnabled() ? "Start listening" : "Tap to start service...");

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open settings page
                Intent intent = new Intent(SimpleActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        // Button start
        MyLog.i("onCreate - buttons made");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestListen = true;
                MyLog.i("Setting requestListen to " + requestListen);
                _currentRecognizer.start();
                overlay.show();
                progress.setVisibility(View.VISIBLE);
            }
        });
        
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestListen = false;
                MyLog.i("Setting requestListen to " + requestListen);
                _currentRecognizer.stopRecording();
                progress.setVisibility(View.INVISIBLE);
                overlay.hide();
            }
        });
        
        btn_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
        
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open about page
                Intent intent = new Intent(SimpleActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        MyLog.i("onCreate - OnClickListeners are made");

        btn_enable.setVisibility(manager.isEnabled() ? View.INVISIBLE : View.VISIBLE);
  
  //this one was already commented out *******_________dont uncomment_______*********
        /* Stops recording once dialog goes away
        lst_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                _currentRecognizer.stopRecording();
            }
        });*/
        
        // Open accessibility service
        overlay = Overlay.getInstance();
        if(overlay == null) overlay = new Overlay(this);
    }

    @Override
    public void onPartialResult(String result) {
        ed_result.setText(result);
        overlay.setText(result);
    }

    public void sendAccessibilityEvent(String string) {
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
        event.setClassName(getClass().getName());
        event.setPackageName(this.getPackageName());
        event.setEnabled(true);
        event.getText().clear();
        event.getText().add(string);
        event.getText().add(":");
        //MyLog.i("SimpleActivity event: " + event.toString());
        if(dispatchPopulateAccessibilityEvent(event)) {
            MyLog.i("SimpleActivity dispatchPopulateAccessibilityEvent says OK");
        }
        else {
            MyLog.i("SimpleActivity dispatchPopulateAccessibilityEvent says ???");
        }
        manager.sendAccessibilityEvent(event);
        MyLog.i("SimpleActivity sent accessibility event");
    }

    String filterText(String input) {
        String[] words = input.split(" ");
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for(String word : words) {
            if(word.charAt(0) == '[' && word.charAt(word.length()-1) == ']') continue;
            if(word.charAt(0) == '<' && word.charAt(word.length()-1) == '>') continue;
            if(sb.length() > 0) sb.append(" ");
            sb.append(word);
        }
        return sb.toString();
    }

    @Override
    public void onFinalResult(String result) {
        ed_result.setText(result + ".");
        overlay.setText(result + ".");

        String canonical = filterText(result);
        MyLog.i("SimpleActivity recognized [" + canonical + "]");

        if (canonical.equals("stop listening")) {
            MyLog.i("SimpleActivity spotted stop listening");
            //onFinish("stop command called");
            overlay.hide();
            progress.setVisibility(View.INVISIBLE);
            _currentRecognizer.stopRecording();
            requestListen = false;
            MyLog.i("Setting requestListen to " + requestListen);
        }

        if(!manager.isEnabled()) { // This will never be called bc start button
            ed_result.setText(result + "...[service not running]");

            MyLog.i("SimpleActivity manager not enabled");
            return;
        }

        if (canonical.equals("next page")) {
            MyLog.i("SimpleActivity spotted next page");
            sendAccessibilityEvent("next");
            MyLog.i("SimpleActivity sent next page");
        }
        if (canonical.equals("previous page")) {
            MyLog.i("SimpleActivity spotted previous page");
            sendAccessibilityEvent("previous");
            MyLog.i("SimpleActivity sent previous page");
        }
        if (canonical.equals("center")) {
            MyLog.i("SimpleActivity spotted center");
            sendAccessibilityEvent("center");
            MyLog.i("SimpleActivity sent center");
        }
        if (canonical.equals("unknowncommande")) {
            MyLog.i("SimpleActivity spotted ");
            overlay.hide();
            MyLog.i("SimpleActivity paused listening");
        }
    }

    @Override
    public void onFinish(String reason) {
        //overlay.destroy(); 
        _currentRecognizer.stopRecording();
        MyLog.i("SimpleActivity stopped listening");
    }

    @Override
    public void onReady(String reason) {
        btn_start.setEnabled(true);
    }

    @Override
    public void onNotReady(String reason) {
        btn_start.setEnabled(false);
        Toast.makeText(getApplicationContext(),"Server connected, but not ready, reason: "+reason,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateStatus(SpeechKit.Status status) {
      /*  Toast.makeText(getApplicationContext(),"Status changed: " + status.name(),
                Toast.LENGTH_SHORT).show();*/
        MyLog.i("SimpleActivity has new status: " + status.name());        
    }

    @Override
    public void onRecordingBegin() {
        ed_result.setText("");        
    }

    @Override
    public void onRecordingDone() {
        if (requestListen) {
            _currentRecognizer.start();
            MyLog.i("SimpleActivity restarted listening.");
        }
    }

    @Override
    public void onError(Exception error) {
        Toast.makeText(getApplicationContext(),"Error: " + error,
                Toast.LENGTH_SHORT).show();
        MyLog.i("SimpleActivity has error: " + error);
        
        for (StackTraceElement e : error.getStackTrace()) {
            MyLog.i("SimpleActivity stack trace: " + e.toString());
        }    
        _currentRecognizer.stopRecording();
        if (requestListen) {
            _currentRecognizer.start();
            MyLog.i("SimpleActivity restarted listening.");
        }
    }

    @Override
    public void onResume() {
        btn_enable.setVisibility(manager.isEnabled() ? View.INVISIBLE : View.VISIBLE);
        super.onResume();
    }
    
    public static ServerInfo getServerInfo() {
        return serverInfo;
    }
}
