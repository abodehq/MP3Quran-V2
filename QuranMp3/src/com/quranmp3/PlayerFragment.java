package com.quranmp3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.quranmp3.MyMediaPlayerService.LocalBinder;
import com.quranmp3.MyMediaPlayerService.PlayerInterfaceListener;
import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.utils.Utilities;
import com.quranmp3.utils.Utils;

public class PlayerFragment extends Fragment implements OnCompletionListener,
		SeekBar.OnSeekBarChangeListener, PlayerInterfaceListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songReciterLabel;
	private RelativeLayout rl_download;
	private RelativeLayout rl_share;

	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private AudioListManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<AudioClass> songsList = new ArrayList<AudioClass>();
	private boolean isPrepared = false;

	private boolean isActive = true;
	MainActivity _FragmentActivity;
	View rootView;

	// connect to service
	MyMediaPlayerService mService;
	boolean mBound = false;

	@Override
	public void onStart() {
		super.onStart();
		Log.e("oon start", "on start");
		if (mBound)
			mService.HideNotification();
		// Bind to LocalService
		// Intent intent = new Intent(_FragmentActivity,
		// MyMediaPlayerService.class);
		// _FragmentActivity.bindService(intent, mConnection,
		// Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("oon stop", "on stop");
		// Unbind from the service
		if (mBound) {
			mService.ShowNotification();
			// _FragmentActivity.unbindService(mConnection);
			// mBound = false;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			mService.registerListener(PlayerFragment.this);
			InitMP();
			Log.e("server", "connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.e("server", "disconnect");
			mBound = false;
		}
	};

	public void InitMP() {
		if (mBound) {
			AudioListManager audioListManager = AudioListManager.getInstance();
			if (audioListManager.getUpdatePlayerStatus()) {

				mService.PlayNewMp3();

				audioListManager.setUpdatePlayerStatus(false);
			} else {
				HashMap<String, Integer> mpStatus = mService.getMPStatus();
				Log.e("mpStatus.get", mpStatus.get("playing") + "");
				if (mpStatus.get("playing") == 1)
					btnPlay.setImageResource(R.drawable.btn_pause);
				if (mpStatus.get("playing") == 0)
					btnPlay.setImageResource(R.drawable.btn_play);
				if (mpStatus.get("playing") != -1) {
					SetVersesInfo();
					updateProgressBar();
				} else {

					mService.TogglePlay();

				}
				if (mpStatus.get("shuffle") == 0) {

					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				} else {
					// make repeat to true

					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);

				}
				if (mpStatus.get("repeat") == 0) {

					btnRepeat.setImageResource(R.drawable.btn_repeat);
				} else {

					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);

				}
			}
		}
	}

	// ////

	// end
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_FragmentActivity = (MainActivity) activity;

		Intent intent = new Intent(_FragmentActivity.getApplicationContext(),
				MyMediaPlayerService.class);
		intent.putExtra(MyMediaPlayerService.START_PLAY, true);
		_FragmentActivity.startService(intent);

		intent = new Intent(_FragmentActivity, MyMediaPlayerService.class);
		_FragmentActivity.bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);

		Log.e("oon Attach", "onattach");

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Fragment _fragment = null;
		_fragment = new AdmobFragment();
		FragmentManager _fragmentManager = getFragmentManager();

		_fragmentManager.beginTransaction()
				.add(R.id.admob_container, _fragment).commit();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.ly_player, container, false);

		rl_download = (RelativeLayout) rootView.findViewById(R.id.rl_download);
		rl_share = (RelativeLayout) rootView.findViewById(R.id.rl_share);
		// All player buttons
		btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
		btnForward = (ImageButton) rootView.findViewById(R.id.btnForward);
		btnBackward = (ImageButton) rootView.findViewById(R.id.btnBackward);
		btnNext = (ImageButton) rootView.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) rootView.findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) rootView.findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) rootView.findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) rootView.findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) rootView.findViewById(R.id.songTitle);
		songReciterLabel = (TextView) rootView.findViewById(R.id.songTitle1);

		songCurrentDurationLabel = (TextView) rootView
				.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) rootView
				.findViewById(R.id.songTotalDurationLabel);

		songManager = AudioListManager.getInstance();
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important

		// Getting all songs list
		songsList = songManager.getPlayList();

		// CreateMediaPlayer();
		// By default play first song
		// playSong(0);

		rl_download.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					AudioClass audioClass = mService.getCurrentVerse();
					_FragmentActivity.DownloadSura(audioClass);
				}

			}
		});
		rl_share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					// mService.TogglePlay();
					AudioClass audioClass = mService.getCurrentVerse();
					Utils.shareMp3(_FragmentActivity, audioClass);
				}

			}
		});

		/**
		 * Play button click event plays a song and changes button to pause
		 * image pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					mService.TogglePlay();
				}

			}
		});

		/**
		 * Forward button click event Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					mService.Forward();
				}
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					mService.Backward();
				}
			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if (mBound) {
					mService.NextMp3();
				}

			}
		});

		/**
		 * Back button click event Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound) {
					mService.PrevMp3();
				}

			}
		});

		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mBound)
					mService.ToggleRepeat();

			}
		});

		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mBound)
					mService.ToggleShuffle();

			}
		});

		/**
		 * Button Click event for Play list click event Launches list activity
		 * which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(
						_FragmentActivity.getApplicationContext(),
						PlayerPlayListActivity.class);// PlayerPlayListActivity//
														// CursorDSLV
				startActivityForResult(i, 101);
			}
		});

		return rootView;
	}

	/**
	 * Receiving song index from playlist view and play the song
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.getExtras() != null) {
					Log.e("result code : ",
							"" + data.getExtras().getInt("_type"));
					if (data.getExtras().getInt("_type") == 1) {
						currentSongIndex = data.getExtras().getInt("songIndex");
						if (mBound) {
							mService.PlayTargetVerse(currentSongIndex);
						}
					}
				}
			}

			// play selected song
			// playSong(currentSongIndex);
		}

	}

	/**
	 * Function to play a song
	 * 
	 * @param songIndex
	 *            - index of song
	 * */
	Dialog dialog;

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			try {
				isPrepared = false;
				if (mBound) {
					isPrepared = mService.getIsprepared();
				}
				if (isPrepared) {

					long totalDuration = 0;
					long currentDuration = 0;
					if (mBound) {
						totalDuration = mService.getDuration();
						currentDuration = mService.getCurrentPosition();
					}

					// Displaying Total Duration time
					songTotalDurationLabel.setText(""
							+ utils.milliSecondsToTimer(totalDuration));
					// Displaying time completed playing
					songCurrentDurationLabel.setText(""
							+ utils.milliSecondsToTimer(currentDuration));

					// Updating progress bar
					int progress = (int) (utils.getProgressPercentage(
							currentDuration, totalDuration));
					// Log.d("Progress", ""+progress);
					songProgressBar.setProgress(progress);

					// Running this thread after 100 milliseconds
					mHandler.postDelayed(this, 100);
				}

			} catch (Exception e) {

			}
		}
	};

	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		mHandler.removeCallbacks(mUpdateTimeTask);
		if (isActive) {

			long totalDuration = 0;
			if (mBound) {
				totalDuration = mService.getDuration();
			}
			int currentPosition = utils.progressToTimer(seekBar.getProgress(),
					(int) (totalDuration));

			// forward or backward to certain seconds
			// mp.seekTo(currentPosition);
			if (mBound) {
				mService.SeekTo(currentPosition);
			}

			// update timer progress again
			updateProgressBar();
		} else {

			// playSong(currentSongIndex);
		}
	}

	/**
	 * On Song Playing completed if repeat is ON play same song again if shuffle
	 * is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.e("destry", "destroy");
		if (mBound) {
			mService.ShowNotification();
		}
		// Intent intent = new Intent(_FragmentActivity.getApplicationContext(),
		// MyMediaPlayerService.class);
		// intent.putExtra(MyMediaPlayerService.START_PLAY, true);
		// _FragmentActivity.startService(intent);
		if (mBound) {
			if (mService != null) {
				mService.unregisterListener(this);
			}
			_FragmentActivity.unbindService(mConnection);
			mBound = false;
		}
	}

	ListView list;

	public void ShowErrorDialog() {

		showAlertDialog(_FragmentActivity, _FragmentActivity.getResources()
				.getString(R.string.interent_error_title), _FragmentActivity
				.getResources().getString(R.string.interent_error), false);

	}

	private void showAlertDialog(Context context, String title, String message,
			Boolean status) {

		if (((Activity) context).isFinishing() == false) {
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();

			// Setting Dialog Title
			alertDialog.setTitle(title);

			// Setting Dialog Message
			alertDialog.setMessage(message);

			// Setting alert dialog icon
			alertDialog.setIcon(R.drawable.fail);

			alertDialog.setButton("Try Again!!",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							if (Utils.isConnectingToInternet(_FragmentActivity)) {

								if (mBound) {
									dialog.cancel();
									mService.ReleaseMediaPlayer();
									mService.CreateMediaPlayer();
									mService.TogglePlay();
								}

							} else {
								dialog.cancel();
								ShowErrorDialog();
							}

						}
					});
			alertDialog.show();
		}

	}

	@Override
	public void onCompletionMp3() {
		// TODO Auto-generated method stub
		Log.e("compl", "el7mdllah");

	}

	@Override
	public void onErrorMp3() {
		if (dialog != null)
			dialog.dismiss();
		// TODO Auto-generated method stub
		btnPlay.setImageResource(R.drawable.btn_play);
		// set Progress bar values
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);

		SetVersesInfo();

		// Updating progress bar

		// TODO Auto-generated method stub
		if (!Utils.isConnectingToInternet(_FragmentActivity)) {
			showAlertDialog(
					_FragmentActivity,
					_FragmentActivity.getResources().getString(
							R.string.interent_error_title),
					_FragmentActivity.getResources().getString(
							R.string.interent_error), false);
		} else {
			showAlertDialog(_FragmentActivity, _FragmentActivity.getResources()
					.getString(R.string.player_error_title), _FragmentActivity
					.getResources().getString(R.string.player_error), false);
		}

	}

	@Override
	public void isPreparedMp3() {
		// TODO Auto-generated method stub
		isPrepared = true;

	}

	@Override
	public void TogglePlay(Boolean status) {

		if (status)
			btnPlay.setImageResource(R.drawable.btn_pause);
		else
			btnPlay.setImageResource(R.drawable.btn_play);

		// TODO Auto-generated method stub

	}

	@Override
	public void onstartMp3() {
		if (dialog != null)
			dialog.dismiss();
		// TODO Auto-generated method stub
		btnPlay.setImageResource(R.drawable.btn_pause);
		// set Progress bar values
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);

		SetVersesInfo();

		// Updating progress bar
		updateProgressBar();

	}

	public void SetVersesInfo() {
		AudioClass verse = null;
		if (mBound) {
			verse = mService.getCurrentVerse();
		}
		if (verse != null) {

			String reciters_pre = _FragmentActivity.getResources().getString(
					R.string.reciters_pre)
					+ " ";
			String verses_pre = _FragmentActivity.getResources().getString(
					R.string.verses_pre)
					+ " ";

			String verseTitle = verse.getVerseName();
			songTitleLabel.setText(verses_pre + verseTitle);

			String verseReciter = verse.getReciterName();
			songReciterLabel.setText(reciters_pre + verseReciter);

			ImageView image_icon = (ImageView) rootView
					.findViewById(R.id.list_image);

			String image = verse.getImage().split(".jpg")[0];
			Context context = image_icon.getContext();
			int id = context.getResources().getIdentifier(image, "drawable",
					context.getPackageName());
			image_icon.setImageDrawable(_FragmentActivity.getResources()
					.getDrawable(id));

		}
	}

	@Override
	public void onInitNewMp3() {

		dialog = new Dialog(_FragmentActivity);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog);
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		final Animation myRotation = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.rotator);
		image.startAnimation(myRotation);
		RelativeLayout btn_cancel = (RelativeLayout) dialog
				.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBound)
					mService.CancleLoading();
				dialog.dismiss();
				// TODO Auto-generated method stub
				btnPlay.setImageResource(R.drawable.btn_play);
				// set Progress bar values
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				// Displaying Total Duration time
				songTotalDurationLabel.setText("");
				// Displaying time completed playing
				songCurrentDurationLabel.setText("");

			}
		});
		// btn_cancel
		dialog.show();

	}

	@Override
	public void onToggleRepeat(Boolean bRepeat) {

		// TODO Auto-generated method stub
		if (bRepeat) {

			btnRepeat.setImageResource(R.drawable.btn_repeat);
		} else {

			btnRepeat.setImageResource(R.drawable.btn_repeat_focused);

		}

	}

	@Override
	public void onToggleShuffle(Boolean bShuffle) {
		// TODO Auto-generated method stub
		if (bShuffle) {

			btnShuffle.setImageResource(R.drawable.btn_shuffle);
		} else {
			// make repeat to true

			btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);

		}

	}
}