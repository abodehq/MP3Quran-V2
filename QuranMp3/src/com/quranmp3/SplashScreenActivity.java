package com.quranmp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quranmp3.controllers.SharedPreferencesManager;
import com.quranmp3.model.DataBaseHelper;
import com.quranmp3.model.DataBaseHelper.DataBaseHelperInterface;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class SplashScreenActivity extends Activity implements
		DataBaseHelperInterface {

	protected int _splashTime = 15000; // Time before Run App Main Activity
	private Thread splashTread;
	// private ProgressBar loading;
	private Context context;
	private DataBaseHelper myDbHelper;
	DataBaseHelperInterface dataBaseHelperInterface;
	TextView TextLoadingFirst;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context = SplashScreenActivity.this;

		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(context);
		GlobalConfig.local = sharedPreferencesManager.GetStringPreferences(
				SharedPreferencesManager._local, GlobalConfig.local);
		GlobalConfig.lang_id = sharedPreferencesManager.GetStringPreferences(
				SharedPreferencesManager._lang_id, GlobalConfig.lang_id);
		Utils.updateLocal(context, GlobalConfig.local, GlobalConfig.lang_id);
		GlobalConfig._DBcontext = getApplicationContext().getPackageName();

		dataBaseHelperInterface = this;

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// application full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.ly_splash_screen);// Splash Layout
		TextLoadingFirst = (TextView) findViewById(R.id.TextLoadingFirst);

		TextView book_txt = (TextView) findViewById(R.id.book_txt);

		String fontPath = "fonts/header.ttf";
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

		book_txt.setTypeface(tf);
		book_txt.setText(getString(R.string.book_name));

		final ImageView loadingImage = (ImageView) findViewById(R.id.imageView1);
		final Animation myRotation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.rotator);
		loadingImage.startAnimation(myRotation);
		try {
			if (myDbHelper == null)
				myDbHelper = new DataBaseHelper(dataBaseHelperInterface);
		} catch (Exception e) {
			e.getStackTrace();
		}

		if (!myDbHelper.checkDataBase()) {
			TextLoadingFirst.setVisibility(View.VISIBLE);
			// GlobalConfig.ShowLongToast(this,
			// " Õ„Ì· «·ﬂ «Ì ·√Ê· „—… /n «·—Ã«¡ «·«‰ Ÿ«—");
			// book_txt.setVisibility(book_txt.VISIBLE);

		}
		// /wait for 2 seconds and then show the loading image.

		final long DELAY = 1000; // in ms

		CopyFiles CopyFilesa = new CopyFiles();
		CopyFilesa.execute();
		// Calendar cal = Calendar.getInstance();
		// cal.add(Calendar.SECOND, 5);
		// Intent intent = new Intent(context, AlarmReceiverActivity.class);
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
		// intent, 0);
		// AlarmManager am = (AlarmManager)
		// getSystemService(Activity.ALARM_SERVICE);
		// am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		// System.currentTimeMillis() + 28800000, 28800000, pendingIntent);
		// 28800000
	}

	@Override
	public void onRequestCompleted() {
		Log.e("done", "done");

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {// check if user Click on
													// the splash to start app
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// synchronized (splashTread) {
			// splashTread.notifyAll();
			// }
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByBackKey();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void exitByBackKey() {

		AlertDialog alertbox = new AlertDialog.Builder(this)

				.setMessage(getResources().getString(R.string.app_exit))
				.setPositiveButton(
						getResources().getString(R.string.app_exit_confirm),
						new DialogInterface.OnClickListener() {

							// do something when the button is clicked
							public void onClick(DialogInterface arg0, int arg1) {

								System.exit(0);
								// close();

							}
						})
				.setNegativeButton(
						getResources().getString(R.string.app_exit_cancle),
						new DialogInterface.OnClickListener() {

							// do something when the button is clicked
							public void onClick(DialogInterface arg0, int arg1) {
							}
						}).show();
	}

	@Override
	public void onBackPressed() {

		myDbHelper.close();
		finish();

	}

	public class CopyFiles extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(String x) {
			super.onPostExecute("");
			Log.e("onpost", "onpost");
			GlobalConfig.myDbHelper = myDbHelper;
			SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
					.getInstance(context);

			sharedPreferencesManager.SetupPreferences();

			if (sharedPreferencesManager.getBooleanPreferences(
					SharedPreferencesManager._first_run, true)) {
				sharedPreferencesManager.savePreferences(
						SharedPreferencesManager._first_run, false);
				showLanguageslist();

			} else {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						GlobalConfig.myDbHelper.get_verses("2");
						Intent i = new Intent();
						i.setClass(context, com.quranmp3.MainActivity.class);
						startActivity(i);
						finish();
						// Actions to do after 10 seconds
					}
				}, 1000);

			}

		}

		@Override
		protected String doInBackground(Void... arg0) {
			Log.e("int", "int");
			myDbHelper.InitDB();
			// TODO Auto-generated method stub
			return "";
		}
	}

	public void showLanguageslist() {
		final Dialog dialog = new Dialog(context);
		dialog.setCancelable(false);
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
				Utils.updateLocal(_context, data[position], lang_ids[position]);

				dialog.cancel();
				Intent i = new Intent();
				i.setClass(_context, com.quranmp3.MainActivity.class);
				startActivity(i);
				finish();

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

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			showLanguageslist();

		}
	};

}
