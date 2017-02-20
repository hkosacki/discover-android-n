package com.hublabs.dragreceiver;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnDragListener{

	@BindView(R.id.activity_main_layout)
	RelativeLayout mainLayout;

	View placeholder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		mainLayout.setOnDragListener(this);
	}

	@Override
	public boolean onDrag(View view, DragEvent dragEvent) {
		Log.d("dragEvent", dragEvent.toString());
		switch(dragEvent.getAction()){
			case DragEvent.ACTION_DRAG_ENDED:
				Log.d("event", "ACTION_DRAG_ENDED");
				return true;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.d("event", "ACTION_DRAG_ENTERED");
				return true;
			case DragEvent.ACTION_DRAG_EXITED:
				Log.d("event", "ACTION_DRAG_EXITED");
				return true;
			case DragEvent.ACTION_DRAG_LOCATION:
				Log.d("event", "ACTION_DRAG_LOCATION");
				return true;
			case DragEvent.ACTION_DRAG_STARTED:
				Log.d("event", "ACTION_DRAG_STARTED");
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

	private void handleDropEvent(View view, DragEvent dragEvent){
		ClipDescription clipDescription = dragEvent.getClipDescription();
		ClipData clipData =  dragEvent.getClipData();
//		String mimeType = clip
		Uri uri = clipData.getItemAt(0).getUri();
		String mimeType = URLConnection.guessContentTypeFromName(new File(uri.getPath()).getName());
		if(mimeType.startsWith("image")){
			handleImage(uri);
		}
	}

	private void handleImage(Uri uri){
		ImageView iv = new ImageView(this);
		Bitmap bmp = BitmapFactory.decodeFile(uri.getPath());
		iv.setImageBitmap(bmp);
		updateView(iv);
	}

	private void updateView(View v){
		mainLayout.removeAllViews();
		mainLayout.addView(v);
		mainLayout.invalidate();
	}
}
