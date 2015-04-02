package com.quranmp3;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.mobeta.android.demodslv.DragSortListView;
import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.utils.SlidingPanel;
import com.quranmp3.utils.Utils;

public class PlayerPlayListActivity extends Activity {

	private DragSortListView lv_audios;
	private PlayerPlayItemAdapter audiosItemAdapter;

	// action id
	private static final int ID_DOWNLOAD = 1;
	private static final int ID_DELETE = 2;
	private static final int ID_PLAY = 3;
	private static final int ID_ADD_PLAY_LIST = 4;
	QuickAction quickAction;
	SlidingPanel popup;
	private Context context;
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			audiosItemAdapter.ReOrder(from, to);
			audiosItemAdapter.notifyDataSetChanged();
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			audiosItemAdapter.RemoveReciter(which);
			audiosItemAdapter.notifyDataSetChanged();
		}
	};

	/** Called when the activity is first created. */
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_player_play_list);

		ActionBar actionbar;
		actionbar = getActionBar();
		actionbar.setTitle(getResources().getString(R.string.menu_queue));
		actionbar.setIcon(R.drawable.ic_playlist_icon);

		lv_audios = (DragSortListView) findViewById(R.id.lv_audios);
		AudioListManager audioListManager = AudioListManager.getInstance();
		ArrayList<AudioClass> audioClass = audioListManager.getPlayList();
		audiosItemAdapter = new PlayerPlayItemAdapter(this, audioClass);
		lv_audios.setDropListener(onDrop);
		lv_audios.setRemoveListener(onRemove);
		lv_audios.setAdapter(audiosItemAdapter);
		lv_audios.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				// quickAction.position = pos;
				// quickAction.show(arg1);
				quickAction.position = pos;
				quickAction.show(arg1);
				return false;
			}
		});
		lv_audios.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				Intent in = new Intent(getApplicationContext(),
						PlayerFragment.class);
				in.putExtra("_type", 1);
				in.putExtra("songIndex", position);
				setResult(Activity.RESULT_OK, in);
				finish();

			}

		});
		// quick Action
		ActionItem nextItem = new ActionItem(ID_PLAY, getResources().getString(
				R.string.qa_play_verse), getResources().getDrawable(
				R.drawable.ic_player_icon));
		ActionItem prevItem = new ActionItem(ID_DOWNLOAD, getResources()
				.getString(R.string.qa_download), getResources().getDrawable(
				R.drawable.ic_action_download));
		ActionItem playListItem = new ActionItem(ID_ADD_PLAY_LIST,
				getResources().getString(R.string.qa_add_play_list),
				getResources().getDrawable(R.drawable.ic_playlist));

		ActionItem noteItem = new ActionItem(ID_DELETE, getResources()
				.getString(R.string.qa_delete_playlist), getResources()
				.getDrawable(R.drawable.ic_action_delete));

		// orientation
		quickAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
		quickAction.addActionItem(playListItem);
		quickAction.addActionItem(noteItem);

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						// here we can filter which action item was clicked with
						// pos or actionId parameter
						if (actionId == ID_PLAY) {
							Intent in = new Intent(getApplicationContext(),
									PlayerFragment.class);
							in.putExtra("_type", 1);
							in.putExtra("songIndex", quickAction.position);
							setResult(Activity.RESULT_OK, in);
							finish();

						} else if (actionId == ID_DOWNLOAD) {

							// Intent in = new Intent(getApplicationContext(),
							// PlayerFragment.class);
							// in.putExtra("songIndex", quickAction.position);
							// in.putExtra("_type", 2);
							// setResult(Activity.RESULT_OK, in);
							// finish();

						} else if (actionId == ID_ADD_PLAY_LIST) {
							AudioClass audioClass = audiosItemAdapter
									.getReciter(quickAction.position);
							Utils.showAddToPlaylist(context, audioClass);

						} else if (actionId == ID_DELETE) {
							audiosItemAdapter
									.RemoveReciter(quickAction.position);
							audiosItemAdapter.notifyDataSetChanged();

						}
					}
				});

		Fragment _fragment = null;
		_fragment = new AdmobFragment();
		FragmentManager _fragmentManager = getFragmentManager();

		_fragmentManager.beginTransaction()
				.add(R.id.admob_container, _fragment).commit();

	}

	public void ShowQuickMenu(int pos, View v) {
		quickAction.position = pos;
		quickAction.show(v);
	}

}
