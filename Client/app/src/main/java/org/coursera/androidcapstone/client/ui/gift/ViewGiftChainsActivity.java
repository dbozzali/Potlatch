package org.coursera.androidcapstone.client.ui.gift;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class ViewGiftChainsActivity extends UIActivityBase {
    private static final String LOG_TAG = ViewGiftChainsActivity.class.getCanonicalName();

    private ViewGiftListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_giftchains_activity);
        if (savedInstanceState == null) {
            fragment = ViewGiftListFragment.newInstance(0L, false);
            String viewGiftChainsFragmentTag = "viewGiftChainsFragmentTag";
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, viewGiftChainsFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
    }
}
