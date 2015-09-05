package org.coursera.androidcapstone.client.ui.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.globals.PotlatchApp;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.gift.CreateGiftChainActivity;
import org.coursera.androidcapstone.client.ui.gift.CreateGiftFragment;
import org.coursera.androidcapstone.client.ui.gift.CreateGiftInChainActivity;
import org.coursera.androidcapstone.client.ui.gift.ViewGiftActivity;
import org.coursera.androidcapstone.client.ui.gift.ViewGiftChainActivity;
import org.coursera.androidcapstone.client.ui.gift.ViewGiftChainsActivity;
import org.coursera.androidcapstone.client.ui.gift.ViewGiftFragment;
import org.coursera.androidcapstone.client.ui.gift.ViewGiftListFragment;
import org.coursera.androidcapstone.client.ui.gift.ViewTopGiftGiversActivity;
import org.coursera.androidcapstone.client.ui.login.LoginScreenActivity;
import org.coursera.androidcapstone.client.ui.settings.EditPreferencesActivity;

public class UIActivityBase extends FragmentActivity implements OnOpenWindowInterface {
	private static final String LOG_TAG = UIActivityBase.class.getCanonicalName();

	protected boolean promptOnBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gift_client_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                return onMenuItemSettings();

            case R.id.menu_item_view_top_gift_givers:
                return onMenuItemViewTopGiftGivers();

            case R.id.menu_item_logout:
                return onMenuItemLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onMenuItemSettings() {
        Log.d(LOG_TAG, "onMenuItemSettings()");
        OnOpenWindowInterface opener = this;
        opener.openEditPreferencesFragment();
        return true;
    }

    private boolean onMenuItemViewTopGiftGivers() {
        Log.d(LOG_TAG, "onMenuItemViewTopGiftGivers()");
        OnOpenWindowInterface opener = this;
        opener.openViewTopGiftGiversFragment();
        return true;
    }

    private boolean onMenuItemLogout() {
        Log.d(LOG_TAG, "onMenuItemLogout()");
        GiftSvc.logout();
        PotlatchApp potlatchApp = (PotlatchApp) getApplication();
        potlatchApp.setUsername("");
        OnOpenWindowInterface opener = this;
        opener.openLoginScreenFragment();
        return true;
    }

	@Override
	public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed()");

		if (promptOnBackPressed) {
			new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(getString(R.string.dialog_alert_title_closing_activity))
					.setMessage(getString(R.string.dialog_alert_message_confirm_close))
					.setPositiveButton(getString(R.string.dialog_alert_choice_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                    .setNegativeButton(getString(R.string.dialog_alert_choice_no), null)
                    .show();
		}
		else {
			super.onBackPressed();
		}
	}
    
    public void openLoginScreenFragment() {
        Log.d(LOG_TAG, "openLoginScreenFragment()");
        
        Intent intent = newLoginScreenIntent(this);
        startActivity(intent);
    }
    
    public void openViewGiftChainsFragment() {
        Log.d(LOG_TAG, "openViewGiftChainsFragment()");

        Intent intent = newViewGiftChainsIntent(this);
        startActivity(intent);
    }
    
    public void openViewGiftChainFragment(long id) {
        Log.d(LOG_TAG, "openViewGiftChainFragment(" + id + ")");

        Intent intent = newViewGiftChainIntent(this, id);
        startActivity(intent);
    }

    public void openViewGiftFragment(long id) {
        Log.d(LOG_TAG, "openViewGiftFragment(" + id + ")");

        Intent intent = newViewGiftIntent(this, id);
        startActivity(intent);
    }
    
    public void openCreateGiftChainFragment() {
        Log.d(LOG_TAG, "openCreateGiftChainFragment()");

        Intent intent = newCrateGiftChainIntent(this);
        startActivity(intent);
    }
    
    public void openCreateGiftInChainFragment(long id) {
        Log.d(LOG_TAG, "openCreateGiftInChainFragment(" + id + ")");

        Intent intent = newCreateGiftInChainIntent(this, id);
        startActivity(intent);
    }
    
    public void openViewTopGiftGiversFragment() {
        Log.d(LOG_TAG, "openViewTopGiftGiversFragment()");

        Intent intent = newViewTopGiftGiversIntent(this);
        startActivity(intent);
    }
    
    public void openEditPreferencesFragment() {
        Log.d(LOG_TAG, "openEditPreferencesFragment()");
        
        Intent intent = newEditPreferencesIntent(this);
        startActivity(intent);
    }

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityResult()");
        getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder)
                                   .onActivityResult(requestCode, resultCode, data);
	}

    protected Intent newLoginScreenIntent(Activity activity) {
        Log.d(LOG_TAG, "newLoginScreenIntent()");
        Intent intent = new Intent();
        intent.setClass(activity, LoginScreenActivity.class);
        return intent;
    }

    protected static Intent newViewGiftChainsIntent(Activity activity) {
        Log.d(LOG_TAG, "newViewGiftChainsIntent()");
        Intent intent = new Intent();
        intent.setClass(activity, ViewGiftChainsActivity.class);
        return intent;
    }
    
	protected static Intent newViewGiftChainIntent(Activity activity, long id) {
        Log.d(LOG_TAG, "newViewGiftChainIntent(" + id + ")");
		Intent intent = new Intent();
		intent.setClass(activity, ViewGiftChainActivity.class);
		intent.putExtra(ViewGiftListFragment.rowIdentifyerTAG, id);
		return intent;
	}

    protected static Intent newViewGiftIntent(Activity activity, long id) {
        Log.d(LOG_TAG, "newViewGiftIntent(" + id + ")");
        Intent intent = new Intent();
        intent.setClass(activity, ViewGiftActivity.class);
        intent.putExtra(ViewGiftFragment.rowIdentifyerTAG, id);
        return intent;
    }

    protected static Intent newCrateGiftChainIntent(Activity activity) {
        Log.d(LOG_TAG, "newCrateGiftChainIntent()");
        Intent intent = new Intent();
        intent.setClass(activity, CreateGiftChainActivity.class);
        intent.putExtra(CreateGiftFragment.rowIdentifyerTAG, 0L);
        return intent;
    }
    
    protected static Intent newCreateGiftInChainIntent(Activity activity, long id) {
        Log.d(LOG_TAG, "newCreateGiftInChainIntent(" + id + ")");
        Intent intent = new Intent();
        intent.setClass(activity, CreateGiftInChainActivity.class);
        intent.putExtra(CreateGiftFragment.rowIdentifyerTAG, id);
        return intent;
    }
    
	protected static Intent newViewTopGiftGiversIntent(Activity activity) {
        Log.d(LOG_TAG, "newViewTopGiftGiversIntent()");
		Intent intent = new Intent();
		intent.setClass(activity, ViewTopGiftGiversActivity.class);
		return intent;
	}
    
    protected static Intent newEditPreferencesIntent(Activity activity) {
        Log.d(LOG_TAG, "newEditPreferencesIntent()");
        Intent intent = new Intent();
        intent.setClass(activity, EditPreferencesActivity.class);
        return intent;
    }
}
