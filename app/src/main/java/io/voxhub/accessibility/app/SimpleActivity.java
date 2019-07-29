package io.voxhub.accessibility.app;
import jp.naist.ahclab.speechkit.logs.MyLog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.graphics.Color;
import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat; 
import android.support.v4.content.ContextCompat;
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

public class SimpleActivity extends Activity {

    final String TAG = "SimpleActivity";

    private AccessibilityManager manager;

    private Button btn_start;
   // private Button btn_setting;
    private ImageButton btn_setting;
    private Button btn_stop;
    private Button btn_enable;
    private Button btn_overlay;
    private Button btn_about;
   // private Button btn_help;
    private ImageButton btn_help;
    private ProgressBar progress;
    private Overlay overlay = null;
    private EditText ed_result;
    private boolean requestListen = false;
    private boolean askedForOverlayPermission;

    private /*static*/ ServerInfo serverInfo; 
    private /*static*/ Recognizer _currentRecognizer;
    private /*static*/ ThreadAdapter _currentListener;

    void init_speechkit(ServerInfo serverInfo){
        SpeechKit _speechKit = SpeechKit.initialize(getApplication().getApplicationContext(), "", "", serverInfo);
        _currentListener = new ThreadAdapter(new SpeechkitCode());
        _currentRecognizer = _speechKit.createRecognizer(_currentListener);
        _currentRecognizer.connect();
    }

    void make_speechkit() {
        if (!makeServerInfo()) return;
        
        destroy_speechkit();
        requestMicPermissions();
    }

