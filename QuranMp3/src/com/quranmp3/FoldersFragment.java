package com.quranmp3;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.Reciters;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class FoldersFragment extends Fragment {

	private View view;

	private ListView lv_reciters;
	private FoldersItemAdapter recitersItemAdapter;
	private FoldersFragment _scope;

	MainActivity _FragmentActivity = null;

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
		// actionbar.setTitle(getResources().getString(R.string.chapters));
		setHasOptionsMenu(true);
		File currentDir = new File("/sdcard/MP3Quran");

		album = fill(currentDir);
		if (album.isEmpty()) {
			view = inflater.inflate(R.layout.ly_note_empty, container, false);
			return view;
		}

		view = inflater.inflate(R.layout.ly_folders, container, false);
		_scope = this;

		lv_reciters = (ListView) view.findViewById(R.id.lv_reciters);
		setupUI();

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();

	}

	ArrayList<Reciters> recitersList;
	private List<Albumb> album = null;

	private void setupUI() {

		recitersList = GlobalConfig.GetmyDbHelper().get_reciters_folders(
				GlobalConfig.lang_id, album);
		recitersItemAdapter = new FoldersItemAdapter(_scope, recitersList);
		lv_reciters.setAdapter(recitersItemAdapter);
		lv_reciters.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				Reciters reciter = recitersItemAdapter
						.getSelectedReciter(position);

				if (reciter != null) {
					AudioListManager audioListManager = AudioListManager
							.getInstance();
					audioListManager.setSelectedReciter(reciter);
					if (_FragmentActivity != null) {
						FilesFragment.reciterId = reciter.getId();
						_FragmentActivity.displayView(10);
					} else {
						Intent in = new Intent(_scope.getActivity()
								.getApplicationContext(), MainActivity.class);

						_scope.getActivity().setResult(Activity.RESULT_OK, in);
						_scope.getActivity().finish();
					}

				}
			}

		});
		int top = lv_reciters.getTop();
		// lv_reciters.setSelectionFromTop(
		// chapterslistManager.getCurrentSelectedChapterPosition(), top);
		// recitersItemAdapter.setSelectedItem(chapterslistManager
		// .getCurrentSelectedChapterPosition());

	}

	private int getFilesCount(File f) {
		File[] dirs = f.listFiles();
		int count = 0;
		try {
			for (File ff : dirs) {
				String name = ff.getName();
				String _name = "aaaa";
				// String _name = name.split(".")[0];
				if (name.length() > 3)
					_name = name.substring(0, 3);

				if (Utils.isNumeric(_name)) {
					count++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return count;

	}

	private List<Albumb> fill(File f) {
		File[] dirs = f.listFiles();
		// this.setTitle("Current Dir: " + f.getName());
		List<Albumb> dir = new ArrayList<Albumb>();
		try {
			for (File ff : dirs) {
				String name = ff.getName();
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				/*
				 * Note: Remove this name.equalsIgnoreCase("Personal" if u want
				 * to list all ur sd card file and folder &&
				 * name.equalsIgnoreCase("Personal")
				 */
				if (ff.isDirectory()) {

					String num_item = String.valueOf(getFilesCount(ff));

					String _name = ff.getName();
					if (Utils.isNumeric(_name)) {

						dir.add(new Albumb(ff.getName(), num_item, date_modify,
								ff.getAbsolutePath(), "play_list_fill"));
					}
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		return dir;

	}
}