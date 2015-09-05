package org.coursera.androidcapstone.client.ui.gift;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.ui.common.UIActivityBase;

public class CreateGiftChainActivity extends UIActivityBase {
    private final static String LOG_TAG = CreateGiftChainActivity.class.getCanonicalName();

	private CreateGiftFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);

        setContentView(R.layout.create_giftchain_activity);
        if (savedInstanceState == null) {
            long id = getIntent().getExtras().getLong(CreateGiftFragment.rowIdentifyerTAG);
            fragment = CreateGiftFragment.newInstance(id, true);
            String createGiftChainFragmentTag = "createGiftChainFragmentTag";
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment, createGiftChainFragmentTag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
	}
}
