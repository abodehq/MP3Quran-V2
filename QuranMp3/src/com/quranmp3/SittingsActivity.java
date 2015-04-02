package com.quranmp3;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quranmp3.controllers.SharedPreferencesManager;
import com.quranmp3.utils.Utils;

public class SittingsActivity extends Activity {

	private Context _scope = null;
	int selectedColorOption = 1;

	private void ToggleFullScreen() {
		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(this);
		if (sharedPreferencesManager.getBooleanPreferences(
				SharedPreferencesManager._full_mode, false)) {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		}
	}

	/** Called when the activity is first created. */
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_sittings);

		_scope = this;

		ActionBar actionbar;
		actionbar = getActionBar();
		actionbar.setTitle(getResources().getString(R.string.menu_settings));
		actionbar.setIcon(R.drawable.ic_action_settings);
		//
		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(_scope);
		int langPosition = Integer.parseInt(sharedPreferencesManager
				.GetStringPreferences(SharedPreferencesManager._lang_id, "1")) - 1;

		TextView txt_language = (TextView) findViewById(R.id.txt_language);
		txt_language.setText(getResources().getStringArray(
				R.array.arr_languages_items)[langPosition]);

		RelativeLayout btn_languages = (RelativeLayout) findViewById(R.id.btn_languages);

		btn_languages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLanguageslist(_scope);

			}
		});

		RelativeLayout btn_mail = (RelativeLayout) findViewById(R.id.btn_mail);

		btn_mail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						ContactusActivity.class);
				startActivity(in);

			}
		});
		RelativeLayout btn_share = (RelativeLayout) findViewById(R.id.btn_share);

		btn_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						MainActivity.class);
				in.putExtra("_type", 103);
				in.putExtra("_request", 4);
				setResult(Activity.RESULT_OK, in);
				finish();
			}
		});

		RelativeLayout btn_apps = (RelativeLayout) findViewById(R.id.btn_apps);

		btn_apps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/developer?id=islam+is+the+way+of+life"));
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ _scope.getPackageName())));
				}
			}
		});

		RelativeLayout btn_rate = (RelativeLayout) findViewById(R.id.btn_rate);

		btn_rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id="
						+ _scope.getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
						| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				try {
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ _scope.getPackageName())));
				}
			}
		});

		RelativeLayout btn_full_mode = (RelativeLayout) findViewById(R.id.btn_full_mode);
		final ImageView cb_full_mode = (ImageView) findViewById(R.id.cb_full_mode);

		btn_full_mode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
						.getInstance(_scope);
				Boolean isChecked = sharedPreferencesManager
						.getBooleanPreferences(
								SharedPreferencesManager._full_mode, false);
				if (!isChecked) {

					sharedPreferencesManager.savePreferences(
							SharedPreferencesManager._full_mode, true);
					cb_full_mode
							.setImageResource(R.drawable.component_style_btn_check_on_focused_holo_dark);

				} else {
					sharedPreferencesManager.savePreferences(
							SharedPreferencesManager._full_mode, false);
					cb_full_mode
							.setImageResource(R.drawable.component_style_btn_check_off_disabled_focused_holo_dark);

				}
				ToggleFullScreen();

			}
		});

		Boolean _full_mode = sharedPreferencesManager.getBooleanPreferences(
				SharedPreferencesManager._full_mode, false);

		if (_full_mode) {

			cb_full_mode
					.setImageResource(R.drawable.component_style_btn_check_on_focused_holo_dark);

		} else {

			cb_full_mode
					.setImageResource(R.drawable.component_style_btn_check_off_disabled_focused_holo_dark);

		}
		ToggleFullScreen();

	}

	@Override
	public void onBackPressed() {
		Intent in = new Intent(getApplicationContext(), MainActivity.class);
		in.putExtra("_type", 103);
		if (forceRefresh)
			in.putExtra("_request", 2);
		else
			in.putExtra("_request", -1);
		setResult(Activity.RESULT_OK, in);
		finish();
	}

	private void SavePreferences() {

	}

	public static boolean forceRefresh = false;

	public void showLanguageslist(Context context) {
		final Dialog dialog = new Dialog(context);

		dialog.setTitle(context.getResources().getString(
				R.string.setting_language_title));
		ListView modeList = new ListView(context);
		final Context _context = context;
		modeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String[] data = getResources().getStringArray(
						R.array.arr_languages_locals);
				String[] lang_ids = getResources().getStringArray(
						R.array.arr_languages_ids);

				SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
						.getInstance(_scope);
				String _local = sharedPreferencesManager.GetStringPreferences(
						SharedPreferencesManager._local, "ar");
				if (!_local.equals(data[position])) {
					Utils.updateLocal(_scope, data[position],
							lang_ids[position]);

					Intent in = new Intent(getApplicationContext(),
							MainActivity.class);
					in.putExtra("_type", 103);
					in.putExtra("_request", 1);
					forceRefresh = true;
					setResult(Activity.RESULT_OK, in);
					finish();
				}
				dialog.cancel();

			}
		});
		String[] languages = getResources().getStringArray(
				R.array.arr_languages_items);

		LanguagesAdapter adapter1 = new LanguagesAdapter(context, languages);

		modeList.setAdapter(adapter1);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(modeList);
		dialog.show();

	}

}
