package com.ddx.hacknroll;

import android.accessibilityservice.AccessibilityService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
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
        Vibrator vibrator;
        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        long[] mVibratePattern = new long[]{10, 10, 10, 10};
        switch (action){
            case BACK:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

                vibrator.vibrate(mVibratePattern,-1);
                break;
            case HOME:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                vibrator.vibrate(mVibratePattern,-1);
                break;
            case RECENT:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                vibrator.vibrate(100);
                break;
            case NOTICE:
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                vibrator.vibrate(100);
                break;
        }
    }

}
