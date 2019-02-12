package com.ddx.hacknroll;

import android.accessibilityservice.AccessibilityService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    public static final int BACK = 1;
    public static final int HOME = 2;
    public static final int RECENT = 3;
    public static final int NOTICE = 4;
    @Override
    public void onCreate() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        super.onCreate();
        //use EventBus to replace broadcast
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() {}

    @Subscribe
    public void onReceive(Integer action){
        switch (action){
            case BACK:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case HOME:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case RECENT:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case NOTICE:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
        }
    }

}
