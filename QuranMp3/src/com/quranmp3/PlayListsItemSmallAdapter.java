package com.quranmp3;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quranmp3.model.Mp3PlayLists;
import com.quranmp3.utils.GlobalConfig;

public class PlayListsItemSmallAdapter extends BaseAdapter {

	private Context activity;
	private List<Mp3PlayLists> data;
	private static LayoutInflater inflater = null;
	private Mp3PlayLists playList;
	private String fontPath;
	private Typeface tf;
	private String playLists_pre = " ";

	public PlayListsItemSmallAdapter(Context _FragmentActivity,
			ArrayList<Mp3PlayLists> playListsList) {
		activity = _FragmentActivity;
		fontPath = GlobalConfig.fontPath;
		tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
		data = playListsList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		playLists_pre = activity.getResources()
				.getString(R.string.reciters_pre) + " ";

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.ly_play_lists_small_item, null);

		}
		TextView txt_playLists_name = (TextView) vi
				.findViewById(R.id.txt_playLists_name);
		txt_playLists_name.setTypeface(tf);
		playList = data.get(position);
		if (txt_playLists_name != null) {

			txt_playLists_name.setText(playList.getName());
		}

		TextView txt_play_lists_verses = (TextView) vi
				.findViewById(R.id.txt_play_lists_verses);
		txt_play_lists_verses.setTypeface(tf);

		if (txt_play_lists_verses != null) {

			txt_play_lists_verses.setText("”Ê—" + " " + playList.getCount());
		}

		return vi;
	}

	private int selectedItem;

	public void setSelectedItem(int position) {
		selectedItem = position;
	}

	public Mp3PlayLists getSelectedFiltteredChapterId(int position) {
		Log.e("Position", position + "");
		if (data == null || data.get(position) == null)
			return null;
		// Log.e("Position", data.get(position).getchapterName() + "");
		return data.get(position);
	}

}