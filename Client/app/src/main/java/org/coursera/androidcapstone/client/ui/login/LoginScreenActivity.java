package org.coursera.androidcapstone.client.ui.login;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class LoginScreenActivity extends UIActivityBase {
    private static final String LOG_TAG = LoginScreenActivity.class.getName();

    private LoginScreenFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        promptOnBackPressed = true;
        setContentView(R.layout.login_screen_activity);
        if (savedInstanceState == null) {
            fragment = new LoginScreenFragment();
            String viewLoginScreenFragmentTag = "viewLoginScreenFragmentTag";
            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, viewLoginScreenFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
	}

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
