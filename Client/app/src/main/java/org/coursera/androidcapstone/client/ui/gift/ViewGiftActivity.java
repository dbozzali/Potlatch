package org.coursera.androidcapstone.client.ui.gift;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class ViewGiftActivity extends UIActivityBase {
    private static final String LOG_TAG = ViewGiftActivity.class.getCanonicalName();

    private ViewGiftFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);

        setContentView(R.layout.view_gift_activity);
        if (savedInstanceState == null) {
            long id = getIntent().getExtras().getLong(ViewGiftFragment.rowIdentifyerTAG);
            fragment = ViewGiftFragment.newInstance(id);
            String viewGiftFragmentTag = "viewGiftFragmentTag";
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, viewGiftFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
	}
}
