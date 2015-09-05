package org.coursera.androidcapstone.client.ui.gift;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.async.CallableTask;
import org.coursera.androidcapstone.client.async.TaskCallback;
import org.coursera.androidcapstone.client.globals.PotlatchApp;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.common.OnOpenWindowInterface;
import org.coursera.androidcapstone.common.client.GiftCounters;
import org.coursera.androidcapstone.common.client.GiftDetail;
import org.coursera.androidcapstone.common.client.GiftSvcApi;

import java.util.concurrent.Callable;

public class ViewGiftFragment extends Fragment {
	private static final String LOG_TAG = ViewGiftFragment.class.getCanonicalName();

	public final static String rowIdentifyerTAG = "gift_id";
    private long giftId = 0;

	private OnOpenWindowInterface mOpener;

    private boolean userTouchedBy  = false;
    private boolean userFlagged = false;

    private TextView titleTV;
    private TextView textTV;
    private ImageView contentIV;
    private TextView createdByTV;
    private TextView creationTimestampTV;
    private TextView touchesTV;
    private TextView flagsTV;
    private RadioGroup voteRG;
    private ToggleButton voteTouchedTB;
    private ToggleButton voteFlaggedTB;

	private Button cancelButton;
	private Button confirmButton;
    private Button deleteButton;

