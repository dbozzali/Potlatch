package org.coursera.androidcapstone.client.ui.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.coursera.androidcapstone.client.R;

import java.util.List;

public class EditPreferencesActivity extends PreferenceActivity {
	public final static String LOG_TAG = EditPreferencesActivity.class.getCanonicalName();

    private EditPreferencesFragment fragment;

	@Override
	public void onBuildHeaders(List<Header> target) {
        Log.d(LOG_TAG, "onBuildHeaders()");
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            fragment = new EditPreferencesFragment();
            String editPreferencesFragmentTag = "editPreferencesFragmentTag";
            getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment, editPreferencesFragmentTag)
                .commit();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected boolean isValidFragment(String fragmentName) {
        Log.d(LOG_TAG, "isValidFragment()");
        if (EditPreferencesActivity.class.getName().equals(fragmentName) ||
            EditPreferencesFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }
}
