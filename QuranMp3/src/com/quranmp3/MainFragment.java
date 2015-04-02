package com.quranmp3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.controllers.AudioListManager;
import com.quranmp3.controllers.SharedPreferencesManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.utils.GlobalConfig;

public class MainFragment extends Fragment {
	MainActivity _FragmentActivity;
	AudioClass audioClass = null;

	public MainFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		_FragmentActivity = (MainActivity) activity;

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

	ArrayList<HashMap<String, String>> semiChaptersList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// GlobalConfig.myDbHelper.getRandomContent();
		View rootView = inflater.inflate(R.layout.ly_main_fragment, container,
				false);

		TextView chapter_title = (TextView) rootView
				.findViewById(R.id.chapter_title);

		Time now = new Time();
		now.setToNow();
		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(_FragmentActivity);

		RelativeLayout rl_today_hadith = (RelativeLayout) rootView
				.findViewById(R.id.rl_today_hadith);
		rl_today_hadith.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioClass == null) {
					AudioListManager audioListManager = AudioListManager
							.getInstance();

					audioListManager.deletAllSuras();
					audioListManager.AddNewSura(audioClass);
					audioListManager.setUpdatePlayerStatus(true);
				}
				if (_FragmentActivity != null)
					_FragmentActivity.displayView(1);
				// semiChapterManager.SetSemiChaptersList(_FragmentActivity);

			}
		});

		RelativeLayout ry_chapters = (RelativeLayout) rootView
				.findViewById(R.id.ry_chapters);
		ry_chapters.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_FragmentActivity.displayView(2);
			}
		});
		RelativeLayout ry_share = (RelativeLayout) rootView
				.findViewById(R.id.ry_share);
		ry_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_FragmentActivity.displayView(6);
			}
		});
		RelativeLayout rl_buy_now = (RelativeLayout) rootView
				.findViewById(R.id.rl_buy_now);
		rl_buy_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(_FragmentActivity,
					//	PurchaseItemActivity.class);
				//_FragmentActivity.startActivityForResult(intent,
					//	GlobalConfig.REQUEST_PASSPORT_PURCHASE);
			}
		});
		if (sharedPreferencesManager.getBooleanPreferences(
				SharedPreferencesManager._no_ads, false)) {
			rl_buy_now.setVisibility(View.GONE);
		}
		rl_buy_now.setVisibility(View.GONE);

		RelativeLayout ly_settings = (RelativeLayout) rootView
				.findViewById(R.id.ly_settings);
		ly_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_FragmentActivity.displayView(8);
			}
		});
		RelativeLayout ly_read = (RelativeLayout) rootView
				.findViewById(R.id.ly_read);
		ly_read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_FragmentActivity.displayView(1);
			}
		});
		RelativeLayout ry_fav = (RelativeLayout) rootView
				.findViewById(R.id.ry_fav);
		ry_fav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_FragmentActivity.displayView(3);
			}
		});

		AudioListManager audioListManager = AudioListManager.getInstance();

		audioClass = audioListManager.getRandomAudioClass();

		TextView txt_reciters_name = (TextView) rootView
				.findViewById(R.id.txt_reciters_name);

		txt_reciters_name.setText(_FragmentActivity.getResources().getString(
				R.string.player_verses_pre)
				+ " "
				+ audioClass.getVerseName()
				+ " "
				+ _FragmentActivity.getResources().getString(
						R.string.player_listen_pre)
				+ " "
				+ audioClass.getReciterName());

		ImageView reciter_icon = (ImageView) rootView
				.findViewById(R.id.reciter_icon);

		String image = audioClass.getImage().split(".jpg")[0];
		Context context = reciter_icon.getContext();
		int id = context.getResources().getIdentifier(image, "drawable",
				context.getPackageName());
		reciter_icon.setImageDrawable(_FragmentActivity.getResources()
				.getDrawable(id));
		return rootView;
	}

}
