package com.quranmp3;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.model.AudioClass;
import com.quranmp3.model.Mp3PlayListsVerses;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class PlayListVersesAdapter extends BaseAdapter {

	private PlayListVersesActivity activity;
	private ArrayList<AudioClass> data;

	private static LayoutInflater inflater = null;
	private AudioClass audioClass;
	private String fontPath;
	private Typeface tf;
	private String reciters_pre = " ";
	private String verses_pre = " ";

	private PlayListVersesAdapter _scope = this;

	public PlayListVersesAdapter(PlayListVersesActivity playListVersesActivity,
			ArrayList<AudioClass> audioClass) {
		activity = playListVersesActivity;
		fontPath = GlobalConfig.fontPath;
		tf = Typeface.createFromAsset(activity.getAssets(), fontPath);
		data = audioClass;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		reciters_pre = activity.getResources().getString(R.string.reciters_pre)
				+ " ";
		verses_pre = activity.getResources().getString(R.string.verses_pre)
				+ " ";

	}

	public int getCount() {
		return data.size();
	}

	public void ReOrder(int from, int to) {
		if (from != to) {
			AudioClass reciter = data.get(from);
			data.remove(from);
			data.add(to, reciter);
		}

	}

	public AudioClass getReciter(int position) {
		return data.get(position);
	}

	public void RemoveReciter(int position, int playListId) {
		if (data.size() > 1) {
			Mp3PlayListsVerses mp3PlayListsVerses = new Mp3PlayListsVerses();

			mp3PlayListsVerses.setPlayListId(playListId);
			mp3PlayListsVerses.setReciterId(data.get(position).getReciterId());
			mp3PlayListsVerses.setVerseId(data.get(position).getVerseId());

			GlobalConfig.GetmyDbHelper().delete_playlist_verses(
					mp3PlayListsVerses);
			data.remove(position);
		} else
			GlobalConfig.ShowInfoToast(activity, "no verses");
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
			vi = inflater.inflate(R.layout.ly_player_play_item, null);

		}
		TextView txt_reciters_name = (TextView) vi
				.findViewById(R.id.txt_reciters_name);
		if (GlobalConfig.lang_id.equals("1"))
			txt_reciters_name.setTypeface(tf);
		audioClass = data.get(position);
		if (txt_reciters_name != null) {

			txt_reciters_name.setText(reciters_pre + " "
					+ audioClass.getReciterName());
		}

		TextView txt_verses_name = (TextView) vi
				.findViewById(R.id.txt_verses_name);

		if (GlobalConfig.lang_id.equals("1"))
			txt_verses_name.setTypeface(tf);

		if (txt_verses_name != null) {

			txt_verses_name.setText(verses_pre + " "
					+ audioClass.getVerseName());
		}

		ImageView image_icon = (ImageView) vi.findViewById(R.id.image_icon);
		String image = audioClass.getImage().split(".jpg")[0];
		Context context = image_icon.getContext();
		int id = context.getResources().getIdentifier(image, "drawable",
				context.getPackageName());
		image_icon.setImageDrawable(activity.getResources().getDrawable(id));

		ImageView pin_icon = (ImageView) vi.findViewById(R.id.pin_icon);

		int reciterId = audioClass.getReciterId();
		String versesId = Utils.getAudioMp3Name(audioClass.getVerseId());
		if (!Utils.isFileExist(reciterId, versesId)) {
			pin_icon.setVisibility(View.GONE);
		} else {
			pin_icon.setVisibility(View.VISIBLE);
		}

		RelativeLayout quick_menu = (RelativeLayout) vi
				.findViewById(R.id.quick_menu);
		quick_menu.setTag(position);
		quick_menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.ShowQuickMenu(
						Integer.parseInt(String.valueOf(v.getTag())), v);
			}
		});

		return vi;
	}

	private int selectedItem;

	public void setSelectedItem(int position) {
		selectedItem = position;
	}

	public AudioClass getSelectedFiltteredChapterId(int position) {
		if (data == null || data.get(position) == null)
			return null;
		// Log.e("Position", data.get(position).getchapterName() + "");
		return data.get(position);
	}

}