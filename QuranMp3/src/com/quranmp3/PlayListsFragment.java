package com.quranmp3;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.model.Mp3PlayLists;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.SlidingPanel;

public class PlayListsFragment extends Fragment {

	private View view;

	private ListView lv_reciters;
	private PlayListsItemAdapter recitersItemAdapter;
	private PlayListsFragment _scope;

	MainActivity _FragmentActivity = null;

	// action id
	private static final int ID_RENAME = 1;
	private static final int ID_DELETE = 2;
	private static final int ID_PLAY = 3;

	QuickAction quickAction;
	SlidingPanel popup;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			_FragmentActivity = (MainActivity) activity;

		} catch (Exception e) {
			_FragmentActivity = null;
		}

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

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionbar;
		actionbar = this.getActivity().getActionBar();
		actionbar.setTitle(getResources().getString(R.string.chapters));
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.ly_play_lists, container, false);
		_scope = this;

		lv_reciters = (ListView) view.findViewById(R.id.lv_reciters);
		lv_reciters.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				Intent i = new Intent(
						_FragmentActivity.getApplicationContext(),
						PlayListVersesActivity.class);// PlayerPlayListActivity//
				i.putExtra("playListName", recitersList.get(position).getName());
				i.putExtra("playListId", recitersList.get(position).getId()); // CursorDSLV
				startActivityForResult(i, 101);
				// SemiChapterActivity.dummyChapterId = _position;
				// ((BookTabsActivity) getActivity()).setSelectedTab(1);
			}

		});

		setupUI();
		RelativeLayout btn_m_new = (RelativeLayout) view
				.findViewById(R.id.btn_m_new);
		btn_m_new.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String label = _FragmentActivity.getResources().getString(
						R.string.new_play_list_label);
				Mp3PlayLists mp3PlayerList = new Mp3PlayLists();
				mp3PlayerList.setName(label);
				mp3PlayerList.setOrder(1);
				mp3PlayerList.setDate("");
				GlobalConfig.myDbHelper.insert_playlist(mp3PlayerList);
				setupUI();
			}
		});

		ActionItem nextItem = new ActionItem(ID_PLAY, getResources().getString(
				R.string.qa_play_playlist), _FragmentActivity.getResources()
				.getDrawable(R.drawable.ic_player_icon));
		ActionItem prevItem = new ActionItem(ID_RENAME, getResources()
				.getString(R.string.qa_rename_playlist), _FragmentActivity
				.getResources().getDrawable(R.drawable.ic_action_edit));

		ActionItem noteItem = new ActionItem(ID_DELETE, getResources()
				.getString(R.string.qa_delete_playlist), _FragmentActivity
				.getResources().getDrawable(R.drawable.ic_action_delete));

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
						if (actionId == ID_PLAY) {
							ArrayList<AudioClass> audioClass = GlobalConfig
									.GetmyDbHelper().get_play_lists_verses(
											GlobalConfig.lang_id,
											recitersList.get(
													quickAction.position)
													.getId()
													+ "");
							AudioListManager audioListManager = AudioListManager
									.getInstance();

							audioListManager.deletAllSuras();

							audioListManager.SetSongs(audioClass);
							audioListManager.setUpdatePlayerStatus(true);
							if (_FragmentActivity != null)
								_FragmentActivity.displayView(1);

						} else if (actionId == ID_RENAME) {
							ShowRenameDialog();

						} else if (actionId == ID_DELETE) {
							// GlobalConfig.myDbHelper
							// .delete_playlist_by_id(recitersList.get(
							// quickAction.position).getId());
							// setupUI();
							if (recitersList.get(quickAction.position)
									.getCount() == 0) {
								GlobalConfig.myDbHelper
										.delete_playlist_by_id(recitersList
												.get(quickAction.position)
												.getId());
								setupUI();
							} else
								DeleteDialog(0);

						}
					}
				});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.getExtras() != null) {
					int currentSongIndex = data.getExtras().getInt("songIndex");

					if (_FragmentActivity != null)
						_FragmentActivity.displayView(1);
				}
			}

			// play selected song
			// playSong(currentSongIndex);
		}

	}

	@Override
	public void onResume() {

		super.onResume();

	}

	public void ShowQuickMenu(int pos, View v) {
		quickAction.position = pos;
		quickAction.show(v);
	}

	ArrayList<Mp3PlayLists> recitersList;

	private void setupUI() {

		recitersList = GlobalConfig.GetmyDbHelper().get_play_lists();
		recitersItemAdapter = new PlayListsItemAdapter(_scope, recitersList);
		lv_reciters.setAdapter(recitersItemAdapter);
		lv_reciters.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				// TODO Auto-generated method stub
				quickAction.position = pos;
				quickAction.show(arg1);
				return false;
			}
		});
		int top = lv_reciters.getTop();
		// lv_reciters.setSelectionFromTop(
		// chapterslistManager.getCurrentSelectedChapterPosition(), top);
		// recitersItemAdapter.setSelectedItem(chapterslistManager
		// .getCurrentSelectedChapterPosition());

	}

	int selectedIndex = 1;

	private void ShowRenameDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(_FragmentActivity);
		builder.setMessage(_FragmentActivity.getResources().getString(
				R.string.play_list_edit_title)
				+ " "
				+ recitersList.get(quickAction.position).getName()
				+ " "
				+ _FragmentActivity.getResources().getString(
						R.string.play_list_edit_to));
		final EditText input = new EditText(_FragmentActivity);
		input.setMaxLines(1);
		input.append(recitersList.get(quickAction.position).getName());
		input.setLines(1);
		input.setSingleLine();
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(15);
		input.setFilters(FilterArray);
		input.setSelection(0, input.getText().length());
		input.setId(1);
		InputMethodManager imm = (InputMethodManager) _FragmentActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		builder.setView(input);
		builder.setPositiveButton(
				_FragmentActivity.getResources().getString(
						R.string.play_list_edit_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = input.getText().toString();
						// db.UpdatePlaylist(
						// surasList.get(selectedIndex).get("playlistId"),
						// value);
						// Toast.makeText(getApplicationContext(),
						// "playlist renamed", Toast.LENGTH_SHORT).show();
						// loadCurrentPlaylists();
						GlobalConfig.myDbHelper.UpdatePlaylist(recitersList
								.get(quickAction.position).getId(), value);
						setupUI();
						dialog.cancel();

					}
				});
		builder.setNegativeButton(
				_FragmentActivity.getResources().getString(
						R.string.play_list_edit_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						return;
					}
				});
		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();

	}

	private void DeleteDialog(int _id) {
		final int id = _id;
		new AlertDialog.Builder(_FragmentActivity)
				.setTitle(
						_FragmentActivity.getResources().getString(
								R.string.play_list_remove_title)
								+ " "
								+ recitersList.get(quickAction.position)
										.getName())
				.setMessage(
						_FragmentActivity.getResources().getString(
								R.string.play_list_remove))
				.setPositiveButton(
						_FragmentActivity.getResources()
								.getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								GlobalConfig.myDbHelper
										.delete_playlist_by_id(recitersList
												.get(quickAction.position)
												.getId());
								setupUI();
								dialog.cancel();

							}
						})
				.setNegativeButton(
						_FragmentActivity.getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}