    private boolean haveMicPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED;
    }

    void destroy_speechkit() {
        if (_currentRecognizer != null) {
            stopListening();
            _currentRecognizer.shutdownThreads();
           // _currentRecognizer.cancel();
            _currentRecognizer = null;
        }
        if (_currentListener != null) {
            _currentListener.stop();
            _currentListener = null;
        }
    }

    void requestMicPermissions(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;  // how do we handle this case?

        if(haveMicPermissions()) {
            init_speechkit(serverInfo);
        }
        else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 101){
            boolean granted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if(granted) {
                init_speechkit(serverInfo);
            }
            else {
                requestMicPermissions();  // infinite loop
                Toast.makeText(this,
                    "App requires audio permissions", Toast.LENGTH_LONG).show();
                //finishAffinity();  // exit app
            }
        }
    }

    public boolean makeServerInfo() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String newserver = pref.getString("server", "silvius-server.voxhub.io");
        int newport = Integer.parseInt(pref.getString("port", "8022"));
        if (serverInfo == null 
            || serverInfo.getAddr() != newserver
            || serverInfo.getPort() != newport) {

            serverInfo = new ServerInfo(newserver, newport);
            serverInfo.setAppSpeech(this.getResources().getString(
                R.string.default_server_app_speech));
            serverInfo.setAppStatus(this.getResources().getString(
                R.string.default_server_app_status));
            return true;
        }
        else return false;
    }
    
    public void makeOverlay(boolean enabled) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
        if(!Settings.canDrawOverlays(this)) return;

        overlay = Overlay.getInstance();
        if(overlay == null) overlay = new Overlay(this);

        if(enabled && requestListen) overlay.show();
        else overlay.hide();
    }

    public void makeOverlay() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        makeOverlay(pref.getBoolean("overlay_enabled", true));
    }

    private void updateOverlayUI() {
        makeOverlay();
        btn_overlay.setVisibility(Settings.canDrawOverlays(this) ? View.GONE : View.VISIBLE);
    }

    private void startListening() {
        requestListen = true;
        MyLog.i("Setting requestListen to " + requestListen);
        _currentRecognizer.start();
        makeOverlay();
        progress.setVisibility(View.VISIBLE);
    }

    private void stopListening() {
        requestListen = false;
        MyLog.i("Setting requestListen to " + requestListen);
        _currentRecognizer.stopRecording();
        makeOverlay();
        progress.setVisibility(View.INVISIBLE);
    }

    private void updateState() {
        btn_enable.setVisibility(manager.isEnabled() ? View.GONE : View.VISIBLE); 
        updateOverlayUI();
    }

    boolean VersionUpgraded() {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            MyLog.i("current version: [" + version + "]");
            if (!(pref.getString("currentVersion", version).equals(version))) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("currentVersion", version);
                editor.apply();
                return true;
            }
            else return false;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false; // should this be set to t or f ???
        }
    }

        private void bringApplicationToBackground() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
        private void bringApplicationToForeground() {
            ActivityManager am =
                (ActivityManager) getSystemService(SimpleActivity.this.ACTIVITY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.AppTask> tasksList = am.getAppTasks();
                for (ActivityManager.AppTask task : tasksList){
                  task.moveToFront();
                }
            }
            else{
                List<ActivityManager.RunningTaskInfo> tasksList =
                    am.getRunningTasks(Integer.MAX_VALUE);
                if(!tasksList.isEmpty()){
                    int nSize = tasksList.size();
                    for(int i = 0; i < nSize;  i++){
                        if(tasksList.get(i).topActivity.getPackageName()
                            .equals(getPackageName())){
                            
                            am.moveTaskToFront(tasksList.get(i).id, 0);
                        }
                    }
                }
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("onCreate has been entered");
        manager = (AccessibilityManager)this.getSystemService(Context.ACCESSIBILITY_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_setting = (ImageButton)findViewById(R.id.btn_setting);
   //     btn_setting = (Button)findViewById(R.id.btn_setting);
        btn_stop = (Button) this.findViewById(R.id.btn_stop);
        btn_enable = (Button) this.findViewById(R.id.btn_enable);
        btn_overlay = (Button) this.findViewById(R.id.btn_overlay); 
        btn_about = (Button) this.findViewById(R.id.btn_about);
   //      btn_help = (Button) this.findViewById(R.id.btn_help);
         btn_help = (ImageButton) this.findViewById(R.id.btn_help);
        progress = (ProgressBar)findViewById(R.id.progress_listening);
        ed_result = (EditText)findViewById(R.id.ed_result);
        /*serverInfo.setAddr(this.getResources().getString(R.string.default_server_addr));
        serverInfo.setPort(Integer.parseInt(this.getResources().getString(R.string.default_server_port)));
        serverInfo.setAppSpeech(this.getResources().getString(R.string.default_server_app_speech));
        serverInfo.setAppStatus(this.getResources().getString(R.string.default_server_app_status));*/

        //init_speechkit(serverInfo);

        make_speechkit();

        if (VersionUpgraded())
            new AlertDialog.Builder(SimpleActivity.this)
                .setTitle("Re-enable Accessibility Settings")
                .setMessage("You have recently updated this app. " +
                    "If the service was previously malfunctioning, you may wish to reenable it.")
                .setPositiveButton("Re-enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                            android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Dismiss", null)
                .show();

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open settings page
                Intent intent = new Intent(SimpleActivity.this, SettingsActivity.class);
                startActivity(intent);
                MyLog.i("SettingsActivity intent started");
            }
        });

        // Button start
        MyLog.i("onCreate - buttons made");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
                if (btn_enable.getVisibility() == View.VISIBLE)
                    new AlertDialog.Builder(SimpleActivity.this)
                        .setTitle("Warning")
                        .setMessage("Accessibility Settings have not been enabled")
                        .setPositiveButton("close", null)
                        .show();
                SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(SimpleActivity.this);
                if(pref.getBoolean("autoBackground", true)) {
                    btn_start.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                bringApplicationToBackground();
                            }
                        }, 500);
                    Toast.makeText(getApplicationContext(),
                        "Please launch e-book reader",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
            }
        });
        
        btn_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider
                    .Settings.ACTION_ACCESSIBILITY_SETTINGS);
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

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Open about page
                Intent intent = new Intent(SimpleActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        /* Stops recording once dialog goes away
        lst_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                _currentRecognizer.stopRecording();
            }
        });*/

        btn_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, 
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 8888);
            }
        });

        MyLog.i("onCreate - OnClickListeners are made");

        updateState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        MyLog.i("requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            MyLog.i("can draw overlays: " + (Settings.canDrawOverlays(this)));
            final SimpleActivity outer = this;
            btn_overlay.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        outer.updateOverlayUI();
                    }
                }, 500);

            if(resultCode == RESULT_OK){
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(getApplicationContext(),
                        "ACTION_MANAGE_OVERLAY_PERMISSION Permission Denied",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void sendAccessibilityEvent(String string) {
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent
            .TYPE_ANNOUNCEMENT);
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
    public void onDestroy() {
       // _currentListener.stop();
       // _currentRecognizer.shutdownThreads();
        destroy_speechkit();
        MyLog.i("SimpleActivity stopped listening");
        if(Overlay.getOverlayExists()) {
            Overlay.getInstance().hide();  // destroy?
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        make_speechkit();
        updateState();

        MyLog.i("onResume called");

        super.onResume();
    }

    public /*static*/ ServerInfo getServerInfo() {
        return serverInfo;
    }

    //--- inner class ~ SpeechkitCode ---//
    class SpeechkitCode implements Recognizer.Listener {

        @Override
        public void onReady(String reason) {
            btn_start.setEnabled(true);
            Toast.makeText(getApplicationContext(),"Connected to server: "+reason,
                Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNotReady(String reason) {
            btn_start.setEnabled(false);
            Toast.makeText(getApplicationContext(),
                "Server connected, but not ready, reason: "+reason,
                Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpdateStatus(SpeechKit.Status status) {
          /*  Toast.makeText(getApplicationContext(),"Status changed: " + status.name(),
                    Toast.LENGTH_SHORT).show();*/
            MyLog.i("SimpleActivity has new status: " + status.name());        
        }

        @Override
        public void onFinalResult(String result) {
            ed_result.setText(result + ".");
            if (Overlay.getOverlayExists())
                overlay.setText(result + ".");

            String canonical = filterText(result);
            MyLog.i("SimpleActivity recognized [" + canonical + "]");

            if (canonical.equals("stop listening")) {
                MyLog.i("SimpleActivity spotted stop listening");
                //onFinish("stop command called");
                stopListening();
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
            if (canonical.equals("foreground")) {
                MyLog.i("SimpleActivity spotted foreground");
                bringApplicationToForeground();
                MyLog.i("SimpleActivity sent foreground");
            }
            if (canonical.equals("background")) {
                MyLog.i("SimpleActivity spotted background");
                bringApplicationToBackground();
                MyLog.i("SimpleActivity sent background");
            }
            if (canonical.equals("unknowncommande")) {
                MyLog.i("SimpleActivity spotted ");
                if(Overlay.getOverlayExists())
                    overlay.hide();
                MyLog.i("SimpleActivity paused listening");
            }
        }

        @Override
        public void onFinish(String reason) {
            _currentRecognizer.stopRecording();
            MyLog.i("SimpleActivity stopped listening");
        }

        @Override
        public void onPartialResult(String result) {
            ed_result.setText(result);
            if(Overlay.getOverlayExists())
                overlay.setText(result);
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
    //        Toast.makeText(getApplicationContext(),"Error: " + error,
    //                Toast.LENGTH_SHORT).show();
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
    }

    class ThreadAdapter implements Recognizer.Listener {
        Recognizer.Listener realCode;  // accessed from main UI thread only

        public ThreadAdapter(Recognizer.Listener realCode) {
            this.realCode = realCode;
        }

        public void stop() {
            this.realCode = null;  // no need for synchronized
        }

        @Override
        public void onReady(final String reason) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onReady(reason);
                }
            });}

        @Override
        public void onRecordingBegin(){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onRecordingBegin();
                }
            });}
 
        @Override       
        public void onRecordingDone(){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onRecordingDone();
                }
            });}
 
        @Override       
        public void onError(final Exception error){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onError(error);
                }
            });}
 
        @Override       
        public void onPartialResult(final String result){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onPartialResult(result);
                }
            });}
 
        @Override       
        public void onFinalResult(final String result){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onFinalResult(result);
                }
            });}
 
        @Override       
        public void onFinish(final String reason){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onFinish(reason);
                }
            });}
 
        @Override       
        public void onNotReady(final String reason){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onNotReady(reason);
                }
            });}
 
        @Override       
        public void onUpdateStatus(final SpeechKit.Status status){
            runOnUiThread(new Runnable() {
                public void run() {
                    if(realCode != null) realCode.onUpdateStatus(status);
                }
            });}
    }
}
