package com.quranmp3;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.quranmp3.model.AudioClass;
import com.quranmp3.model.DownloadClass;

public class DownloadService extends Service {

	private MediaPlayer mp = null;
	private final IBinder dBinder = new DownloadBinder();
	private DownloadInterfaceListener mCallback;
	private static int classID = 771; // just a number
	private final ArrayList<DownloadInterfaceListener> mListeners = new ArrayList<DownloadInterfaceListener>();
	private final ArrayList<DownloadClass> downloads = new ArrayList<DownloadClass>();

	public DownloadService() {

	}

	@Override
	public void onDestroy() {
		Log.e("Download Destroy", "Download Destroy");
		stopForeground(true);

	}

	public interface DownloadInterfaceListener {
		public void onDownloadPreExecute(DownloadClass downloadClass);

		public void onDownloadPostExecute(DownloadClass downloadClass);

		public void onDownloadProgressUpdate(DownloadClass downloadClass,
				Integer progress);

		public void onDownloadError(DownloadClass downloadClass);

	}

	public void registerListener(DownloadInterfaceListener listener) {
		mListeners.add(listener);
	}

	public void unregisterListener(DownloadInterfaceListener listener) {
		mListeners.remove(listener);
	}

	public class DownloadBinder extends Binder {
		DownloadService getService() {
			return DownloadService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return dBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.e("start", "download service");
		return Service.START_STICKY;
	}

