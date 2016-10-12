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

public class MainActivity extends AppCompatActivity {

	private final String TAG = MainActivity.class.getSimpleName();
	private static final String KEY_TEXT_REPLY = "key_text_reply";

	Button buttonSend;
	int notificationId = 1234;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		buttonSend = (Button) findViewById(R.id.button_send_notifaction);
		setButtonListeners();
	}

	private void setButtonListeners(){
		buttonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendNotification("TEST");
			}
		});
	}

	private void sendNotification(String message){
		// Key for the string that's delivered in the action's intent.
		String replyLabel = getResources().getString(R.string.reply_label);
		RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
				.setLabel(replyLabel)
				.build();

		Intent replyIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		//stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(replyIntent);
		PendingIntent replyPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		// Create the reply action and add the remote input.
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
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId, newMessageNotification);
	}

}
