package com.codete.notificationsexample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	private final String TAG = MainActivity.class.getSimpleName();
	private static final String KEY_TEXT_REPLY = "key_text_reply";

	Button buttonSend;
	int notificationId = 1234;
	Context mContext;
	Intent replyIntent;
	TextView textReplyMessage, textReplyLabel;
	NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		buttonSend = (Button) findViewById(R.id.button_send_notifaction);
		replyIntent = new Intent(this, MainActivity.class);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		setButtonListeners();
		checkForReplies();
	}

	private void setButtonListeners() {
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendNotification("TEST");
			}
		});
	}

	private void sendNotification(String message) {

		// Add a notification reply intent to the task stack
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(replyIntent);
		PendingIntent replyPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		// Create the reply action and add the remote input.
		String replyLabel = getResources().getString(R.string.reply_label);
		RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
				.setLabel(replyLabel)
				.build();
		Notification.Action action =
				new Notification.Action.Builder(R.drawable.ic_arrow_forward_black_24dp,
						getString(R.string.label), replyPendingIntent)
						.addRemoteInput(remoteInput)
						.build();


		// Build the notification and add the action.
		Notification newMessageNotification =
				new Notification.Builder(mContext)
						.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
						.setContentTitle(getString(R.string.notification_title))
						.setContentText(getString(R.string.notification_message))
						.addAction(action)
						.build();

		// Issue the notification.
		notificationManager.notify(notificationId, newMessageNotification);
	}


	private void sendReplyNotification() {
		// Build a new notification, which informs the user that the system
		// handled their interaction with the previous notification.
		Notification repliedNotification =
				new Notification.Builder(this)
						.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
						.setContentTitle(getString(R.string.notification_reply_title))
						.setContentText(getString(R.string.notification_reply_message))
						.build();

		// Issue the new notification.
		notificationManager.notify(notificationId, repliedNotification);
	}

	private void checkForReplies() {
		Bundle remoteInput = RemoteInput.getResultsFromIntent(replyIntent);
		String reply = "";
		if (remoteInput != null) {
			reply = remoteInput.getString(KEY_TEXT_REPLY);
			if (reply.isEmpty()) return;
		}
		textReplyLabel.setVisibility(View.VISIBLE);
		textReplyMessage.setVisibility(View.VISIBLE);
		textReplyMessage.setText(reply);
		sendReplyNotification();
	}

}