	public void BuildNotification(int _id, DownloadClass downloadClass) {

		final int id = _id;
		final DownloadClass _downloadClass = downloadClass;
		final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final Builder mBuilder = new NotificationCompat.Builder(this);
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		mBuilder.setContentIntent(pi);
		mBuilder.setAutoCancel(true);
		String image = downloadClass.getAudioClass().getImage().split(".jpg")[0];

		int reciterIconId = this.getResources().getIdentifier(image,
				"drawable", this.getPackageName());

		mBuilder.setContentTitle(
				getResources().getString(R.string.reciters_pre) + " "
						+ downloadClass.getAudioClass().getReciterName())
				.setContentText(
						getResources().getString(R.string.download_progress)
								+ " "
								+ downloadClass.getAudioClass().getVerseName()
								+ " - " + _downloadClass.getProgress() + " %")
				.setSmallIcon(reciterIconId);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (_downloadClass.getProgress() < 100) {
						mBuilder.setProgress(100, _downloadClass.getProgress(),
								false);
						mBuilder.setContentText(getResources().getString(
								R.string.download_progress)
								+ " "
								+ _downloadClass.getAudioClass().getVerseName()
								+ " - " + _downloadClass.getProgress() + " %");
						mNotifyManager.notify(id, mBuilder.build());
						// Sleep for 5 seconds
						Thread.sleep(3 * 1000);
					}
					if (_downloadClass.getProgress() < 200) {
						mBuilder.setProgress(100, 100, false);
						mBuilder.setContentText(getResources().getString(
								R.string.download_complete_pre)
								+ " "
								+ _downloadClass.getAudioClass().getVerseName()
								+ getResources().getString(
										R.string.download_complete_after) + " ");
						mNotifyManager.notify(id, mBuilder.build());
					}

					for (int j = 0; j < downloads.size(); j++) {
						if (downloads.get(j).getId() == id) {

							downloads.remove(j);
							break;
						}
					}
					Log.e("downloads Size", downloads.size() + "");
					if (downloads.isEmpty()) {
						Log.e("downloads Size", "empty");
						Intent _intent = new Intent(getApplicationContext(),
								DownloadService.class);
						stopService(_intent);
					}
					Log.e("downloads Size", "cancel: " + id);
					mNotifyManager.cancel(id);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

			}
		}).start();

		startForeground(id, mBuilder.build());

	}

	public ArrayList<DownloadClass> getDownloads() {
		return downloads;
	}

	int idCounter = 0;

	public Boolean CheckSuraIsDownloading(AudioClass audioClass) {
		for (int i = 0; i < downloads.size(); i++) {
			AudioClass _audioClass = downloads.get(i).getAudioClass();
			if (audioClass.getReciterId() == _audioClass.getReciterId()
					&& audioClass.getVerseId() == _audioClass.getVerseId()) {
				return true;
			}
		}
		return false;
	}

	public void DownloadSura(AudioClass audioClass) {
		Log.e("Download now", audioClass.getReciterName());
		DownloadClass downloadClass = new DownloadClass();
		downloadClass.setAudioClass(audioClass);
		downloadClass.setId(idCounter + classID);
		downloadClass.setProgress(-1);
		downloads.add(downloadClass);
		BuildNotification(idCounter + classID, downloadClass);
		idCounter++;
		DownloaderThread downloaderThread = new DownloaderThread(downloadClass,
				DownloadService.this);
		downloaderThread.start();
	}

	public Handler activityHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*
			 * Handling MESSAGE_UPDATE_PROGRESS_BAR: 1. Get the current
			 * progress, as indicated in the arg1 field of the Message. 2.
			 * Update the progress bar.
			 */
			case MESSAGE_UPDATE_PROGRESS_BAR:

				break;

			/*
			 * Handling MESSAGE_CONNECTING_STARTED: 1. Get the URL of the file
			 * being downloaded. This is stored in the obj field of the Message.
			 * 2. Create an indeterminate progress bar. 3. Set the message that
			 * should be sent if user cancels. 4. Show the progress bar.
			 */
			case MESSAGE_CONNECTING_STARTED:

				break;

			/*
			 * Handling MESSAGE_DOWNLOAD_STARTED: 1. Create a progress bar with
			 * specified max value and current value 0; assign it to
			 * progressDialog. The arg1 field will contain the max value. 2. Set
			 * the title and text for the progress bar. The obj field of the
			 * Message will contain a String that represents the name of the
			 * file being downloaded. 3. Set the message that should be sent if
			 * dialog is canceled. 4. Make the progress bar visible.
			 */
			case MESSAGE_DOWNLOAD_STARTED:
				Log.e("started", "started");
				Log.e("args", msg.arg1 + "");
				for (int i = mListeners.size() - 1; i >= 0; i--) {
					mListeners.get(i).onDownloadPreExecute(null);
				}
				// obj will contain a String representing the file name

				break;

			/*
			 * Handling MESSAGE_DOWNLOAD_COMPLETE: 1. Remove the progress bar
			 * from the screen. 2. Display Toast that says download is complete.
			 */
			case MESSAGE_DOWNLOAD_COMPLETE:
				for (int j = 0; j < downloads.size(); j++) {
					if (downloads.get(j).getId() == msg.arg1) {
						for (int i = mListeners.size() - 1; i >= 0; i--) {
							mListeners.get(i).onDownloadPostExecute(
									downloads.get(j));
						}

						// downloads.remove(j);
						break;
					}
				}
				break;

			/*
			 * Handling MESSAGE_DOWNLOAD_CANCELLED: 1. Interrupt the downloader
			 * thread. 2. Remove the progress bar from the screen. 3. Display
			 * Toast that says download is complete.
			 */
			case MESSAGE_DOWNLOAD_CANCELED:

				break;

			/*
			 * Handling MESSAGE_ENCOUNTERED_ERROR: 1. Check the obj field of the
			 * message for the actual error message that will be displayed to
			 * the user. 2. Remove any progress bars from the screen. 3. Display
			 * a Toast with the error message.
			 */
			case MESSAGE_ENCOUNTERED_ERROR:
				for (int j = 0; j < downloads.size(); j++) {
					if (downloads.get(j).getId() == msg.arg1) {
						for (int i = mListeners.size() - 1; i >= 0; i--) {
							mListeners.get(i).onDownloadError(downloads.get(j));
						}
						downloads.get(j).setProgress(1000);
						// downloads.remove(j);
						break;
					}
				}
				break;

			default:
				// nothing to do here
				break;
			}
		}
	};
	public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
	public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
	public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
	public static final int MESSAGE_CONNECTING_STARTED = 1004;
	public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

}
