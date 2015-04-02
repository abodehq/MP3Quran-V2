package com.quranmp3;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
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
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.SlidingPanel;
import com.quranmp3.utils.Utils;

public class PlayListVersesActivity extends Activity {

	private DragSortListView lv_audios;
	private PlayListVersesAdapter audiosItemAdapter;

	// action id
	private static final int ID_ADD_QUEUE = 1;
	private static final int ID_DELETE = 2;
	private static final int ID_PLAY = 3;
	private static final int ID_ADD_PLAY_LIST = 4;
	private static final int ID_SHARE = 5;
	QuickAction quickAction;
	SlidingPanel popup;
	private Context context = null;
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

			audiosItemAdapter.RemoveReciter(which, playListId);
			audiosItemAdapter.notifyDataSetChanged();
		}
	};
	ArrayList<AudioClass> audioClass = null;
	int playListId = 1;

	/** Called when the activity is first created. */
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		setContentView(R.layout.ly_player_play_list);
		Intent intent = getIntent();
		ActionBar actionbar;
		actionbar = getActionBar();
		actionbar.setTitle(intent.getStringExtra("playListName"));
		actionbar.setIcon(R.drawable.ic_playlist);

		playListId = intent.getIntExtra("playListId", 1);
		lv_audios = (DragSortListView) findViewById(R.id.lv_audios);
		audioClass = GlobalConfig.GetmyDbHelper().get_play_lists_verses(
				GlobalConfig.lang_id, playListId + "");
		audiosItemAdapter = new PlayListVersesAdapter(this, audioClass);
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
				return false;
			}
		});
		lv_audios.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				if (audioClass != null) {
					AudioListManager audioListManager = AudioListManager
							.getInstance();

					audioListManager.deletAllSuras();

					audioListManager.AddNewSura(audioClass.get(position));
					audioListManager.setUpdatePlayerStatus(true);
					Intent in = new Intent(getApplicationContext(),
							PlayerFragment.class);
					in.putExtra("songIndex", position);
					setResult(Activity.RESULT_OK, in);
					finish();
				}

			}

		});
		// quick Action
		ActionItem nextItem = new ActionItem(ID_PLAY, getResources().getString(
				R.string.qa_play_verse), getResources().getDrawable(
				R.drawable.ic_player_icon));
		ActionItem prevItem = new ActionItem(ID_ADD_QUEUE, getResources()
				.getString(R.string.qa_add_queue), getResources().getDrawable(
				R.drawable.ic_playlist_icon));
		ActionItem playListItem = new ActionItem(ID_ADD_PLAY_LIST,
				getResources().getString(R.string.qa_add_play_list),
				getResources().getDrawable(R.drawable.ic_playlist));

		ActionItem noteItem = new ActionItem(ID_DELETE, getResources()
				.getString(R.string.qa_delete_playlist), getResources()
				.getDrawable(R.drawable.ic_action_delete));

		ActionItem shareItem = new ActionItem(ID_SHARE, getResources()
				.getString(R.string.qa_share), getResources().getDrawable(
				R.drawable.ic_action_share));

		// orientation
		quickAction = new QuickAction(this, QuickAction.VERTICAL);

		// add action items into QuickAction
		quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
		quickAction.addActionItem(playListItem);
		quickAction.addActionItem(noteItem);
		quickAction.addActionItem(shareItem);
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						// here we can filter which action item was clicked with
						// pos or actionId parameter
						if (actionId == ID_PLAY) {
							if (audioClass != null) {
								AudioListManager audioListManager = AudioListManager
										.getInstance();

								audioListManager.deletAllSuras();

								audioListManager.AddNewSura(audioClass
										.get(quickAction.position));
								audioListManager.setUpdatePlayerStatus(true);
								Intent in = new Intent(getApplicationContext(),
										PlayerFragment.class);
								in.putExtra("songIndex", quickAction.position);
								setResult(Activity.RESULT_OK, in);
								finish();
							}

						} else if (actionId == ID_ADD_QUEUE) {
							AudioListManager audioListManager = AudioListManager
									.getInstance();

							// audioListManager.AddNewSuraAt(audioListManager
							// .getPlayList().size(), audioClass
							// .get(quickAction.position));
							AudioClass _audioClass = audioClass
									.get(quickAction.position);
							if (!audioListManager.isVerseExist(
									_audioClass.getReciterId(),
									_audioClass.getVerseId())) {
								audioListManager.AddNewSuraAt(audioListManager
										.getPlayList().size(), _audioClass);
								GlobalConfig.ShowSuccessToast(
										context,
										context.getResources().getString(
												R.string.play_ins_wait_list_s));
								// play_ins_wait_list_s
							} else {
								GlobalConfig.ShowErrorToast(
										context,
										context.getResources().getString(
												R.string.play_ins_wait_list_e));
							}

						} else if (actionId == ID_ADD_PLAY_LIST) {
							AudioClass audioClass = audiosItemAdapter
									.getReciter(quickAction.position);
							Utils.showAddToPlaylist(context, audioClass);

						} else if (actionId == ID_DELETE) {
							audiosItemAdapter.RemoveReciter(
									quickAction.position, playListId);
							audiosItemAdapter.notifyDataSetChanged();

						} else if (actionId == ID_SHARE) {

							AudioClass audioClass = audiosItemAdapter
									.getReciter(quickAction.position);
							Utils.shareMp3(context, audioClass);

						}
					}
				});

	}

	public void ShowQuickMenu(int pos, View v) {
		quickAction.position = pos;
		quickAction.show(v);
	}

}
