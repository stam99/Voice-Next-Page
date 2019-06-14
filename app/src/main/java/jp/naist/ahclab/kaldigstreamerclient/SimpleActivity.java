package jp.naist.ahclab.kaldigstreamerclient;
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


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager; 

public class SimpleActivity extends Activity implements Recognizer.Listener{

    final String TAG = "SimpleActivity";

    private ListeningDialog lst_dialog;
    private Button btn_start;
    private EditText ed_result;
    private Button btn_stop;
    private Button btn_fake;

    protected ServerInfo serverInfo = new ServerInfo();
    Recognizer _currentRecognizer;

    void init_speechkit(ServerInfo serverInfo){
        SpeechKit _speechKit = SpeechKit.initialize(getApplication().getApplicationContext(), "", "", serverInfo);
        _currentRecognizer = _speechKit.createRecognizer(SimpleActivity.this);
        _currentRecognizer.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);

        lst_dialog = new ListeningDialog(SimpleActivity.this);
        btn_start = (Button)findViewById(R.id.btn_start);
        ed_result = (EditText)findViewById(R.id.ed_result);
        btn_fake = (Button)findViewById(R.id.btn_fake);
        btn_stop = (Button) this.findViewById(R.id.btn_listeningStop);
        serverInfo.setAddr(this.getResources().getString(R.string.default_server_addr));
        serverInfo.setPort(Integer.parseInt(this.getResources().getString(R.string.default_server_port)));
        serverInfo.setAppSpeech(this.getResources().getString(R.string.default_server_app_speech));
        serverInfo.setAppStatus(this.getResources().getString(R.string.default_server_app_status));

        init_speechkit(serverInfo);

        btn_fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAccessibilityEvent("fake button");
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _currentRecognizer.start();
                lst_dialog.show();
            }
        });

        Button.OnClickListener stop_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _currentRecognizer.stopRecording();
            }
        };
        // Stops recording once dialog goes away
        lst_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                _currentRecognizer.stopRecording();
            }
        });    
        lst_dialog.prepare(stop_listener);
    }

    @Override
    public void onPartialResult(String result) {
        ed_result.setText(result);
    }

    public void sendAccessibilityEvent(String string) { 
        View view = btn_fake;
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_CLICKED);
        AccessibilityManager manager
            = (AccessibilityManager)btn_fake.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
       // MyLog.i("accessibilityservice spotted next page"); 
       // event.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
        event.setClassName(getClass().getName());
        event.setSource(btn_fake);
        event.setEnabled(true);
        event.getText().clear();
        event.getText().add(string);
        event.setPackageName(this.getPackageName());
        MyLog.i("accessibilityservice event: " + event.toString()); 
        manager.sendAccessibilityEvent(event);
        MyLog.i("accessibilityservice sent accessibility event"); 
        MyLog.i("accessibilityservice event after: " + event.toString()); 
    }
    @Override
    public void onFinalResult(String result) {
        ed_result.setText(result);
       // View view = btn_fake;
        //AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_CLICKED);
        AccessibilityManager manager = (AccessibilityManager)this.getSystemService(Context.ACCESSIBILITY_SERVICE);
        sendAccessibilityEvent("test");

        if (result.equals("next page") && manager.isEnabled()) {
            //sendAccessibilityEvent();
    /*        AccessibilityEvent event = AccessibilityEvent.obtain();
            view.onInitializeAccessibilityEvent(event);
            MyLog.i("accessibilityservice spotted next page"); 
            event.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
           // event.setClassName(getClass().getName());
            event.getText().clear();
            event.getText().add("next");
            view.getParent().requestSendAccessibilityEvent(view, event);
            MyLog.i("accessibilityservice sent next page");*/
            MyLog.i("accessibilityservice spotted next page"); 
            sendAccessibilityEvent("next");
            MyLog.i("accessibilityservice sent next page");
        }
        if (result.equals("previous page") && manager.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            MyLog.i("accessibilityservice spotted previous page"); 
            event.setEventType(AccessibilityEvent.TYPE_VIEW_CLICKED);
            event.setClassName(getClass().getName());
            event.getText().clear();
            event.getText().add("previous");
            MyLog.i("accessibilityservice event: " + event.toString());
            manager.sendAccessibilityEvent(event);
            MyLog.i("accessibilityservice sent previous page");
            MyLog.i("accessibilityservice event after: " + event.toString());
        }
    }

    @Override
    public void onFinish(String reason) {
        if (lst_dialog.isShowing())
            lst_dialog.dismiss();
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

    }

    @Override
    public void onRecordingBegin() {
        lst_dialog.setText("Listening");
    }

    @Override
    public void onRecordingDone() {
        lst_dialog.setText("Please wait!");
    }

    @Override
    public void onError(Exception error) {

    }

}
