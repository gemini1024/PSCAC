package com.example.ihc.proto_odroid_new;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by ihc on 2017-01-31.
 */
public class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context context) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
    }
    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
    }
}
