package org.coursera.androidcapstone.client.ui.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.async.CallableTask;
import org.coursera.androidcapstone.client.async.TaskCallback;
import org.coursera.androidcapstone.client.globals.PotlatchApp;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.common.OnOpenWindowInterface;
import org.coursera.androidcapstone.common.client.GiftSvcApi;

import java.util.concurrent.Callable;

public class LoginScreenFragment extends Fragment {
    private static final String LOG_TAG = LoginScreenFragment.class.getName();

    private OnOpenWindowInterface mOpener;
    
    private EditText serverET;
	private EditText usernameET;
	private EditText passwordET;

    private Button loginButton;
    
    private OnClickListener loginScreenOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_button:
                    loginButtonPressed();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.serverET.setText(preferences.getString("default_server_address", getString(R.string.login_screen_server_default_value)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.login_screen_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        this.serverET = (EditText) getView().findViewById(R.id.login_screen_server_value);
        this.usernameET = (EditText) getView().findViewById(R.id.login_screen_username_value);
        this.passwordET = (EditText) getView().findViewById(R.id.login_screen_password_value);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.serverET.setText(preferences.getString("default_server_address", getString(R.string.login_screen_server_default_value)));

        this.usernameET.setText("" + "");
        this.passwordET.setText("" + "");

        this.loginButton = (Button) getView().findViewById(R.id.login_button);
        this.loginButton.setOnClickListener(loginScreenOnClickListener);
    }

 	public void loginButtonPressed() {
        Log.d(LOG_TAG, "loginButtonPressed()");

        String server = this.serverET.getText().toString();
		String username = this.usernameET.getText().toString();
		String password = this.passwordET.getText().toString();

		GiftSvc.init(server, username, password);

        final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

        CallableTask.invoke(
            new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return svc.checkUsername();
                }
            },
            new TaskCallback<String>() {
                @Override
                public void success(String result) {
                    // OAuth 2.0 grant was successful and we
                    // can talk to the server, open up the Gift listing
                    PotlatchApp potlatchApp = (PotlatchApp) getActivity().getApplication();
                    potlatchApp.setUsername(result);
                    mOpener.openViewGiftChainsFragment();
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
}
