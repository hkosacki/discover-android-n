package com.codete.notificationsexample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

	private final String TAG = MainActivity.class.getSimpleName();
	public static final int NOTIFICATION_ID = 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button buttonSend = (Button) findViewById(R.id.button_send_notifaction);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendNotification();
			}
		});

		registerReceiver(new ReplyBroadcastReceiver(), null);
	}

	private void sendNotification() {
		// Create a reply intent sending us back to the activity
		Intent replyIntent = new Intent(this, MainActivity.class);
		// Add a notification reply intent to the task stack
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(replyIntent);
		// Create reply pending intent as broadcast
		PendingIntent replyPendingIntent = PendingIntent.getBroadcast(
				MainActivity.this,
				0,
				new Intent(MainActivity.this, ReplyBroadcastReceiver.class),
				PendingIntent.FLAG_CANCEL_CURRENT
		);
		// Create the remote input
		RemoteInput remoteInput = new RemoteInput.Builder(ReplyBroadcastReceiver.KEY_TEXT_REPLY)
				.setLabel("Reply")
				.build();
		// Create the reply action
		Notification.Action action =
				new Notification.Action.Builder(
						R.drawable.ic_arrow_forward_black_24dp,
						"Reply",
						replyPendingIntent)
						.addRemoteInput(remoteInput)
						.build();
		// Build the notification
		Notification newMessageNotification =
				new Notification.Builder(this)
						.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
						.setContentTitle("Notification")
						.addAction(action)
						.build();
		// Send the notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, newMessageNotification);
	}

}
