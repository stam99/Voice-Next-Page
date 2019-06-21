package io.voxhub.accessibility.app;
import jp.naist.ahclab.speechkit.logs.MyLog;
import android.view.View;
import android.view.ViewParent;

import android.content.Context;
import android.content.Intent; 
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.os.Handler;
import android.os.Build.VERSION;

public class Overlay {
    private TextView tv;
    private LinearLayout ll;
    private WindowManager windowManager;
    private Handler handler;
    private Runnable hideCallback;
    private static Overlay overlay_instance;
    private boolean visible = true;
   // int LAYOUT_FLAG;

    public Overlay(Activity act) {
        Log.i("overlay" , "overlay constructor");

        //request permission at runtime
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        myIntent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(myIntent, APP_PERMISSIONS);

        overlay_instance = this;
        int statusBarHeight = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) statusBarHeight = act.getResources().getDimensionPixelSize(resourceId);

       // int LAYOUT_FLAG;
     //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       //      LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
      //  } else {
      //       LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
      //  }

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                statusBarHeight,
                //WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,   // Allows the view to be on top of the StatusBar
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,    // Keeps the button presses from going to the background window and Draws over status bar
                PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.TOP | Gravity.CENTER;

        ll = new LinearLayout(act);
        ll.setBackgroundColor(Color.argb(128, 0, 0, 0));
        LinearLayout.LayoutParams layoutParameteres = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(layoutParameteres);

        tv = new TextView(act);
        ViewGroup.LayoutParams tvParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(tvParameters);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);

        windowManager = (WindowManager) act.getSystemService(act.WINDOW_SERVICE);
        windowManager.addView(ll, parameters);

        handler = new Handler();
        hideCallback = new Runnable() {
            @Override
            public void run() {
                ll.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }
        };
        hideCallback.run();
    }
    
    public static Overlay getInstance() {
        return overlay_instance;
    }

    public void show() {
        handler.removeCallbacks(hideCallback);
        ll.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        visible = true;
    }

    public void hide() {
        handler.postDelayed(hideCallback, 1000);
        visible = false;
    // windowManager.removeView(ll);
    }

    public Overlay getOverlay() {
        return this;
    } 

    public boolean isVisible() {
        return visible;
    }

    public void setText(String result) {
        tv.setText(result);
    }
    
}
