package org.coursera.androidcapstone.client.ui.gift;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.client.async.CallableTask;
import org.coursera.androidcapstone.client.async.TaskCallback;
import org.coursera.androidcapstone.client.rest.GiftSvc;
import org.coursera.androidcapstone.client.ui.common.OnOpenWindowInterface;
import org.coursera.androidcapstone.common.client.GiftSvcApi;
import org.coursera.androidcapstone.common.repository.Gift;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

public class CreateGiftFragment extends Fragment {
    private final static String LOG_TAG = CreateGiftFragment.class.getCanonicalName();
    
    private final static int CAMERA_PIC_REQUEST = 1;
    private final static int SELECT_PHOTO_REQUEST = 2;

    public final static String rowIdentifyerTAG = "gift_id";
    public final static String newChainTAG = "new_chain";
    private long giftId = 0;
    private boolean newChain = false;

	private EditText titleET;
    private EditText textET;
    public Uri imagePath;
    private Bitmap imageBitmap = null;
    private ImageView contentIV;

	private Button imageSelectButton;
    private Button imageCaptureButton;

    private Button cancelButton;
    private Button confirmButton;

	private OnOpenWindowInterface mOpener;

    private OnClickListener createGiftOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.create_gift_button_select_image:
                    launchPickPhotoIntent();
                    break;
                case R.id.create_gift_button_capture_image:
                    launchCameraIntent();
                    break;
                case R.id.create_gift_button_cancel:
                    cancelButtonPressed();
                    break;
                case R.id.create_gift_button_confirm:
                    confirmButtonPressed();
                    break;
                default:
                    break;
            }
        }
    };

    public static CreateGiftFragment newInstance(long id, boolean newChain) {
		CreateGiftFragment fragment = new CreateGiftFragment();
        Bundle args = new Bundle();
        args.putLong(rowIdentifyerTAG, id);
        args.putBoolean(newChainTAG, newChain);
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
            this.newChain = getArguments().getBoolean(newChainTAG);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.create_gift_fragment, container, false);
        return view;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);

        this.titleET = (EditText) getView().findViewById(R.id.gift_create_value_title);
        this.textET = (EditText) getView().findViewById(R.id.gift_create_value_text);
        this.contentIV = (ImageView) getView().findViewById(R.id.gift_create_value_image_content);

        this.titleET.setText("" + "");
        this.textET.setText("" + "");
        this.contentIV.setImageResource(R.drawable.ic_empty);

        this.imageSelectButton = (Button) getView().findViewById(R.id.create_gift_button_select_image);
        this.imageCaptureButton = (Button) getView().findViewById(R.id.create_gift_button_capture_image);

        this.cancelButton = (Button) getView().findViewById(R.id.create_gift_button_cancel);
        this.confirmButton = (Button) getView().findViewById(R.id.create_gift_button_confirm);

        this.imageSelectButton.setOnClickListener(this.createGiftOnClickListener);
        this.imageCaptureButton.setOnClickListener(this.createGiftOnClickListener);
        this.cancelButton.setOnClickListener(this.createGiftOnClickListener);
        this.confirmButton.setOnClickListener(this.createGiftOnClickListener);
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

        // local Editables
        Editable titleCreateable = titleET.getText();
        Editable textCreateable = textET.getText();

        // pull values from Editables
        final String title = String.valueOf(titleCreateable.toString());
        final String text = String.valueOf(textCreateable.toString());

        // text can be empty
        if ((title.length() > 0) && (imageBitmap != null)) {
            if (!GiftSvc.loggedIn()) {
                mOpener.openLoginScreenFragment();
                return;
            }

            final GiftSvcApi svc = GiftSvc.getSecuredRestBuilder();

            CallableTask.invoke(
                new Callable<Gift>() {
                    @Override
                    public Gift call() throws Exception {
                        Gift gift = new Gift();

                        gift.setTitle(title);
                        gift.setText(text);

                        Bitmap contentBitmap = Bitmap.createScaledBitmap(imageBitmap, 320, 320, false);
                        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
                        contentBitmap.compress(Bitmap.CompressFormat.PNG, 100, contentStream);
                        gift.contentFromByteArray(contentStream.toByteArray());

                        Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(imageBitmap, 64, 64, false);
                        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                        thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, thumbnailStream);
                        gift.thumbnailFromByteArray(thumbnailStream.toByteArray());

                        if (newChain) {
                            return svc.addGift(gift);
                        }
                        else {
                            return svc.addGiftToChain(giftId, gift);
                        }
                    }
                },
                new TaskCallback<Gift>() {
                    @Override
                    public void success(Gift result) {
                        // same as hitting 'back' button
                        getActivity().finish();
                    }

                    @Override
                    public void error(Exception ex) {
                        Log.e(LOG_TAG, "Error saving new Gift.", ex);

                        Toast.makeText(getActivity(),
                                getString(R.string.dialog_error_connection_failure_message),
                                Toast.LENGTH_SHORT).show();

                        // same as hitting 'back' button
                        getActivity().finish();
                    }
                }
            );
        }
        else {
            // invalid information: alert user
            Log.e(LOG_TAG, "Invalid Gift information.");

            Toast.makeText(getActivity(),
                    getString(R.string.dialog_error_invalid_gift_information),
                    Toast.LENGTH_SHORT).show();
        }
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivtyResult(). requestCode: " + requestCode + " resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = null;
            // Image captured and saved to fileUri specified in the Intent
            if (requestCode == CAMERA_PIC_REQUEST) {
                selectedImage = this.imagePath;
            }
            else if (requestCode == SELECT_PHOTO_REQUEST) {
                selectedImage = data.getData();
            }
            if (selectedImage != null) {
                InputStream imageStream = null;
                try {
                    imageStream =
                        getActivity().getApplicationContext().getContentResolver().openInputStream(selectedImage);
                }
                catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                if (imageStream != null) {
                    this.imageBitmap = BitmapFactory.decodeStream(imageStream);
                    this.contentIV.setImageBitmap(this.imageBitmap);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled the image capture
            Log.e(LOG_TAG, "User cancelled the image capture.");
        }
        else {
            // Image capture failed, advise user
            Log.e(LOG_TAG, "Image capture or selection failed.");
        }
	}

    private static Uri getOutputMediaFileUri() {
        File mediaFile = getOutputMediaFile();
        if (mediaFile != null) {
            return Uri.fromFile(mediaFile);
        }
        return null;
    }
    
    private static File getOutputMediaFile() {
        Log.d(LOG_TAG, "getOutputMediaFile()");

        // To be safe, we must check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "External storage is not mounted.");
            return null;
        }

        File mediaStorageDir =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PotlatchApp");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "Failed to create directory.");
                return null;
            }
        }

        // Create a media file name
        String timeStamp =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile =
            new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void launchCameraIntent() {
        // This function creates a new Intent to launch the built-in Camera activity
        Log.d(LOG_TAG, "launchCameraIntent()");

        // Create a new intent to launch the MediaStore, Image capture function
        // See: http://developer.android.com/reference/android/provider/MediaStore.html
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Set the imagePath for this image file using the pre-made function
        // getOutputMediaFile to create a new filename for this specific image
        Uri imageFileName = getOutputMediaFileUri();

        if (imageFileName == null) {
            Log.e(LOG_TAG, "Cannot create image file for new Gift.");

            Toast.makeText(getActivity(),
                           getString(R.string.dialog_error_cannot_create_image_file_message),
                           Toast.LENGTH_SHORT).show();

            // same as hitting 'back' button
            getActivity().finish();
            return;
        }

        // Add the filename to the Intent as an extra. Use the Intent-extra name
        // from the MediaStore class, EXTRA_OUTPUT
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileName);
        cameraIntent.putExtra("return-data", true);
        this.imagePath = imageFileName;

        // Start a new activity for result, using the new intent and the request
        // code CAMERA_PIC_REQUEST
        getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    private void launchPickPhotoIntent() {
        Log.d(LOG_TAG, "launchPickPhotoIntent()");
        Intent photoPickerIntent =
            new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        getActivity().startActivityForResult(photoPickerIntent, SELECT_PHOTO_REQUEST);
    }
}
