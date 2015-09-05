package org.coursera.androidcapstone.client.ui.gift;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class ViewTopGiftGiversActivity extends UIActivityBase {
    private static final String LOG_TAG = ViewTopGiftGiversActivity.class.getCanonicalName();

	private ViewTopGiftGiversFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_top_gift_givers_activity);
        if (savedInstanceState == null) {
            fragment = new ViewTopGiftGiversFragment();
			String viewTopGiftGiversFragmentTag = "viewTopGiftGiversFragmentTag";
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, viewTopGiftGiversFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
    }
}
