package com.quranmp3;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ListView;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.model.Reciters;
import com.quranmp3.utils.GlobalConfig;

public class RecitersFragment extends Fragment {

	private View view;

	private ListView lv_reciters;
	private RecitersItemAdapter recitersItemAdapter;
	private RecitersFragment _scope;

	private EditText reciters_Search;
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
		view = inflater.inflate(R.layout.ly_reciters, container, false);
		_scope = this;

		lv_reciters = (ListView) view.findViewById(R.id.lv_reciters);
		reciters_Search = (EditText) view.findViewById(R.id.reciters_Search);
		reciters_Search.setText("");
		reciters_Search.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));

		reciters_Search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (recitersItemAdapter != null)
					recitersItemAdapter.getFilter().filter(cs);
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
					reciters_Search.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
				else
					reciters_Search.setBackgroundColor(Color.WHITE);

			}
		});
		reciters_Search.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (v == reciters_Search) {
					if (hasFocus) {
						reciters_Search.setBackgroundColor(Color.WHITE);

					}
				}

			}
		});

		setupUI();

		return view;
	}

	@Override
	public void onResume() {

		super.onResume();

	}

	ArrayList<Reciters> recitersList;

	private void setupUI() {
		reciters_Search.setText("");
		recitersList = GlobalConfig.GetmyDbHelper().get_reciters(
				GlobalConfig.lang_id);
		recitersItemAdapter = new RecitersItemAdapter(_scope, recitersList);
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
					if (_FragmentActivity != null)
						_FragmentActivity.displayView(3);
					else {
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
}