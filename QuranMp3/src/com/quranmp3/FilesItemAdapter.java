package com.quranmp3;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.Reciters;
import com.quranmp3.model.Verses;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class FilesItemAdapter extends BaseAdapter implements Filterable {

	private FilesFragment activity;
	private List<Verses> data;
	private List<Verses> mOriginalValues;
	private static LayoutInflater inflater = null;
	private Verses verse;
	private String fontPath;
	private Typeface tf;
	private String verses_pre = " ";
	private String verses_ayah_count_pre = " ";
	private String verses_ayah_count_small = " ";
	Reciters reciter;
	AudioListManager audioListManager;

	public FilesItemAdapter(FilesFragment _scope, ArrayList<Verses> recitersList) {
		activity = _scope;
		fontPath = GlobalConfig.fontPath;
		tf = Typeface.createFromAsset(activity.getActivity().getAssets(),
				fontPath);
		data = recitersList;
		inflater = (LayoutInflater) activity.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		verses_pre = activity.getResources().getString(R.string.verses_pre)
				+ " ";
		verses_ayah_count_pre = activity.getResources().getString(
				R.string.verses_ayah_count_pre)
				+ " ";
		verses_ayah_count_small = activity.getResources().getString(
				R.string.verses_ayah_count_small)
				+ " ";
		audioListManager = AudioListManager.getInstance();
		UpdateSelectedReciter();

	}

	public void UpdateSelectedReciter() {
		reciter = audioListManager.getSelectedReciter();
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

	public void RemoveItem(int position) {
		data.remove(position);

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.ly_files_item, null);

		}
		TextView txt_verses_name = (TextView) vi
				.findViewById(R.id.txt_verses_name);
		if (GlobalConfig.lang_id.equals("1"))
			txt_verses_name.setTypeface(tf);
		verse = data.get(position);
		if (txt_verses_name != null) {
			txt_verses_name.setText(verses_pre + " " + verse.getName());
		}

		TextView txt_ayah_count = (TextView) vi
				.findViewById(R.id.txt_ayah_count);
		if (GlobalConfig.lang_id.equals("1"))
			txt_ayah_count.setTypeface(tf);

		if (txt_ayah_count != null) {
			if (verse.getSize() < 1)

				txt_ayah_count.setText(String.format("%.2f",
						(verse.getSize() * 100.0)) + " KB ");
			else
				txt_ayah_count.setText(String.format("%.2f", (verse.getSize()))
						+ " MB ");
		}

		// ImageView pin_icon = (ImageView) vi.findViewById(R.id.pin_icon);

		int reciterId = reciter.getId();
		String versesId = Utils.getAudioMp3Name(verse.getId());

		RelativeLayout quick_menu = (RelativeLayout) vi
				.findViewById(R.id.quick_menu);
		quick_menu.setTag(position);
		quick_menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((FilesFragment) activity).ShowQuickMenu(
						Integer.parseInt(String.valueOf(v.getTag())), v);
			}
		});
		// if (chapter.getChapterId() == bookChaptersManager
		// .getselectedChapterId()) {

		// vi.setBackgroundResource(R.color.list_background_pressed2);
		// } else {
		// vi.setBackgroundResource(R.drawable.list_selector2);
		// }
		return vi;
	}

	public Verses getSelectedFiltteredChapterId(int position) {
		if (data == null || data.get(position) == null)
			return null;
		return data.get(position);
	}

	List<Verses> FilteredArrList = null;

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				data = (List<Verses>) results.values; // has the filtered
														// values
				notifyDataSetChanged(); // notifies the data with new filtered
										// values
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults(); // Holds the
																// results of a
																// filtering
																// operation in
																// values
				FilteredArrList = new ArrayList<Verses>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<Verses>(data); // saves
																	// the
																	// original
																	// data
																	// in
																	// mOriginalValues
				}

				/********
				 * 
				 * If constraint(CharSequence that is received) is null returns
				 * the mOriginalValues(Original) values else does the Filtering
				 * and returns FilteredArrList(Filtered)
				 * 
				 ********/
				if (constraint == null || constraint.length() == 0) {

					// set the Original result to return
					results.count = mOriginalValues.size();
					results.values = mOriginalValues;
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						String data = mOriginalValues.get(i).getName();
						if (data.toLowerCase().contains(constraint.toString())) {
							FilteredArrList.add(mOriginalValues.get(i));
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}
				return results;
			}
		};
		return filter;
	}

}