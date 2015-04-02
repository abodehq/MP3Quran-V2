package com.quranmp3;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListFolder extends Fragment {

	private File currentDir;
	private FileArrayAdapter adapter;
	private View view;
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

	private ListView lv_reciters;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = inflater.inflate(R.layout.ly_audios_list, container, false);

		lv_reciters = (ListView) view.findViewById(R.id.lv_reciters);
		lv_reciters.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View aView,
					int position, long arg3) {
				Albumb o = adapter.getItem(position);
				if (o.getImage().equalsIgnoreCase("play_list_fill")
						|| o.getImage().equalsIgnoreCase("ic_action_back")) {
					currentDir = new File(o.getPath());
					level = 2;
					if (o.getImage().equalsIgnoreCase("ic_action_back"))
						level = 1;
					fill(currentDir);
				}
			}

		});
		currentDir = new File("/sdcard/MP3Quran");
		fill(currentDir);
		return view;
	}

	int level = 1;

	private void fill(File f) {
		File[] dirs = f.listFiles();
		// this.setTitle("Current Dir: " + f.getName());
		List<Albumb> dir = new ArrayList<Albumb>();
		List<Albumb> fls = new ArrayList<Albumb>();
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

					File[] fbuf = ff.listFiles();
					int buf = 0;
					if (fbuf != null) {
						buf = fbuf.length;
					} else
						buf = 0;
					String num_item = String.valueOf(buf);
					if (buf == 0)
						num_item = num_item + " item";
					else
						num_item = num_item + " items";

					// String formated = lastModDate.toString();
					if (level == 1)
						dir.add(new Albumb(ff.getName(), num_item, date_modify,
								ff.getAbsolutePath(), "play_list_fill"));
				} else {
					/*
					 * Note: Remove this f.getName().equalsIgnoreCase("Personal"
					 * if u want to list all ur sd card file and folder if
					 * (f.getName().equalsIgnoreCase( "Personal")) {}
					 */
					if (level != 1)
						fls.add(new Albumb(ff.getName(), ff.length() + " Byte",
								date_modify, ff.getAbsolutePath(), "mp3_icon"));

				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("MP3Quran")) {
			dir.add(0, new Albumb("..", "Parent Directory", "", f.getParent(),
					"ic_action_back"));
			level = 1;
		}
		adapter = new FileArrayAdapter(_FragmentActivity, R.layout.file_view,
				dir);
		lv_reciters.setAdapter(adapter);
		// this.setListAdapter(adapter);
	}

}
