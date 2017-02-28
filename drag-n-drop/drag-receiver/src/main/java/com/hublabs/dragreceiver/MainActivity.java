package com.hublabs.dragreceiver;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnDragListener {

	private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 600;
	private static final String TAG = MainActivity.class.getSimpleName();

	@BindView(R.id.activity_main_layout)
	RelativeLayout mainLayout;

	@BindView(R.id.dragLayout)
	FrameLayout dragLayout;

	@BindView(R.id.contentLayout)
	FrameLayout contentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		    != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
			                                                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
				new AlertDialog.Builder(this).setTitle("Allow to read external storage")
				                             .setMessage("I'm just reading files in order to be able to present them for you. I need this permission in order to work.")
				                             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					                             @Override
					                             public void onClick(DialogInterface dialogInterface, int i) {
						                             ActivityCompat.requestPermissions(MainActivity.this,
						                                                               new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						                                                               READ_EXTERNAL_STORAGE_REQUEST_CODE);
					                             }
				                             }).create().show();
			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(this,
				                                  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				                                  READ_EXTERNAL_STORAGE_REQUEST_CODE);
			}
		} else {
			enableDragging();
		}
	}

	/*
     * Consume the permission
     */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
			if (grantResults[Arrays.asList(permissions).indexOf(Manifest.permission.READ_EXTERNAL_STORAGE)] == PackageManager.PERMISSION_DENIED) {
				finish();
			} else {
				enableDragging();
			}
		}
	}

	private void enableDragging() {
		Log.i(TAG, "We now have all required permissions. Let's get it started.");
		mainLayout.setOnDragListener(this);
	}

	@Override
	public boolean onDrag(View view, DragEvent dragEvent) {
		Log.d("dragEvent", dragEvent.toString());
		switch (dragEvent.getAction()) {
			case DragEvent.ACTION_DRAG_ENDED:
				Log.d("event", "ACTION_DRAG_ENDED");
				dragLayout.setVisibility(View.GONE);
				dragLayout.setBackgroundResource(R.drawable.drag_background);
				return true;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d("event", "ACTION_DRAG_ENTERED");
				dragLayout.setBackgroundResource(R.drawable.drag_background_active);
				return true;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.d("event", "ACTION_DRAG_EXITED");
				dragLayout.setBackgroundResource(R.drawable.drag_background);
				return true;
			case DragEvent.ACTION_DRAG_LOCATION:
				Log.d("event", "ACTION_DRAG_LOCATION");
				return true;
			case DragEvent.ACTION_DRAG_STARTED:
				Log.d("event", "ACTION_DRAG_STARTED");
				dragLayout.setVisibility(View.VISIBLE);
				return true;
			case DragEvent.ACTION_DROP:
				Log.d("event", "ACTION_DRAG_DROP");
				handleDropEvent(view, dragEvent);
				return true;
			default:
				Log.e("event", "unknown");
		}

		return false;
	}

	private void handleDropEvent(View view, DragEvent dragEvent) {
		ClipData clipData = dragEvent.getClipData();
		Uri uri = clipData.getItemAt(0).getUri();
		String mimeType = URLConnection.guessContentTypeFromName(new File(uri.getPath()).getName());
		if (mimeType == null) {
			Snackbar.make(mainLayout, "Can't open file. The file type is unknown.", Snackbar.LENGTH_LONG).show();
		} else if (mimeType.startsWith("image")) {
			handleImage(uri);
		} else {
			Intent newIntent = new Intent(Intent.ACTION_VIEW);
			Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(uri.getPath()));

			newIntent.setDataAndType(contentUri, mimeType);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(newIntent, PackageManager.MATCH_DEFAULT_ONLY);
			for (ResolveInfo resolveInfo : resInfoList) {
				String packageName = resolveInfo.activityInfo.packageName;
				grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
			try {
				startActivity(newIntent);
			} catch (ActivityNotFoundException e) {
				Snackbar.make(mainLayout, "Sorry, I found no handler for this type of file.", Snackbar.LENGTH_SHORT).show();
			} catch (FileUriExposedException ex) {
				Snackbar.make(mainLayout, "FileUriExposedException", Snackbar.LENGTH_SHORT).show();
			}
		}
	}

	private void handleImage(Uri uri) {
		ImageView iv = new ImageView(this);
		Bitmap bmp = BitmapFactory.decodeFile(uri.getPath());
		iv.setImageBitmap(bmp);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY);
		params.gravity = Gravity.CENTER;
		iv.setLayoutParams(params);
		updateView(iv);
	}

	private void updateView(View v) {
		contentLayout.removeAllViews();
		contentLayout.addView(v);
		contentLayout.invalidate();
	}
}
