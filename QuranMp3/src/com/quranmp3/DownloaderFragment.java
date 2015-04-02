package com.quranmp3;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.DownloadService.DownloadBinder;
import com.quranmp3.DownloadService.DownloadInterfaceListener;
import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.DownloadClass;
import com.quranmp3.model.Reciters;
import com.quranmp3.utils.SlidingPanel;

public class DownloaderFragment extends Fragment implements
		DownloadInterfaceListener {

	private View view;
	private ListView lv_verses;
	private DownloaderItemAdapter downloaderItemAdapter;
	private DownloaderFragment _scope;

	// action id
	private static final int ID_READ = 1;
	private static final int ID_COPY = 2;
	private static final int ID_NOTE = 3;
	QuickAction quickAction;
	SlidingPanel popup;

	MainActivity _FragmentActivity;

	// connect to service
	DownloadService dService;
	boolean dBound = false;
	private ServiceConnection dConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			DownloadBinder binder = (DownloadBinder) service;
			dService = binder.getService();
			dBound = true;
			dService.registerListener(DownloaderFragment.this);

			boolean res = setupUI();
			if (dService.getDownloads().isEmpty()) {

				dService.unregisterListener(DownloaderFragment.this);

				_FragmentActivity.unbindService(dConnection);
				dBound = false;

			}
			Log.e("server", "Download connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.e("server", "Download disconnect");
			dBound = false;
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_FragmentActivity = (MainActivity) activity;
		Intent intent = new Intent(_FragmentActivity, DownloadService.class);
		_FragmentActivity.bindService(intent, dConnection,
				Context.BIND_AUTO_CREATE);

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

	View emptyView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionbar;
		actionbar = this.getActivity().getActionBar();
		// actionbar.setTitle(getResources().getString(R.string.chapters));
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.ly_downloader, container, false);

		_scope = this;
		// updateReciterInfo();
		lv_verses = (ListView) view.findViewById(R.id.lv_verses);

		return view;
	}

	public void replaceView() {

		RelativeLayout parentView = (RelativeLayout) view
				.findViewById(R.id.rootView);

		LayoutInflater inflater = (LayoutInflater) _FragmentActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View noInternetView = inflater.inflate(R.layout.ly_fav_empty, null);
		parentView.addView(noInternetView);

	}

	private void updateReciterInfo1() {
		// reciter info section
		String reciters_pre = _FragmentActivity.getResources().getString(
				R.string.reciters_pre)
				+ " ";

		AudioListManager audioListManager = AudioListManager.getInstance();
		Reciters reciter = audioListManager.getSelectedReciter();

		TextView songTitleLabel = (TextView) view.findViewById(R.id.songTitle);

		String verseReciter = reciter.getName();
		songTitleLabel.setText(reciters_pre + verseReciter);

		ImageView image_icon = (ImageView) view.findViewById(R.id.list_image);

		String image = reciter.getImage().split(".jpg")[0];
		Context context = image_icon.getContext();
		int id = context.getResources().getIdentifier(image, "drawable",
				context.getPackageName());
		image_icon.setImageDrawable(_FragmentActivity.getResources()
				.getDrawable(id));

		ImageView country_icon = (ImageView) view
				.findViewById(R.id.country_icon);
		String countryImage = "country" + reciter.getCountryId();
		context = country_icon.getContext();
		id = context.getResources().getIdentifier(countryImage, "drawable",
				context.getPackageName());
		country_icon.setImageDrawable(_FragmentActivity.getResources()
				.getDrawable(id));

		//
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.e("result", "result_fragment");

		if (requestCode == 1) {

			// Make sure the request was successful
			if (resultCode == Activity.RESULT_OK) {

			}
		}
	}

	private void updateView(int index) {
		View v = lv_verses.getChildAt(index
				- lv_verses.getFirstVisiblePosition());

		if (v == null)
			return;

		TextView someText = (TextView) v.findViewById(R.id.txt_verses_name);
		someText.setText("Hi! I updated you manually!");
	}

	private Boolean setupUI() {
		if (dBound) {

			if (dService.getDownloads().isEmpty()) {
				replaceView();
				return true;
			}

			Log.e("-----------", dService.getDownloads().size() + "");
			downloaderItemAdapter = new DownloaderItemAdapter(_scope,
					dService.getDownloads());

			lv_verses.setAdapter(downloaderItemAdapter);
		}
		lv_verses.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				quickAction.position = pos;
				quickAction.show(arg1);
				return false;
			}
		});
		lv_verses.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				updateView(1);

			}

		});
		int top = lv_verses.getTop();
		// lv_verses.setSelectionFromTop(
		// chapterslistManager.getCurrentSelectedChapterPosition(), top);
		// downloaderItemAdapter.setSelectedItem(chapterslistManager
		// .getCurrentSelectedChapterPosition());
		ActionItem nextItem = new ActionItem(ID_READ, getResources().getString(
				R.string.quick_action_read), _FragmentActivity.getResources()
				.getDrawable(R.drawable.ic_player_icon));
		ActionItem prevItem = new ActionItem(ID_COPY, getResources().getString(
				R.string.quick_action_copy), _FragmentActivity.getResources()
				.getDrawable(R.drawable.ic_playlist));
		ActionItem noteItem = new ActionItem(ID_NOTE, getResources().getString(
				R.string.quick_action_add_note), _FragmentActivity
				.getResources().getDrawable(R.drawable.ic_verses_icon));

		// orientation
		quickAction = new QuickAction(_FragmentActivity, QuickAction.VERTICAL);

		// add action items into QuickAction
		quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
		quickAction.addActionItem(noteItem);
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						// here we can filter which action item was clicked with
						// pos or actionId parameter
						if (actionId == ID_READ) {

						} else if (actionId == ID_COPY) {

						} else if (actionId == ID_NOTE) {

						}
					}
				});
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				TimerMethod();
			}

		}, 0, 3000);
		return true;
	}

	Timer myTimer = null;

	private void TimerMethod() {
		// This method is called directly by the timer
		// and runs in the same thread as the timer.

		// We call the method that will work with the UI
		// through the runOnUiThread method.
		_FragmentActivity.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			if (dBound) {
				downloaderItemAdapter = new DownloaderItemAdapter(_scope,
						dService.getDownloads());
				lv_verses.setAdapter(downloaderItemAdapter);
				downloaderItemAdapter.notifyDataSetChanged();
			}
			// This method runs in the same thread as the UI.

			// Do something to the UI thread here

		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (myTimer != null)
			myTimer.cancel();
		Log.e("destry", "destroy");
		if (dBound) {
			if (dService != null) {
				dService.unregisterListener(this);
			}
			_FragmentActivity.unbindService(dConnection);
			dBound = false;
		}
	}

	@Override
	public void onResume() {

		super.onResume();
		if (!dBound) {

			Intent intent = new Intent(_FragmentActivity, DownloadService.class);
			_FragmentActivity.bindService(intent, dConnection,
					Context.BIND_AUTO_CREATE);
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		if (dBound) {
			dBound = false;
			if (dService != null) {
				dService.unregisterListener(this);
			}
			_FragmentActivity.unbindService(dConnection);

		}

	}

	@Override
	public void onDownloadPreExecute(DownloadClass downloadClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownloadPostExecute(DownloadClass downloadClass) {
		if (dBound) {
			if (dService.getDownloads().isEmpty()) {

				if (myTimer != null)
					myTimer.cancel();
				dService.unregisterListener(this);
				_FragmentActivity.unbindService(dConnection);
				dBound = false;

			}
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownloadProgressUpdate(DownloadClass downloadClass,
			Integer progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownloadError(DownloadClass downloadClass) {
		// TODO Auto-generated method stub

	}
}