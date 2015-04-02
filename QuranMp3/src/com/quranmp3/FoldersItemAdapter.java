package com.quranmp3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.model.Reciters;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class FoldersItemAdapter extends BaseAdapter implements Filterable {

	private FoldersFragment activity;
	private List<Reciters> data;
	private List<Reciters> mOriginalValues;
	private static LayoutInflater inflater = null;
	private Reciters reciter;
	private String fontPath;
	private Typeface tf;
	private String reciters_pre = " ";
	private String folders_pre = " ";

	public FoldersItemAdapter(FoldersFragment _scope,
			ArrayList<Reciters> recitersList) {
		activity = _scope;
		fontPath = GlobalConfig.fontPath;
		tf = Typeface.createFromAsset(activity.getActivity().getAssets(),
				fontPath);
		data = recitersList;
		inflater = (LayoutInflater) activity.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		reciters_pre = activity.getResources().getString(R.string.reciters_pre)
				+ " ";
		folders_pre = activity.getResources().getString(R.string.folders_pre)
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
			vi = inflater.inflate(R.layout.ly_folders_item, null);

		}
		TextView txt_reciters_name = (TextView) vi
				.findViewById(R.id.txt_reciters_name);
		if (GlobalConfig.lang_id.equals("1"))
			txt_reciters_name.setTypeface(tf);
		reciter = data.get(position);
		if (txt_reciters_name != null) {

			txt_reciters_name.setText(reciters_pre + " " + reciter.getName());
		}
		TextView txt_suras_count = (TextView) vi
				.findViewById(R.id.txt_suras_count);
		if (GlobalConfig.lang_id.equals("1"))
			txt_suras_count.setTypeface(tf);
		if (txt_suras_count != null) {

			txt_suras_count.setText(reciter.getItems() + " " + folders_pre);
		}

		RelativeLayout delete_icon = (RelativeLayout) vi
				.findViewById(R.id.delete_icon);
		delete_icon.setTag(position);
		delete_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int position = Integer.parseInt(String.valueOf(v.getTag()));
				DeleteDialog(position);
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

	private int selectedItem;

	public void setSelectedItem(int position) {
		selectedItem = position;
	}

	public Reciters getSelectedReciter(int position) {

		if (data == null || data.get(position) == null)
			return null;
		return data.get(position);
	}

	List<Reciters> FilteredArrList = null;

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				data = (List<Reciters>) results.values; // has the filtered
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
				FilteredArrList = new ArrayList<Reciters>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<Reciters>(data); // saves
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

	private void DeleteDialog(int position) {
		final int _position = position;
		new AlertDialog.Builder(activity.getActivity())
				.setTitle(
						activity.getResources().getString(
								R.string.verses_remove)
								+ " ")
				.setMessage(
						activity.getResources().getString(
								R.string.reciters_remove_message))
				.setPositiveButton(
						activity.getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								reciter = data.get(_position);
								Log.e("-->", reciter.getName());
								File currentDir = new File("/sdcard/MP3Quran/"
										+ reciter.getId());

								if (Utils.deleteDirectory(currentDir)) {
									GlobalConfig.ShowSuccessToast(
											activity.getActivity(),
											activity.getResources()
													.getString(
															R.string.reciters_remove_message_s));
								} else {
									GlobalConfig.ShowErrorToast(
											activity.getActivity(),
											activity.getResources()
													.getString(
															R.string.reciters_remove_message_e));

								}

								data.remove(_position);
								notifyDataSetChanged();
								dialog.cancel();

							}
						})
				.setNegativeButton(
						activity.getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

}