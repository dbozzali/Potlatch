package org.coursera.androidcapstone.client.globals;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.updater.GiftUpdater;

public class PotlatchApp extends Application {
    private static final String LOG_TAG = PotlatchApp.class.getName();

	private String username;
    private boolean updaterTimerIsRunning = false;
    private SharedPreferences mPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener =
        new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(LOG_TAG, "SharedPreferences.onSharedPreferenceChanged()");
                if (key.equals("gifts_sync_frequency")) {
                    stopUpdaterTimer();
                    startUpdaterTimer();
                }
            }
        };

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate()");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(mListener);

        PreferenceManager.setDefaultValues(this, R.xml.edit_preferences_fragment, false);

        startUpdaterTimer();
    }

	public synchronized String getUsername() {
		return this.username;
	}
	public synchronized void setUsername(String username) {
		this.username = username;
	}

    private synchronized boolean getUpdaterTimerIsRunning() {
        return this.updaterTimerIsRunning;
    }

    private synchronized void setUpdaterTimerIsRunning(boolean isRunning) {
        this.updaterTimerIsRunning = isRunning;
    }

    public synchronized void startUpdaterTimer() {
        Log.d(LOG_TAG, "startUpdaterTimer()");
        if (getUpdaterTimerIsRunning()) {
            return;
        }
        Context context = getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GiftUpdater.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int intervalInMinutes = Integer.parseInt(preferences.getString("gifts_sync_frequency", "60"));
        final long intervalInMs = intervalInMinutes * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + intervalInMs, intervalInMs, pendingIntent);
        setUpdaterTimerIsRunning(true);
    }

    public synchronized void stopUpdaterTimer() {
        Log.d(LOG_TAG, "stopUpdaterTimer()");
        if (!getUpdaterTimerIsRunning()) {
            return;
        }
        Context context = getApplicationContext();
        Intent intent = new Intent(context, GiftUpdater.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        setUpdaterTimerIsRunning(false);
    }
}
