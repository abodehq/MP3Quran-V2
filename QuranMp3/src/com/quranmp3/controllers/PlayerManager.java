package com.quranmp3.controllers;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;

import com.quranmp3.model.Verses;

public class PlayerManager implements OnCompletionListener {

	private static PlayerManager instance = null;
	// Media Player
	private MediaPlayer mp = null;
	private boolean isPrepared = false;
	private boolean isActive = true;
	private Verses verse;
	private int currentVerseIndex;

	public PlayerManager() {
		InitMP();
	}

	public static PlayerManager getInstance() {
		if (instance == null) {
			instance = new PlayerManager();
		}
		return instance;
	}

	public MediaPlayer getMP() {
		return mp;
	}

	public void InitMP() {
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

				return false;
			}
		});
	}

	public void PlayNewMp3(Verses _verse) {
		verse = _verse;
		isPrepared = false;
		getTask = new GetTask();
		getTask.execute();
	}

	public void SeekTo(int position) {
		mp.seekTo(position);
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub

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

			} else {

				getTask.cancel(true);
			}

		}
	}

	private Integer GetData() {

		try {

			String verseAudioPath = verse.getAudioPath();
			mp.reset();
			try {
				mp.setDataSource(verseAudioPath);
			} catch (IllegalStateException e) {
				mp.reset();
				mp.setDataSource(verseAudioPath);
			}

			mp.prepare();

		} catch (Exception e) {
			return null;

		}

		return verse.getId();
	}

}
