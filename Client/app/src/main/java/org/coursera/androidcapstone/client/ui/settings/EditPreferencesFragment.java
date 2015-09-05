package org.coursera.androidcapstone.client.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import org.coursera.androidcapstone.client.R;

public class EditPreferencesFragment extends PreferenceFragment {
	public final static String LOG_TAG = EditPreferencesFragment.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.edit_preferences_fragment);
    }
}
