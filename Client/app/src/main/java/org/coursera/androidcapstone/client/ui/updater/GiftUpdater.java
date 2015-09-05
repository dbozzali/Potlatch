package org.coursera.androidcapstone.client.ui.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GiftUpdater extends BroadcastReceiver {
    public final static String LOG_TAG = GiftUpdater.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive()");

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(new Intent("FragmentUpdater"));
    }
}
