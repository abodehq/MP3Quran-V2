package com.quranmp3;

import java.util.HashMap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.quranmp3.MyMediaPlayerService.LocalBinder;
import com.quranmp3.MyMediaPlayerService.PlayerInterfaceListener;
import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;

public class MyWidgetProvider extends AppWidgetProvider implements
		PlayerInterfaceListener {
	// connect to service
	private static MyMediaPlayerService mService;
	private static boolean mBound = false;
	Context _context;
	RemoteViews remoteViews;
	private static ServiceConnection mConnection;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		_context = context;
		// initializing widget layout
		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		// --------------
		// Intent _intent = new Intent(context, MyMediaPlayerService.class);
		// _intent.putExtra(MyMediaPlayerService.START_PLAY, true);
		// context.startService(_intent);
		// end
		mConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				// We've bound to LocalService, cast the IBinder and get
				// LocalService instance
				LocalBinder binder = (LocalBinder) service;
				mService = binder.getService();
				mBound = true;
				mService.registerListener(MyWidgetProvider.this);
				// InitMP(remoteViews, _context);
				Log.e("MyWidgetProvider", "MyWidget_connected");
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				Log.e("MyWidgetProvider", "MyWidget_disconnect");
				mBound = false;
			}
		};

		// Intent intent = new Intent(context.getApplicationContext(),
		// MyMediaPlayerService.class);
		// intent.putExtra(MyMediaPlayerService.START_PLAY, true);
		// context.startService(intent);
		// context.getApplicationContext().bindService(intent, mConnection,
		// Context.BIND_AUTO_CREATE);

		remoteViews.setOnClickPendingIntent(R.id.btnNext,
				getPendingSelfIntent(context, WidgetUtils.WIDGET_ACTION_NEXT));

		remoteViews.setOnClickPendingIntent(R.id.btnPrevious,
				getPendingSelfIntent(context, WidgetUtils.WIDGET_ACTION_PREV));
		remoteViews.setOnClickPendingIntent(R.id.btnPlay,
				getPendingSelfIntent(context, WidgetUtils.WIDGET_ACTION_PLAY));
		remoteViews.setViewVisibility(R.id.imgLoading, View.INVISIBLE);
		// updating view with initial data
		remoteViews.setTextViewText(R.id.songTitle, getTitle());
		remoteViews.setTextViewText(R.id.songTitle1, getDesc());
		remoteViews.setImageViewResource(R.id.btnPlay, R.drawable.btn_pause);

		// request for widget update
		pushWidgetUpdate(context, remoteViews);
	}

	public PendingIntent buildButtonPendingIntent3(Context context) {
		Log.e("asdasd", "Asdasd");
		return null;
	}

	public static PendingIntent buildButtonPendingIntent(Context context) {
		// ++MyWidgetIntentReceiver.clickCount;

		// initiate widget update request
		Intent intent = new Intent();
		intent.setAction(WidgetUtils.WIDGET_UPDATE_ACTION);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public static PendingIntent PendingIntentClickTogglePlay(Context context) {
		// ++MyWidgetIntentReceiver.clickCount;

		// initiate widget update request
		Intent intent = new Intent();
		intent.setAction(WidgetUtils.WIDGET_ACTION_PLAY);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private static CharSequence getDesc() {
		return "Sync to see some of our funniest joke collections";
	}

	private static CharSequence getTitle() {
		return "Funny Jokes";
	}

	public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {

		ComponentName myWidget = new ComponentName(context,
				MyWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
	}

	private static Boolean playingStatus = true;

	@Override
	public void onCompletionMp3() {
		Log.e("init", "onCompletionMp3");
		// TODO Auto-generated method stub

	}

	@Override
	public void onErrorMp3() {
		Log.e("init", "onErrorMp3");
		// TODO Auto-generated method stub

	}

	@Override
	public void isPreparedMp3() {
		Log.e("init", "isPreparedMp3");
		// TODO Auto-generated method stub

	}

	@Override
	public void TogglePlay(Boolean status) {

		MyWidgetProvider.playingStatus = status;
		Intent intent = new Intent(_context, getClass());

		intent.setAction(WidgetUtils.WIDGET_PLAY);
		// TODO Auto-generated method stub
		intent.setFlags(0);
		_context.sendBroadcast(intent);
		// PendingIntent.getBroadcast(_context, 0, intent,
		// PendingIntent.FLAG_UPDATE_CURRENT);

	}

	@Override
	public void onstartMp3() {
		Intent intent = new Intent(_context, getClass());
		intent.setAction(WidgetUtils.WIDGET_START);
		intent.setFlags(0);
		_context.sendBroadcast(intent);
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNewMp3() {
		Log.e("init", "init");
		// TODO Auto-generated method stub

	}

	public void InitMP(RemoteViews remoteViews, Context context) {
		Log.e("InitMP", "InitMP" + mBound);
		if (mBound) {
			AudioListManager audioListManager = AudioListManager.getInstance();
			if (audioListManager.getUpdatePlayerStatus()) {
				Log.e("status", "new mp3");
				mService.PlayNewMp3();
				audioListManager.setUpdatePlayerStatus(false);
			} else {
				HashMap<String, Integer> mpStatus = mService.getMPStatus();
				Log.e("status", mpStatus.get("playing") + "");
				if (mpStatus.get("playing") == 1)
					remoteViews.setImageViewResource(R.id.btnPlay,
							R.drawable.btn_pause);

				if (mpStatus.get("playing") == 0)
					remoteViews.setImageViewResource(R.id.btnPlay,
							R.drawable.btn_play);

				if (mpStatus.get("playing") != -1) {
					SetVersesInfo(remoteViews, context);
				} else {

					mService.TogglePlay();

				}
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("receive", "receive");
		Log.e("receive", intent.getAction());
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);

		RemoteViews remoteViews;
		ComponentName watchWidget;
		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED
				.equals(intent.getAction())) {
			Log.e("delete", "delete");
			if (mBound) {
				if (mService != null) {
					mService.unregisterListener(this);
				}
				context.unbindService(mConnection);
				mBound = false;
			}

		}
		if (AppWidgetManager.ACTION_APPWIDGET_ENABLED
				.equals(intent.getAction())) {
			// Intent serviceIntent = new
			// Intent(context.getApplicationContext(),
			// MyMediaPlayerService.class);
			// serviceIntent.putExtra(MyMediaPlayerService.START_PLAY, true);
			// context.startService(serviceIntent);
			Log.e("enabled", "enabled");
			Intent _intent = new Intent(context, getClass());
			_intent.setAction(WidgetUtils.WIDGET_INIT);
			_intent.setFlags(0);
			context.sendBroadcast(_intent);

		}
		if (WidgetUtils.WIDGET_ACTION_PLAY.equals(intent.getAction())) {
			if (mBound)
				mService.TogglePlay();
		}
		if (WidgetUtils.WIDGET_ACTION_NEXT.equals(intent.getAction())) {
			if (mBound) {
				remoteViews.setViewVisibility(R.id.imgLoading, View.VISIBLE);
				mService.NextMp3();
			}
		}
		if (WidgetUtils.WIDGET_ACTION_PREV.equals(intent.getAction())) {
			if (mBound) {
				remoteViews.setViewVisibility(R.id.imgLoading, View.VISIBLE);
				mService.PrevMp3();
			}
		}

		if (WidgetUtils.WIDGET_INIT.equals(intent.getAction())) {
			// InitMP(remoteViews, context);
		}
		if (WidgetUtils.WIDGET_START.equals(intent.getAction())) {
			remoteViews.setViewVisibility(R.id.imgLoading, View.INVISIBLE);
			SetVersesInfo(remoteViews, context);
			remoteViews
					.setImageViewResource(R.id.btnPlay, R.drawable.btn_pause);
		}
		if (WidgetUtils.WIDGET_PLAY.equals(intent.getAction())) {

			remoteViews.setOnClickPendingIntent(
					R.id.btnPlay,
					getPendingSelfIntent(context,
							WidgetUtils.WIDGET_ACTION_PLAY));

			if (MyWidgetProvider.playingStatus)
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.btn_pause);
			else
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.btn_play);

			// remoteViews.setTextViewText(R.id.sync_button, "TESTING");

		}
		watchWidget = new ComponentName(context, MyWidgetProvider.class);
		appWidgetManager.updateAppWidget(watchWidget, remoteViews);

	}

	public void SetVersesInfo(RemoteViews remoteViews, Context context) {
		AudioClass verse = null;
		if (mBound) {
			verse = mService.getCurrentVerse();
		}
		if (verse != null) {

			String reciters_pre = context.getApplicationContext().getString(
					R.string.reciters_pre)
					+ " ";
			String verses_pre = context.getApplicationContext().getString(
					R.string.verses_pre)
					+ " ";

			String verseTitle = verse.getVerseName();
			remoteViews
					.setTextViewText(R.id.songTitle, verses_pre + verseTitle);

			String verseReciter = verse.getReciterName();
			remoteViews.setTextViewText(R.id.songTitle1, reciters_pre
					+ verseReciter);

			// ImageView image_icon = (ImageView) rootView
			// .findViewById(R.id.list_image);

			String image = verse.getImage().split(".jpg")[0];

			int id = context.getResources().getIdentifier(image, "drawable",
					context.getPackageName());
			remoteViews.setImageViewResource(R.id.list_image, id);

			// image_icon.setImageDrawable(_FragmentActivity.getResources()
			// .getDrawable(id));

		}
	}

	protected PendingIntent getPendingSelfIntent(Context context, String action) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	@Override
	public void onToggleRepeat(Boolean bRepeat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onToggleShuffle(Boolean bShuffle) {
		// TODO Auto-generated method stub

	}
}
