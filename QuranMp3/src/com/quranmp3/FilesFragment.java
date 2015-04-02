package com.quranmp3;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.model.Reciters;
import com.quranmp3.model.Verses;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.SlidingPanel;
import com.quranmp3.utils.Utils;

public class FilesFragment extends Fragment {

	private View view;
	private ListView lv_verses;
	private FilesItemAdapter versesItemAdapter;
	private FilesFragment _scope;
	private EditText verses_Search;

	// action id
	private static final int ID_PLAY = 1;
	private static final int ID_ADD_QUEUE = 2;
	private static final int ID_ADD_PLAY_LIST = 3;

	private static final int ID_DELETE = 4;
	private static final int ID_SHARE = 5;
	QuickAction quickAction;
	SlidingPanel popup;

	MainActivity _FragmentActivity;

	private File currentDir;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_FragmentActivity = (MainActivity) activity;
		// Intent intent = new Intent(_FragmentActivity, DownloadService.class);
		// _FragmentActivity.bindService(intent, dConnection,
		// Context.BIND_AUTO_CREATE);

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

	Reciters reciter = new Reciters();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionbar;
		actionbar = this.getActivity().getActionBar();
		setHasOptionsMenu(true);
		view = inflater.inflate(R.layout.ly_files, container, false);
		_scope = this;
		reciter = GlobalConfig.GetmyDbHelper().get_reciters_by_id(reciterId,
				GlobalConfig.lang_id);
		updateReciterInfo();
		lv_verses = (ListView) view.findViewById(R.id.lv_verses);
		verses_Search = (EditText) view.findViewById(R.id.verses_Search);
		verses_Search.setText("");
		verses_Search.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		verses_Search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (versesItemAdapter != null)
					versesItemAdapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length() == 0)
					verses_Search.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
				else
					verses_Search.setBackgroundColor(Color.WHITE);
			}
		});
		RelativeLayout back_menu = (RelativeLayout) view
				.findViewById(R.id.reciter_header_bg);

		back_menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_FragmentActivity.displayView(5);
				// Intent i = new Intent();

				// i.setClass(_FragmentActivity, RecitersActivity.class);
				// startActivityForResult(i, 1);

			}
		});

		verses_Search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				verses_Search.setBackgroundColor(Color.WHITE);
			}
		});

		verses_Search.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (v == verses_Search) {
					if (hasFocus) {
						verses_Search.setBackgroundColor(Color.WHITE);

					}

				}

			}
		});

		setupUI();

		return view;
	}

	private void updateReciterInfo() {
		// reciter info section
		String reciters_pre = _FragmentActivity.getResources().getString(
				R.string.reciters_pre)
				+ " ";

		AudioListManager audioListManager = AudioListManager.getInstance();

		TextView txt_reciter_name = (TextView) view
				.findViewById(R.id.txt_reciter_name);

		String verseReciter = reciter.getName();
		txt_reciter_name.setText(reciters_pre + verseReciter);

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
	public void onResume() {

		super.onResume();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				updateReciterInfo();
				versesItemAdapter.UpdateSelectedReciter();
				versesItemAdapter.notifyDataSetChanged();
			}
		}
	}

	public static int reciterId = 1;

	private AudioClass SetAudioClasData(int position) {
		AudioListManager audioListManager = AudioListManager.getInstance();

		AudioClass audioClass = null;
		Verses verses = versesItemAdapter
				.getSelectedFiltteredChapterId(position);
		if (verses != null) {
			audioClass = new AudioClass();
			audioClass.setVerseId(verses.getId());
			audioClass.setVerseName(verses.getName());
			audioClass.setAyahCount(verses.getAyahCount());
			audioClass.setPlaceId(verses.getPlaceId());
			audioClass.setReciterId(reciter.getId());
			audioClass.setImage(reciter.getImage());
			audioClass.setReciterName(reciter.getName());
			String audioBasePath = reciter.getAudioBasePath();
			String versesId = Utils.getAudioMp3Name(verses.getId()) + "";
			String audioPath = audioBasePath + versesId;
			audioClass.setAudioPath(audioPath);

		}
		return audioClass;
	}

	private void setupUI() {
		currentDir = new File("/sdcard/MP3Quran/" + reciter.getId());
		// end
		verses_Search.setText("");
		fill(currentDir);
		versesItemAdapter = new FilesItemAdapter(_scope, GlobalConfig
				.GetmyDbHelper().get_verses_files(GlobalConfig.lang_id, Ids,
						filesSize));
		lv_verses.setAdapter(versesItemAdapter);
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
				quickAction.position = position;
				DeleteDialog();

			}

		});
		int top = lv_verses.getTop();
		ActionItem nextItem = new ActionItem(ID_PLAY, getResources().getString(
				R.string.qa_play_verse), _FragmentActivity.getResources()
				.getDrawable(R.drawable.ic_player_icon));
		ActionItem prevItem = new ActionItem(ID_ADD_QUEUE, getResources()
				.getString(R.string.qa_add_queue), _FragmentActivity
				.getResources().getDrawable(R.drawable.ic_playlist_icon));

		ActionItem playListItem = new ActionItem(ID_ADD_PLAY_LIST,
				getResources().getString(R.string.qa_add_play_list),
				_FragmentActivity.getResources().getDrawable(
						R.drawable.ic_playlist));

		ActionItem noteItem = new ActionItem(ID_DELETE, getResources()
				.getString(R.string.qa_delete_verse), _FragmentActivity
				.getResources().getDrawable(R.drawable.ic_action_delete));

		ActionItem shareItem = new ActionItem(ID_SHARE, getResources()
				.getString(R.string.qa_share), _FragmentActivity.getResources()
				.getDrawable(R.drawable.ic_action_share));

		// orientation
		quickAction = new QuickAction(_FragmentActivity, QuickAction.VERTICAL);

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
							AudioListManager audioListManager = AudioListManager
									.getInstance();

							AudioClass audioClass = SetAudioClasData(quickAction.position);

							audioListManager.deletAllSuras();
							audioListManager.AddNewSura(audioClass);
							audioListManager.setUpdatePlayerStatus(true);
							if (_FragmentActivity != null)
								_FragmentActivity.displayView(1);

						} else if (actionId == ID_ADD_QUEUE) {
							AudioListManager audioListManager = AudioListManager
									.getInstance();
							AudioClass audioClass = SetAudioClasData(quickAction.position);
							if (!audioListManager.isVerseExist(
									audioClass.getReciterId(),
									audioClass.getVerseId())) {
								audioListManager.AddNewSuraAt(audioListManager
										.getPlayList().size(), audioClass);
								GlobalConfig
										.ShowSuccessToast(
												_FragmentActivity,
												_FragmentActivity
														.getResources()
														.getString(
																R.string.play_ins_wait_list_s));
							} else {
								GlobalConfig
										.ShowErrorToast(
												_FragmentActivity,
												_FragmentActivity
														.getResources()
														.getString(
																R.string.play_ins_wait_list_e));
							}

						} else if (actionId == ID_ADD_PLAY_LIST) {
							AudioClass audioClass = new AudioClass();
							AudioListManager audioListManager = AudioListManager
									.getInstance();
							Reciters reciter = audioListManager
									.getSelectedReciter();

							Verses verses = versesItemAdapter
									.getSelectedFiltteredChapterId(quickAction.position);
							audioClass.setReciterId(reciter.getId());
							audioClass.setVerseId(verses.getId());
							Utils.showAddToPlaylist(_FragmentActivity,
									audioClass);

						} else if (actionId == ID_DELETE) {

							DeleteDialog();

						} else if (actionId == ID_SHARE) {

							AudioClass audioClass = SetAudioClasData(quickAction.position);
							Utils.shareMp3(_FragmentActivity, audioClass);

						}
					}
				});

	}

	ArrayList<Float> filesSize;
	ArrayList<Integer> Ids;

	private ArrayList<Integer> fill(File f) {
		File[] dirs = f.listFiles();
		Ids = new ArrayList<Integer>();
		filesSize = new ArrayList<Float>();
		try {
			for (File ff : dirs) {
				String name = ff.getName();
				String _name = "aaaa";
				// String _name = name.split(".")[0];
				if (name.length() > 3)
					_name = name.substring(0, 3);

				if (Utils.isNumeric(_name)) {
					Ids.add(Integer.parseInt(_name));
					float size = ff.length();
					size = ff.length();

					filesSize.add(size);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return Ids;
		// Collections.sort(fls);
		// dir.addAll(fls);

		// dir.add(0, new Albumb("..", "Parent Directory", "", f.getParent(),
		// "ic_action_back"));

	}

	public void ShowQuickMenu(int pos, View v) {
		quickAction.position = pos;
		quickAction.show(v);
	}

	private void DeleteDialog() {

		new AlertDialog.Builder(_FragmentActivity)
				.setTitle(
						_FragmentActivity.getResources().getString(
								R.string.verses_remove)
								+ " ")
				.setMessage(
						_FragmentActivity.getResources().getString(
								R.string.verses_remove_message))
				.setPositiveButton(
						_FragmentActivity.getResources()
								.getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								AudioClass audioClass = SetAudioClasData(quickAction.position);
								Utils.deleteVerse(audioClass);
								versesItemAdapter
										.RemoveItem(quickAction.position);
								versesItemAdapter.notifyDataSetChanged();
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
