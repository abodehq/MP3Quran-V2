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
import android.widget.ImageView;
import android.widget.TextView;

import com.quranmp3.model.DownloadClass;
import com.quranmp3.utils.GlobalConfig;

public class DownloaderItemAdapter extends BaseAdapter implements Filterable {

	private DownloaderFragment activity;
	private List<DownloadClass> data;
	private List<DownloadClass> mOriginalValues;
	private static LayoutInflater inflater = null;
	private DownloadClass verse;
	private String fontPath;
	private Typeface tf;
	private String verses_pre = " ";
	private String reciters_pre = " ";

	private String verses_ayah_count_pre = " ";
	private String verses_ayah_count_small = " ";

	public DownloaderItemAdapter(DownloaderFragment _scope,
			ArrayList<DownloadClass> arrayList) {
		activity = _scope;
		fontPath = GlobalConfig.fontPath;
		tf = Typeface.createFromAsset(activity.getActivity().getAssets(),
				fontPath);
		data = arrayList;
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
		reciters_pre = activity.getResources().getString(R.string.reciters_pre)
				+ " ";

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
			vi = inflater.inflate(R.layout.ly_downloader_item, null);

		}
		TextView txt_verses_name = (TextView) vi
				.findViewById(R.id.txt_verses_name);
		if (GlobalConfig.lang_id.equals("1"))
			txt_verses_name.setTypeface(tf);
		verse = data.get(position);
		if (verse != null) {
			if (txt_verses_name != null) {

				txt_verses_name.setText(verses_pre + " "
						+ verse.getAudioClass().getVerseName());
			}

			TextView txt_reciter_name = (TextView) vi
					.findViewById(R.id.txt_reciter_name);
			if (GlobalConfig.lang_id.equals("1"))
				txt_reciter_name.setTypeface(tf);

			if (txt_reciter_name != null) {

				txt_reciter_name.setText(reciters_pre + " "
						+ verse.getAudioClass().getReciterName());
			}

			TextView txt_ayah_count = (TextView) vi
					.findViewById(R.id.txt_ayah_count);
			if (GlobalConfig.lang_id.equals("1"))
				txt_ayah_count.setTypeface(tf);
			txt_ayah_count.setText(verse.getProgress() + "  % ");
			AnimatingProgressBar pbDownload = (AnimatingProgressBar) vi
					.findViewById(R.id.pbDownload);
			pbDownload.setProgress(verse.getProgress());

			ImageView reciter_icon = (ImageView) vi
					.findViewById(R.id.reciter_icon);
			String image = verse.getAudioClass().getImage().split(".jpg")[0];
			Context context = reciter_icon.getContext();
			int id = context.getResources().getIdentifier(image, "drawable",
					context.getPackageName());
			reciter_icon.setImageDrawable(activity.getResources().getDrawable(
					id));
		}
		// if (chapter.getChapterId() == bookChaptersManager
		// .getselectedChapterId()) {

		// vi.setBackgroundResource(R.color.list_background_pressed2);
		// } else {
		// vi.setBackgroundResource(R.drawable.list_selector2);
		// }
		return vi;
	}

	public DownloadClass getSelectedFiltteredChapterId(int position) {
		if (data == null || data.get(position) == null)
			return null;
		return data.get(position);
	}

	List<DownloadClass> FilteredArrList = null;

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				data = (List<DownloadClass>) results.values; // has the filtered
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
				FilteredArrList = new ArrayList<DownloadClass>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<DownloadClass>(data); // saves
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
						String data = mOriginalValues.get(i).getAudioClass()
								.getVerseName();
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