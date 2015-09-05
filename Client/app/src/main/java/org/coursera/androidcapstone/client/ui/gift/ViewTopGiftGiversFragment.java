package org.coursera.androidcapstone.client.ui.gift;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.async.CallableTask;
import org.coursera.androidcapstone.client.async.TaskCallback;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.common.OnOpenWindowInterface;
import org.coursera.androidcapstone.common.client.GiftSvcApi;
import org.coursera.androidcapstone.common.client.TopGiftGiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class ViewTopGiftGiversFragment extends ListFragment {
	static final String LOG_TAG = ViewTopGiftGiversFragment.class.getCanonicalName();

	private OnOpenWindowInterface mOpener;

    private BroadcastReceiver mReceiver =
        new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "BroadcastReceiver.onReceive()");
                if (isVisible()) {
                    updateTopGiftGiversList();
                }
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        LocalBroadcastManager localBroadcastManager =
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        localBroadcastManager.registerReceiver(mReceiver, new IntentFilter("FragmentUpdater"));
    }

	@Override
	public void onAttach(Activity activity) {
		Log.d(LOG_TAG, "onAttach() start");
		super.onAttach(activity);
		try {
			mOpener = (OnOpenWindowInterface) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnOpenWindowListener.");
		}
		Log.d(LOG_TAG, "onAttach() end");
	}

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, "onDetach()");
        super.onDetach();
        LocalBroadcastManager localBroadcastManager =
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        localBroadcastManager.unregisterReceiver(mReceiver);
        mOpener = null;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume()");
        super.onResume();
        updateTopGiftGiversList();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.view_top_gift_givers_fragment, container, false);
		return view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        updateTopGiftGiversList();
    }

    public void updateTopGiftGiversList() {
        Log.d(LOG_TAG, "updateTopGiftGiversList()");

        try {
            if (!GiftSvc.loggedIn()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final String serverAddress =
                    preferences.getString("default_server_address", getString(R.string.login_screen_server_default_value));
                GiftSvc.initReadOnly(serverAddress);
            }

            CallableTask.invoke(
                new Callable<Collection<TopGiftGiver>>() {
                    @Override
                    public Collection<TopGiftGiver> call() throws Exception {
                        if (GiftSvc.loggedIn()) {
                            final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();
                            return svc.findTopGiftGivers();
                        }

                        final GiftSvcApi svc = GiftSvc.getReadOnlySecuredRestBuilder();
                        return svc.findTopGiftGivers();
                    }
                },
                new TaskCallback<Collection<TopGiftGiver>>() {
                    @Override
                    public void success(Collection<TopGiftGiver> result) {
                        getListView().setAdapter(
                            new TopGiftGiversCollectionAdapter(getActivity(), R.layout.top_gift_giver_listview_row, new ArrayList<TopGiftGiver>(result)));
                    }

                    @Override
                    public void error(Exception ex) {
                        Log.e(LOG_TAG, "Error logging in via OAuth.", ex);

                        Toast.makeText(getActivity(),
                                       getString(R.string.dialog_error_connection_failure_message),
                                       Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Error retrieving top Gift givers list information. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
