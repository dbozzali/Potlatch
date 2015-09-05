package org.coursera.androidcapstone.client.ui.gift;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class ViewGiftChainActivity extends UIActivityBase {
    private static final String LOG_TAG = ViewGiftChainActivity.class.getCanonicalName();

    private ViewGiftListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_giftchain_activity);
        if (savedInstanceState == null) {
            long id = getIntent().getExtras().getLong(ViewGiftListFragment.rowIdentifyerTAG);
            fragment = ViewGiftListFragment.newInstance(id, true);
            String viewGiftChainFragmentTag = "viewGiftChainFragmentTag";
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, viewGiftChainFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
    }
}
