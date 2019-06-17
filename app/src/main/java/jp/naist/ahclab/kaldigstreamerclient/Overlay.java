package jp.naist.ahclab.kaldigstreamerclient;
import jp.naist.ahclab.speechkit.logs.MyLog;
import android.view.View;
import android.view.ViewParent;

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

public class Overlay {
    private TextView tv;
    private LinearLayout ll;
    private WindowManager windowManager;

    public Overlay(Activity act) {
        int statusBarHeight = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) statusBarHeight = act.getResources().getDimensionPixelSize(resourceId);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                statusBarHeight,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,   // Allows the view to be on top of the StatusBar
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
        tv.setText("Initializing ...");
        ll.addView(tv);

        windowManager = (WindowManager) act.getSystemService(act.WINDOW_SERVICE);
        windowManager.addView(ll, parameters);
    }

    public void destroy() {
        windowManager.removeViewImmediate(ll);
    }
    
    public void setText(String result) {
        tv.setText(result);
    }
    
}
