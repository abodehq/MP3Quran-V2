package com.quranmp3.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.quranmp3.PlayListsItemSmallAdapter;
import com.quranmp3.R;
import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.controllers.SharedPreferencesManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.model.Mp3PlayLists;
import com.quranmp3.model.Mp3PlayListsVerses;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public static boolean isFileExist(int folderId, String verseName) {
		String localPath = getLocalPath(folderId);
		File file = new File(localPath, verseName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static String getLocalPath(int folderId) {
		return Environment.getExternalStorageDirectory() + "/MP3Quran/"
				+ folderId;
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
												// '-' and decimal.
	}

	public static String getAudioMp3Name(int verseId) {
		String versesId = verseId + "";
		if (versesId.length() == 3)
			versesId = versesId + ".mp3";
		if (versesId.length() == 2)
			versesId = "0" + versesId + ".mp3";
		if (versesId.length() == 1)
			versesId = "00" + versesId + ".mp3";
		return versesId;
	}

	public static boolean deleteVerse(AudioClass audioClass) {
		File currentDir = new File("/sdcard/MP3Quran/"
				+ audioClass.getReciterId() + "/"
				+ getAudioMp3Name(audioClass.getVerseId()));
		if (currentDir.exists()) {
			return currentDir.delete();
		}
		return false;
	}

	public static boolean ifSuraDownloaded(Context context,
			AudioClass _audioClass) {
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/MP3Quran/" + _audioClass.getReciterId());
		String versesId = getAudioMp3Name(_audioClass.getVerseId()) + "";

		if (dir.exists()) {
			File from = new File(dir, versesId);

			if (from.exists())
				return true;
		}
		return false;

	}

	public static void shareMp3(Context context, AudioClass audioClass) {
		if (ifSuraDownloaded(context, audioClass)) {
			String sharePath = Environment.getExternalStorageDirectory()
					.getPath()
					+ "/MP3Quran/"
					+ audioClass.getReciterId()
					+ "/"
					+ getAudioMp3Name(audioClass.getVerseId());
			Uri uri = Uri.parse(sharePath);
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("audio/*");
			share.putExtra(Intent.EXTRA_STREAM, uri);
			context.startActivity(Intent.createChooser(share,
					"Share Sound File"));
		} else {
			ShowNotDownloaded(context, audioClass);
		}

	}

	public static void SharePath(Context context, AudioClass audioClass) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = context.getString(R.string.share_body)
				+ "http://server11.mp3quran.net/shatri/001.mp3";
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				context.getString(R.string.share_title));
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		context.startActivity(Intent.createChooser(sharingIntent,
				context.getString(R.string.share_title)));
	}

	private static void ShowNotDownloaded(Context context, AudioClass audioClass) {

		new AlertDialog.Builder(context)
				.setTitle(
						context.getResources().getString(R.string.share_title))
				.setMessage(
						context.getResources().getString(
								R.string.share_condition))
				.setPositiveButton(
						context.getResources().getString(
								R.string.app_exit_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static void showAddToPlaylist(Context context, AudioClass _audioClass) {
		final Dialog dialog = new Dialog(context);
		final AudioClass audioClass = _audioClass;
		final ArrayList<Mp3PlayLists> playLists = GlobalConfig.GetmyDbHelper()
				.get_play_lists();
		dialog.setTitle(context.getResources().getString(
				R.string.play_list_title));
		ListView modeList = new ListView(context);
		final Context _context = context;
		modeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				int playlistId = playLists.get(position).getId();
				AudioListManager audioListManager = AudioListManager
						.getInstance();

				Mp3PlayListsVerses mp3PlayListsVerses = new Mp3PlayListsVerses();
				mp3PlayListsVerses.setPlayListId(playlistId);
				mp3PlayListsVerses.setReciterId(audioClass.getReciterId());
				mp3PlayListsVerses.setVerseId(audioClass.getVerseId());
				if (GlobalConfig.GetmyDbHelper().get_play_list_verses_exist(
						mp3PlayListsVerses))
					GlobalConfig.GetmyDbHelper().insert_playlist_verses(
							mp3PlayListsVerses);
				else
					GlobalConfig.ShowErrorToast(
							_context,
							_context.getResources().getString(
									R.string.play_list_verse_exist));

				dialog.cancel();

			}
		});

		if (playLists.size() == 0) {
			GlobalConfig.ShowErrorToast(context, context.getResources()
					.getString(R.string.no_play_list));

			dialog.cancel();
		} else {

			PlayListsItemSmallAdapter adapter1 = new PlayListsItemSmallAdapter(
					context, playLists);

			modeList.setAdapter(adapter1);
			dialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			dialog.setContentView(modeList);
			dialog.show();
		}
	}

	public static void updateLocal(Context context, String lang_local,
			String lang_id) {
		// SharedPreferences sharedPreferences = PreferenceManager
		// .getDefaultSharedPreferences(context);
		// String _local = sharedPreferences.getString("languages_preference",
		// "ar");
		// Log.e("_local", _local);
		GlobalConfig.local = lang_local;
		GlobalConfig.lang_id = lang_id;
		Locale locale = new Locale(lang_local);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;

		context.getResources().updateConfiguration(config,
				context.getResources().getDisplayMetrics());

		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(context);
		sharedPreferencesManager.savePreferences(
				SharedPreferencesManager._lang_id, lang_id);
		sharedPreferencesManager.savePreferences(
				SharedPreferencesManager._local, lang_local);
	}

}