package com.quranmp3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * context activity will be called when the alarm is triggered.
 * 
 * @author Michael Irwin
 */

public class AlarmReceiverActivity extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {

		if (WidgetUtils.WIDGET_EXIT.equals(intent.getAction())) {
			if (!MyMediaPlayerService.isMainAppRunning) {
				Intent _intent = new Intent(context.getApplicationContext(),
						MyMediaPlayerService.class);
				context.stopService(_intent);
			} else {
				Intent _intent = new Intent(context.getApplicationContext(),
						MyMediaPlayerService.class);
				IBinder serviceBinder = peekService(context, _intent);
				if (serviceBinder != null)
					((MyMediaPlayerService.LocalBinder) serviceBinder)
							.getService().HideNotification();
				// Intent notificationIntent = new Intent(context,
				// MainActivity.class);

				// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				// | Intent.FLAG_ACTIVITY_NEW_TASK);

				// context.startActivity(notificationIntent);
			}
		}
		if (WidgetUtils.WIDGET_PLAY.equals(intent.getAction())) {
			Intent _intent = new Intent(context.getApplicationContext(),
					MyMediaPlayerService.class);
			IBinder serviceBinder = peekService(context, _intent);
			if (serviceBinder != null)
				((MyMediaPlayerService.LocalBinder) serviceBinder).getService()
						.TogglePlay();
		}
		if (WidgetUtils.WIDGET_ACTION_NEXT.equals(intent.getAction())) {
			Intent _intent = new Intent(context.getApplicationContext(),
					MyMediaPlayerService.class);
			IBinder serviceBinder = peekService(context, _intent);
			if (serviceBinder != null)
				((MyMediaPlayerService.LocalBinder) serviceBinder).getService()
						.NextMp3();
		}

	}
}