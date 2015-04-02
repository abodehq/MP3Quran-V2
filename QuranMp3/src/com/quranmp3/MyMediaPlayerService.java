package com.quranmp3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.utils.Utils;

public class MyMediaPlayerService extends Service implements
		OnCompletionListener {

	private MediaPlayer mp = null;
	private final IBinder mBinder = new LocalBinder();
	private PlayerInterfaceListener mCallback;
	private static int classID = 772; // just a number
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private boolean isPrepared = false;
	private boolean isActive = false;
	private AudioClass verse;
	private final ArrayList<PlayerInterfaceListener> mListeners = new ArrayList<PlayerInterfaceListener>();
	private int currentSongIndex = 0;
	private ArrayList<AudioClass> songsList = null;
	public static Boolean isMainAppRunning = false;

	public MyMediaPlayerService() {
		CreateMediaPlayer();
		setList();

	}

	public void setList() {
		AudioListManager audioListManager = AudioListManager.getInstance();
		songsList = audioListManager.getPlayList();
	}

	public interface PlayerInterfaceListener {
		public void onCompletionMp3();

		public void onErrorMp3();

		public void isPreparedMp3();

		public void TogglePlay(Boolean status);

		public void onstartMp3();

		public void onInitNewMp3();

		public void onToggleRepeat(Boolean bRepeat);

		public void onToggleShuffle(Boolean bShuffle);

	}

	public Boolean getIsprepared() {
		return isPrepared;
	}

	public void registerListener(PlayerInterfaceListener listener) {
		mListeners.add(listener);
	}

	public void unregisterListener(PlayerInterfaceListener listener) {
		mListeners.remove(listener);
	}

	public HashMap<String, Integer> getMPStatus() {
		HashMap<String, Integer> mpStatus = new HashMap<String, Integer>();
		Log.e("isactive", isActive + "");
		if (isActive) {

			// check for already playing
			if (mp.isPlaying()) {
				mpStatus.put("playing", 1);
				// true
			} else {
				mpStatus.put("playing", 0);
				// Resume song
				// false;
			}
		} else {
			mpStatus.put("playing", -1);
			// TogglePlay();
			// false;
		}
		int shuffle = 0;
		if (isShuffle)
			shuffle = 1;
		int repeat = 0;
		if (isRepeat)
			repeat = 1;
		mpStatus.put("shuffle", shuffle);
		mpStatus.put("repeat", repeat);

		// mpStatus.put("currentPosition", mp.getCurrentPosition());
		// mpStatus.put("duration", mp.getDuration());
		return mpStatus;
	}

	/**/
	public void CreateMediaPlayer() {
		if (mp != null)
			ReleaseMediaPlayer();
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				isPrepared = true;
			}
		});
		mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {

				for (int i = mListeners.size() - 1; i >= 0; i--) {
					mListeners.get(i).onErrorMp3();
				}
				return false;
			}
		});

		// PlayNewMp3(null);
	}

	private boolean isPlaying = false;

	public static String START_PLAY = "START_PLAY";

	public class LocalBinder extends Binder {
		public MyMediaPlayerService getService() {
			return MyMediaPlayerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// if (intent.getBooleanExtra(START_PLAY, false)) {
		// BuildNotification();
		// }
		Log.e("Start Service", "start Service");
		return Service.START_STICKY;
		// return Service.START_STICKY;
	}

	RemoteViews remoteViews = null;
	NotificationCompat.Builder mBuilder = null;
	NotificationManager mNotifyManager = null;

	public void BuildNotification() {

		if (mBuilder == null) {
			mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
			// ///-------------------
			mBuilder = new NotificationCompat.Builder(getApplicationContext());
			remoteViews = new RemoteViews(getPackageName(),
					R.layout.notification);
			Intent switchIntent = new Intent(this, AlarmReceiverActivity.class);
			switchIntent.setAction(WidgetUtils.WIDGET_PLAY);
			PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(
					this, 0, switchIntent, 0);

			remoteViews.setOnClickPendingIntent(R.id.btnPlay,
					pendingSwitchIntent);

			switchIntent.setAction(WidgetUtils.WIDGET_EXIT);
			PendingIntent pendingExitIntent = PendingIntent.getBroadcast(this,
					0, switchIntent, 0);
			remoteViews
					.setOnClickPendingIntent(R.id.btnExit, pendingExitIntent);

			switchIntent.setAction(WidgetUtils.WIDGET_ACTION_NEXT);
			PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this,
					0, switchIntent, 0);
			remoteViews
					.setOnClickPendingIntent(R.id.btnNext, pendingNextIntent);

			mBuilder.setSmallIcon(R.drawable.ic_player_icon);
			mBuilder.setContent(remoteViews);
			mBuilder.setAutoCancel(true);
			// mBuilder.setContentTitle(" –ﬂÌ— !");
			// mBuilder.setContentText("·«  ‰”Ï ﬁ—«¡… ÕœÌÀ «·ÌÊ„ „‰ ’ÕÌÕ „”·„!!");
			mBuilder.setContentIntent(pi);
		}
		remoteViews.setTextViewText(R.id.txt_reciter_name,
				verse.getReciterName());
		remoteViews.setTextViewText(R.id.txt_verse_name, verse.getVerseName());
		String image = verse.getImage().split(".jpg")[0];

		Context context = getApplicationContext();
		int id = context.getResources().getIdentifier(image, "drawable",
				context.getPackageName());

		remoteViews.setImageViewResource(R.id.image_icon, id);

		if (isActive && mp != null) {

			// check for already playing
			if (mp.isPlaying()) {
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.ic_notification_pause);
				// true
			} else {
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.ic_notification_play);
				// Resume song
				// false;
			}
		}
		startForeground(classID, mBuilder.build());
		// ------------------
		// if (mBuilder == null) {
		// startForeground(classID, mBuilder.build());
		// } else
		// mNotifyManager.notify(classID, mBuilder.build());
		// mp = MediaPlayer.create(this, R.raw.rain);
		// mp.setLooping(true);
		// mp.start();

	}

	public void BuildNotificationOld() {

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		// ///-------------------
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				getApplicationContext());
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.notification);
		remoteViews.setTextViewText(R.id.txt_reciter_name,
				verse.getReciterName());
		remoteViews.setTextViewText(R.id.txt_verse_name, verse.getVerseName());
		String image = verse.getImage().split(".jpg")[0];

		Context context = getApplicationContext();
		int id = context.getResources().getIdentifier(image, "drawable",
				context.getPackageName());

		remoteViews.setImageViewResource(R.id.image_icon, id);

		if (isActive && mp != null) {

			// check for already playing
			if (mp.isPlaying()) {
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.ic_notification_pause);
				// true
			} else {
				remoteViews.setImageViewResource(R.id.btnPlay,
						R.drawable.ic_notification_play);
				// Resume song
				// false;
			}
		}

		Intent switchIntent = new Intent(this, AlarmReceiverActivity.class);
		switchIntent.setAction(WidgetUtils.WIDGET_PLAY);
		PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
				switchIntent, 0);

		remoteViews.setOnClickPendingIntent(R.id.btnPlay, pendingSwitchIntent);

		switchIntent.setAction(WidgetUtils.WIDGET_EXIT);
		PendingIntent pendingExitIntent = PendingIntent.getBroadcast(this, 0,
				switchIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.btnExit, pendingExitIntent);

		switchIntent.setAction(WidgetUtils.WIDGET_ACTION_NEXT);
		PendingIntent pendingNextIntent = PendingIntent.getBroadcast(this, 0,
				switchIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.btnNext, pendingNextIntent);

		mBuilder.setSmallIcon(R.drawable.ic_player_icon);
		mBuilder.setContent(remoteViews);
		mBuilder.setAutoCancel(true);
		// mBuilder.setContentTitle(" –ﬂÌ— !");
		// mBuilder.setContentText("·«  ‰”Ï ﬁ—«¡… ÕœÌÀ «·ÌÊ„ „‰ ’ÕÌÕ „”·„!!");
		mBuilder.setContentIntent(pi);

		// ------------------
		startForeground(classID, mBuilder.build());
		// mp = MediaPlayer.create(this, R.raw.rain);
		// mp.setLooping(true);
		// mp.start();

	}

	public void Forward() {
		if (isActive) {
			// get current song position
			int currentPosition = mp.getCurrentPosition();
			// check if seekForward time is lesser than song duration
			if (currentPosition + seekForwardTime <= mp.getDuration()) {
				// forward song
				mp.seekTo(currentPosition + seekForwardTime);
			} else {
				// forward to end position
				mp.seekTo(mp.getDuration());
			}
		} else {

			PlayNewMp3();
		}
	}

	public void PlayTargetVerse(int index) {
		currentSongIndex = index;
		PlayNewMp3();
	}

	public void NextMp3() {
		if (currentSongIndex < (songsList.size() - 1)) {

			currentSongIndex = currentSongIndex + 1;

		} else {

			currentSongIndex = 0;
		}
		PlayNewMp3();
	}

	public void PrevMp3() {
		if (currentSongIndex > 0) {

			currentSongIndex = currentSongIndex - 1;
		} else {

			currentSongIndex = songsList.size() - 1;
		}
		PlayNewMp3();
	}

	public void Backward() {
		if (isActive) {
			// get current song position
			int currentPosition = mp.getCurrentPosition();
			// check if seekBackward time is greater than 0 sec
			if (currentPosition - seekBackwardTime >= 0) {
				// forward song
				mp.seekTo(currentPosition - seekBackwardTime);
			} else {
				// backward to starting position
				mp.seekTo(0);
			}
		} else {

			PlayNewMp3();
		}
	}

	public void TogglePlay() {

		if (isActive) {

			// check for already playing
			if (mp.isPlaying()) {
				if (mp != null) {
					mp.pause();
					for (int i = mListeners.size() - 1; i >= 0; i--) {
						mListeners.get(i).TogglePlay(false);
					}
				}
			} else {
				// Resume song
				if (mp != null) {
					mp.start();
					for (int i = mListeners.size() - 1; i >= 0; i--) {
						mListeners.get(i).TogglePlay(true);
					}
				}
			}
			if (isShowNotification)
				BuildNotification();
		} else {

			PlayNewMp3();
		}
	}

	public void ReleaseMediaPlayer() {
		getTask.cancel(true);
		mp.release();
		isActive = false;
	}

	public void CancleLoading() {
		getTask.cancel(true);
		isActive = false;
	}

	public AudioClass getCurrentVerse() {
		return verse;
	}

	public void PlayNewMp3() {
		if (songsList == null)
			setList();
		if (currentSongIndex >= songsList.size())
			currentSongIndex = 0;
		if (songsList.size() == 0) {
			AudioListManager audioListManager = AudioListManager.getInstance();
			audioListManager.FillRandomAudio();
			songsList = audioListManager.getPlayList();
		}

		verse = songsList.get(currentSongIndex);
		if (isShowNotification)
			BuildNotification();
		isActive = true;
		isPrepared = false;
		for (int i = mListeners.size() - 1; i >= 0; i--) {
			mListeners.get(i).onInitNewMp3();
		}
		getTask = new GetTask();
		getTask.execute();
	}

	public long getDuration() {
		return mp.getDuration();
	}

	public long getCurrentPosition() {
		return mp.getCurrentPosition();
	}

	public void SeekTo(int position) {
		mp.seekTo(position);
	}

	@Override
	public void onDestroy() {
		isMainAppRunning = false;
		Log.e("onDestroy", "onDestroy service");

		stop();
	}

	private static Boolean isShowNotification = false;

	public void ShowNotification() {
		isShowNotification = true;
		BuildNotification();
	}

	public void HideNotification() {
		isShowNotification = false;
		stopForeground(true);
	}

	private void stop() {
		if (isPlaying) {
			isPlaying = false;
			if (mp != null) {
				mp.release();
				mp = null;
			}
			stopForeground(true);
		}
	}

	boolean isRepeat = false;
	boolean isShuffle = false;

	public void ToggleRepeat() {
		isRepeat = !isRepeat;
		for (int i = mListeners.size() - 1; i >= 0; i--) {
			mListeners.get(i).onToggleRepeat(!isRepeat);
		}
	}

	public void ToggleShuffle() {
		isShuffle = !isShuffle;
		for (int i = mListeners.size() - 1; i >= 0; i--) {
			mListeners.get(i).onToggleShuffle(!isShuffle);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		// check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same song again
			PlayNewMp3();
		} else if (isShuffle) {
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			PlayNewMp3();
		} else {
			// no repeat or shuffle ON - play next song
			if (currentSongIndex < (songsList.size() - 1)) {

				currentSongIndex = currentSongIndex + 1;
				PlayNewMp3();
			} else {
				// play first song

				currentSongIndex = 0;
				PlayNewMp3();
			}
		}
		// TODO Auto-generated method stub
		for (int i = mListeners.size() - 1; i >= 0; i--) {
			mListeners.get(i).onCompletionMp3();
		}

	}

	// //player Loading
	private GetTask getTask = null;

	private class GetTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {

			return GetData();
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result != -1) {
				mp.start();
				if (isShowNotification)
					BuildNotification();
				for (int i = mListeners.size() - 1; i >= 0; i--) {
					mListeners.get(i).onstartMp3();
				}

			} else {

				getTask.cancel(true);
				for (int i = mListeners.size() - 1; i >= 0; i--) {
					mListeners.get(i).onErrorMp3();
				}
			}

		}
	}

	private Integer GetData() {

		try {

			String verseAudioPath = verse.getAudioPath();
			int reciterId = verse.getReciterId();
			String versesId = Utils.getAudioMp3Name(verse.getVerseId());
			if (Utils.isFileExist(reciterId, versesId)) {
				verseAudioPath = Utils.getLocalPath(reciterId) + "/" + versesId;
			}
			Log.e("sss", verseAudioPath);
			mp.reset();
			try {
				mp.setDataSource(verseAudioPath);
			} catch (IllegalStateException e) {
				mp.reset();
				mp.setDataSource(verseAudioPath);
			}

			mp.prepare();

		} catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated method stub

			return -1;

		}

		return verse.getVerseId();
	}

}
