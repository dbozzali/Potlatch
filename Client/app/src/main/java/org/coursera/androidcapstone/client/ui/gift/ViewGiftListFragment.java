package org.coursera.androidcapstone.client.ui.gift;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.async.CallableTask;
import org.coursera.androidcapstone.client.async.TaskCallback;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.common.OnOpenWindowInterface;
import org.coursera.androidcapstone.common.client.GiftItem;
import org.coursera.androidcapstone.common.client.GiftSvcApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class ViewGiftListFragment extends ListFragment {
	static final String LOG_TAG = ViewGiftListFragment.class.getCanonicalName();

    public final static String rowIdentifyerTAG = "gift_id";
	public final static String chainDetailTAG = "chain_detail";
    private long giftId = 0;
	private boolean chainDetail = false;

	private OnOpenWindowInterface mOpener;

	private EditText filterET;

    private Button filterButton;
    private Button addButton;

    private OnClickListener viewGiftListOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_gift_listview_button_add:
                    addButtonPressed();
                    break;
                case R.id.view_gift_listview_button_filter:
                    filterButtonPressed();
                default:
                    break;
            }
        }
    };

    public static ViewGiftListFragment newInstance(long id, boolean chainDetail) {
        ViewGiftListFragment fragment = new ViewGiftListFragment();
        Bundle args = new Bundle();
        args.putLong(rowIdentifyerTAG, id);
        args.putBoolean(chainDetailTAG, chainDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            // read values from arguments
            giftId = getArguments().getLong(rowIdentifyerTAG);
            chainDetail = getArguments().getBoolean(chainDetailTAG);
        }
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
		mOpener = null;
	}

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume()");
        super.onResume();
        updateGiftList();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.view_gift_listview_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        this.filterET = (EditText) getView().findViewById(R.id.view_gift_listview_filter);
        this.filterButton = (Button) getView().findViewById(R.id.view_gift_listview_button_filter);
        this.addButton = (Button) getView().findViewById(R.id.view_gift_listview_button_add);

        this.filterET.setText("" + "");
        if (chainDetail) {
            this.addButton.setText(R.string.view_gift_listview_button_add_to_chain_value);
        }
        else {
            this.addButton.setText(R.string.view_gift_listview_button_create_new_chain_value);
        }

        this.filterButton.setOnClickListener(this.viewGiftListOnClickListener);
        this.addButton.setOnClickListener(this.viewGiftListOnClickListener);

        // create the custom array adapter that will make the custom row
		// layouts and update the back end data.
        GiftItemCollectionAdapter adapter =
            new GiftItemCollectionAdapter(getActivity(), R.layout.view_gift_listview_row, new ArrayList<GiftItem>());
        setListAdapter(adapter);

        updateGiftList();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(LOG_TAG, "onListItemClick(). position: " + position + ", id = " + id);

		if (chainDetail) {
			mOpener.openViewGiftFragment(id);
		}
		else {
			mOpener.openViewGiftChainFragment(id);
		}
	}

    public void addButtonPressed() {
        Log.d(LOG_TAG, "addButtonPressed()");
        if (!GiftSvc.loggedIn()) {
            mOpener.openLoginScreenFragment();
            return;
        }

        if (chainDetail) {
            mOpener.openCreateGiftInChainFragment(giftId);
        }
        else {
            mOpener.openCreateGiftChainFragment();
        }
    }

    public void filterButtonPressed() {
        Log.d(LOG_TAG, "filterButtonPressed()");
        updateGiftList();
    }

    private void updateGiftList() {
        Log.d(LOG_TAG, "updateGiftList()");

        if (chainDetail && (giftId == 0)) {
            return;
        }

        try {
            final String filterWord = filterET.getText().toString();

            if (!GiftSvc.loggedIn()) {
                mOpener.openLoginScreenFragment();
                return;
            }

            final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

            SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
            final boolean filterFlaggedGifts =
                Boolean.valueOf(preferences.getBoolean("filter_flagged_gifts", false));

            CallableTask.invoke(
                new Callable<Collection<GiftItem>>() {
                    @Override
                    public Collection<GiftItem> call() throws Exception {
                        if (filterWord.length() > 0) {
                            // create String that will match with 'like' in query
                            final String filterTitle = "%" + filterWord + "%";

                            if (chainDetail) {
                                return svc.findGiftInChainByTitle(giftId, filterTitle, filterFlaggedGifts);
                            }
                            else {
                                return svc.findGiftChainByTitle(filterTitle, filterFlaggedGifts);
                            }
                        }
                        else {
                            if (chainDetail) {
                                return svc.getGiftChainById(giftId, filterFlaggedGifts);
                            }
                            else {
                                return svc.getGiftChainsList(filterFlaggedGifts);
                            }
                        }
                    }
                },
                new TaskCallback<Collection<GiftItem>>() {
                    @Override
                    public void success(Collection<GiftItem> result) {
                        if (result.isEmpty() && chainDetail) {
                            // same as hitting 'back' button
                            getActivity().finish();
                            return;
                        }
                        getListView().setAdapter(
                            new GiftItemCollectionAdapter(getActivity(), R.layout.view_gift_listview_row, new ArrayList<GiftItem>(result)));
                    }

                    @Override
                    public void error(Exception ex) {
                        Log.e(LOG_TAG, "Error retrieving Gift list information.", ex);

                        Toast.makeText(getActivity(),
                                       getString(R.string.dialog_error_connection_failure_message),
                                       Toast.LENGTH_SHORT).show();

                        // same as hitting 'back' button
                        getActivity().finish();
                    }
                }
            );
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Error retrieving Gift list information. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
