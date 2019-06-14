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
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("accessibilityservice", "entering onAccessibilityEvent");
        Log.i("accessibilityservice", "got event: " + event.toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Log.i("accessibilityservice", "got click event");
           // AccessibilityNodeInfo currentNode = getRootInActiveWindow();
        // if (event.getText().equals("next") -->
           // currentNode.performAction(AccessibilityNodeInfo.ACTION_PAGE_RIGHT);
           // currentNode.performAction(AccessibilityNodeInfo.ACTION_PAGE_LEFT);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

            int middleYValue = 1000;//displayMetrics.heightPixels / 2;
            final int leftSideOfScreen = 100;//displayMetrics.widthPixels / 4;
            final int rightSizeOfScreen = 1000;//leftSideOfScreen * 3;
            //final int leftSideOfScreen = 1;
            //final int rightSizeOfScreen = displayMetrics.widthPixels - 2;
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            Path path = new Path();
            
            StringBuilder sb = new StringBuilder();
            for (CharSequence ch : event.getText()) {
                sb.append(ch.toString());
            }

            String text = sb.toString();
            Log.i("accessibilityservice", "text is [" + text + "]");
            if (text.equals("next")) {
                //Swipe left
                //path.moveTo(rightSizeOfScreen, middleYValue);
                //path.lineTo(leftSideOfScreen, middleYValue);
                path.moveTo(rightSizeOfScreen, middleYValue);
                Log.i("accessibilityservice", "swiped left");
            } 
            else if (text.equals("previous")) {
                 //Swipe right
                //path.moveTo(leftSideOfScreen, middleYValue);
                //path.lineTo(rightSizeOfScreen, middleYValue);
                path.moveTo(leftSideOfScreen, middleYValue);
                Log.i("accessibilityservice", "swiped right");
            }
            else {
                return;
            }
            Log.w("accessibilityservice", "Gesture Started");
            //gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1000));
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    Log.w("accessibilityservice", "Gesture Completed");
                    super.onCompleted(gestureDescription);
                }
            }, null);
        }
    }
    @Override
    public void onInterrupt() {
    
    }
}

