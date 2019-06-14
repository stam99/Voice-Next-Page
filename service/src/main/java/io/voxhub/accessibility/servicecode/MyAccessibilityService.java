package io.voxhub.accessibility.servicecode;
//package com.example.android.apis.accessibility;

import android.util.Log;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.DisplayMetrics;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;

public class MyAccessibilityService extends AccessibilityService {
    public MyAccessibilityService() {
        Log.i("accessibilityservice", "constructor");
    }
   
    @Override
    public void onServiceConnected() {
        Log.i("accessibilityservice", "service connected");
    }

    private String getTextFor(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence ch : event.getText()) {
            sb.append(ch.toString());
        }

        String text = sb.toString();
        Log.d("accessibilityservice", "split event text ["
            + text + "] -> [" + text.split(":")[0] + "]");
        return text.split(":")[0];
    }

    private enum GestureType {
        GESTURE_TAP_LEFT_SIDE,
        GESTURE_TAP_RIGHT_SIDE,
        GESTURE_TAP_CENTER,
    }

    private void doGesture(GestureType type) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final int BORDER = 10;
        final int leftX = BORDER;
        final int midX = displayMetrics.widthPixels / 2;
        final int rightX = displayMetrics.widthPixels - BORDER;
        final int topY = displayMetrics.heightPixels / 4;
        final int midY = displayMetrics.heightPixels / 2;
        final int botY = displayMetrics.heightPixels - BORDER;
        final int GESTURE_DURATION = 100;

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        Path path = new Path();
        switch(type) {
        case GESTURE_TAP_LEFT_SIDE: 
            path.moveTo(leftX, midY);
            Log.i("accessibilityservice", "gesture: tap left side");
            break;
        case GESTURE_TAP_RIGHT_SIDE: 
            path.moveTo(rightX, midY);
            Log.i("accessibilityservice", "gesture: tap right side");
            break;
        case GESTURE_TAP_CENTER:
            path.moveTo(midX, midY);
            Log.i("accessibilityservice", "gesture: tap center");
            break; 
        default:
            Log.i("accessibilityservice", "gesture: unsupported gesture");
            return;
        } 

        Log.d("accessibilityservice", "gesture started");
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
            path, 0, GESTURE_DURATION));
        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                Log.d("accessibilityservice", "gesture completed");
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("accessibilityservice", "onAccessibilityEvent called, event: " + event.toString());
        if(event.getEventType() == AccessibilityEvent.TYPE_ANNOUNCEMENT) {
            Log.i("accessibilityservice", "got announce");
            String text = getTextFor(event);
            Log.i("accessibilityservice", "announce event text is [" + text + "]");

            if(text.equals("next")) {
                doGesture(GestureType.GESTURE_TAP_RIGHT_SIDE);
            }
            else if(text.equals("previous")) {
                doGesture(GestureType.GESTURE_TAP_LEFT_SIDE);
            }
            else if(text.equals("center")) {
                doGesture(GestureType.GESTURE_TAP_CENTER);
            } 
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Log.i("accessibilityservice", "got click event");
            String text = getTextFor(event);
            Log.i("accessibilityservice", "click event text is [" + text + "]");
        }
    }
    @Override
    public void onInterrupt() {
    
    }
}