    private BroadcastReceiver mReceiver =
        new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "BroadcastReceiver.onReceive()");
                if (isVisible()) {
                    updateGiftCounters();
                }
            }
        };

	private OnClickListener viewGiftOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
                case R.id.view_gift_button_cancel:
                    cancelButtonPressed();
                    break;
                case R.id.view_gift_button_confirm:
                    confirmButtonPressed();
                    break;
                case R.id.view_gift_button_delete:
                    deleteButtonPressed();
                    break;
                default:
                    break;
			}
		}
	};

    private RadioGroup.OnCheckedChangeListener radioToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int selectedId) {
            try {
                for (int item = 0; item < radioGroup.getChildCount(); item++) {
                    final ToggleButton view = (ToggleButton) radioGroup.getChildAt(item);
                    if (view.getId() != selectedId) {
                        view.setChecked(false);
                    }
                }
            }
            catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    };

    private OnClickListener toggleListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ((RadioGroup) view.getParent()).check(view.getId());
        }
    };

	public static ViewGiftFragment newInstance(long id) {
		ViewGiftFragment fragment = new ViewGiftFragment();
		// Supply id input as an argument.
		Bundle args = new Bundle();
		args.putLong(rowIdentifyerTAG, id);
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
            this.giftId = getArguments().getLong(rowIdentifyerTAG);
        }

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
        updateGiftContent();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.view_gift_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);

        this.titleTV = (TextView) getView().findViewById(R.id.gift_view_value_title);
        this.textTV = (TextView) getView().findViewById(R.id.gift_view_value_text);
        this.contentIV = (ImageView) getView().findViewById(R.id.gift_view_value_image_content);
        this.createdByTV = (TextView) getView().findViewById(R.id.gift_view_value_created_by_username);
        this.creationTimestampTV = (TextView) getView().findViewById(R.id.gift_view_value_creation_timestamp);
        this.touchesTV = (TextView) getView().findViewById(R.id.gift_view_value_touches);
        this.flagsTV = (TextView) getView().findViewById(R.id.gift_view_value_flags);
        this.voteRG = (RadioGroup) getView().findViewById(R.id.vote_gift_toggle_group);
        this.voteTouchedTB = (ToggleButton) getView().findViewById(R.id.vote_gift_radio_touched);
        this.voteFlaggedTB = (ToggleButton) getView().findViewById(R.id.vote_gift_radio_flagged);

        this.voteRG.setOnCheckedChangeListener(this.radioToggleListener);
        this.voteTouchedTB.setOnClickListener(this.toggleListener);
        this.voteFlaggedTB.setOnClickListener(this.toggleListener);

        this.titleTV.setText("" + "");
        this.textTV.setText("" + "");
        this.contentIV.setImageResource(R.drawable.ic_stub);
        this.createdByTV.setText("" + "");
        this.creationTimestampTV.setText("" + "");
        this.touchesTV.setText("0");
        this.flagsTV.setText("0");

        this.cancelButton = (Button) getView().findViewById(R.id.view_gift_button_cancel);
        this.confirmButton = (Button) getView().findViewById(R.id.view_gift_button_confirm);
        this.deleteButton = (Button) getView().findViewById(R.id.view_gift_button_delete);

        this.cancelButton.setOnClickListener(this.viewGiftOnClickListener);
        this.confirmButton.setOnClickListener(this.viewGiftOnClickListener);
        this.deleteButton.setOnClickListener(this.viewGiftOnClickListener);

        updateGiftContent();
	}

	private void cancelButtonPressed() {
        // action to be performed when the cancel button is pressed
        Log.d(LOG_TAG, "cancelButtonPressed()");
        // same as hitting 'back' button
        getActivity().finish();
	}

	private void confirmButtonPressed() {
        // action to be performed when the confirm button is pressed
        Log.d(LOG_TAG, "confirmButtonPressed()");
        // send vote if different from previous one
        if (!this.voteRG.isEnabled() ||
            (!this.voteTouchedTB.isChecked() && !this.voteFlaggedTB.isChecked() && !this.userTouchedBy && !this.userFlagged) ||
            (this.voteTouchedTB.isChecked() && this.userTouchedBy) ||
            (this.voteFlaggedTB.isChecked() && this.userFlagged)) {
            // same as hitting 'back' button
            getActivity().finish();
            return;
        }

        if (!GiftSvc.loggedIn()) {
            mOpener.openLoginScreenFragment();
            return;
        }

        final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

        CallableTask.invoke(
            new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (voteTouchedTB.isChecked()) {
                        return svc.touchedByGift(giftId);
                    }
                    else if (voteFlaggedTB.isChecked()) {
                        return svc.flagGift(giftId);
                    }
                    else {
                        if (userTouchedBy) {
                            return svc.untouchedByGift(giftId);
                        }
                        else if (userFlagged) {
                            return svc.unflagGift(giftId);
                        }
                    }
                    return null;
                }
            },
            new TaskCallback<Void>() {
                @Override
                public void success(Void result) {
                    // same as hitting 'back' button
                    getActivity().finish();
                }

                @Override
                public void error(Exception ex) {
                    Log.e(LOG_TAG, "Error sending Gift rate.", ex);

                    Toast.makeText(getActivity(),
                                   getString(R.string.dialog_error_connection_failure_message),
                                   Toast.LENGTH_SHORT).show();

                    // same as hitting 'back' button
                    getActivity().finish();
                }
            }
        );
	}

    private void deleteButtonPressed() {
        // action to be performed when the confirm button is pressed
        Log.d(LOG_TAG, "deleteButtonPressed()");

        if (!GiftSvc.loggedIn()) {
            mOpener.openLoginScreenFragment();
            return;
        }

        final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

        CallableTask.invoke(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        return svc.deleteGiftById(giftId);
                    }
                },
                new TaskCallback<Void>() {
                    @Override
                    public void success(Void result) {
                        // same as hitting 'back' button
                        getActivity().finish();
                    }

                    @Override
                    public void error(Exception ex) {
                        Log.e(LOG_TAG, "Error while deleting Gift.", ex);

                        Toast.makeText(getActivity(),
                                       getString(R.string.dialog_error_connection_failure_message),
                                       Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updateGiftContent() {
        Log.d(LOG_TAG, "updateGiftCounters(" + this.giftId + ")");

        if (this.giftId == 0) {
            return;
        }

        try {
            if (!GiftSvc.loggedIn()) {
                mOpener.openLoginScreenFragment();
                return;
            }

            final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

            CallableTask.invoke(
                    new Callable<GiftDetail>() {
                        @Override
                        public GiftDetail call() throws Exception {
                            return svc.getGiftDetailById(giftId);
                        }
                    },
                    new TaskCallback<GiftDetail>() {
                        @Override
                        public void success(GiftDetail result) {
                            titleTV.setText("" + result.title);
                            textTV.setText("" + result.text);
							byte[] contentByteArray = result.contentAsByteArray();
                            contentIV.setImageBitmap(BitmapFactory.decodeByteArray(contentByteArray, 0, contentByteArray.length));
                            createdByTV.setText("" + result.createdByUsername);
                            creationTimestampTV.setText("" + result.creationTimestampAsDateString());
                            touchesTV.setText("" + result.touches);
                            flagsTV.setText("" + result.flags);

                            // A user cannot vote for his/her own Gifts
                            PotlatchApp potlatchApp = (PotlatchApp) getActivity().getApplication();
                            boolean isEnabled = !potlatchApp.getUsername().equals(result.createdByUsername);
                            voteRG.setEnabled(isEnabled);
                            voteTouchedTB.setEnabled(isEnabled);
                            voteFlaggedTB.setEnabled(isEnabled);
                            confirmButton.setEnabled(isEnabled);
                            // Only owner can delete Gift
                            deleteButton.setEnabled(!isEnabled);

                            userTouchedBy = result.userTouchedBy;
                            userFlagged = result.userFlagged;

                            if (userTouchedBy) {
                                voteTouchedTB.setChecked(true);
                                voteFlaggedTB.setChecked(false);
                            }
                            else if (userFlagged) {
                                voteTouchedTB.setChecked(false);
                                voteFlaggedTB.setChecked(true);
                            }
                            else {
                                voteTouchedTB.setChecked(false);
                                voteFlaggedTB.setChecked(false);
                            }
                        }

                        @Override
                        public void error(Exception ex) {
                            Log.e(LOG_TAG, "Error retrieving Gift information.", ex);

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
            Log.e(LOG_TAG, "Error retrieving Gift information. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGiftCounters() {
        Log.d(LOG_TAG, "updateGiftCounters(" + this.giftId + ")");

        if (this.giftId == 0) {
            return;
        }

        if (!GiftSvc.loggedIn()) {
            mOpener.openLoginScreenFragment();
            return;
        }

        final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

        CallableTask.invoke(
            new Callable<GiftCounters>() {
                @Override
                public GiftCounters call() throws Exception {
                    return svc.getGiftCountersById(giftId);
                }
            },
            new TaskCallback<GiftCounters>() {
                @Override
                public void success(GiftCounters result) {
                    touchesTV.setText("" + result.touches);
                    flagsTV.setText("" + result.flags);
                }

                @Override
                public void error(Exception ex) {
                    Log.e(LOG_TAG, "Error retrieving Gift counters information.", ex);

                    Toast.makeText(getActivity(),
                                   getString(R.string.dialog_error_connection_failure_message),
                                   Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
}
