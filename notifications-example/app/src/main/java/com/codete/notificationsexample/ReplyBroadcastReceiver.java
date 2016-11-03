package com.codete.notificationsexample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ReplyBroadcastReceiver extends BroadcastReceiver {

	public static final String KEY_TEXT_REPLY = "key_text_reply";
	public static final int REPLY_NOTIFICATION_ID = 2;

	public ReplyBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get remote input
		Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
		// Exit if remote input is null (always true for API < 24)
		if (remoteInput == null) {
			return;
		}
		// Get reply text
		String reply = remoteInput.getString(KEY_TEXT_REPLY);
		// Create reply notification
		Notification replyNotification =
				new Notification.Builder(context)
						.setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
						.setContentTitle("Reply received!")
						.setContentText(reply)
						.build();
		// Send reply notification
		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(REPLY_NOTIFICATION_ID, replyNotification);
	}
}


