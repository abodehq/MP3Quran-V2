package com.quranmp3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.quranmp3.DownloadService.DownloadBinder;
import com.quranmp3.controllers.SharedPreferencesManager;
import com.quranmp3.controllers.UndoManager;
import com.quranmp3.model.AudioClass;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

//import android.app.Fragment;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener, SearchView.OnQueryTextListener,
		SearchView.OnCloseListener {

	// **new code
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private MainActivity _scope;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter1;
	// end code

	// action bar
	private ActionBar actionBar;

	// Refresh menu item
	private MenuItem refreshMenuItem;

	// connect to service
	DownloadService dService;
	boolean dBound = false;
	private ServiceConnection dConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			DownloadBinder binder = (DownloadBinder) service;
			dService = binder.getService();
			dBound = true;
			Log.e("dservice connect", "dservice connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			dBound = false;
			Log.e("dservice connect", "disconnected");
		}
	};

	public void DownloadSura(AudioClass audioClass) {
		if (Utils.ifSuraDownloaded(_scope, audioClass)) {
			GlobalConfig.ShowErrorToast(
					this,
					_scope.getResources()
							.getString(R.string.download_exist_pre)
							+ " "
							+ audioClass.getVerseName()
							+ " "
							+ _scope.getResources().getString(
									R.string.download_exist_after));
		} else {
			if (dBound) {
				Intent intent = new Intent(getApplicationContext(),
						DownloadService.class);
				// intent.putExtra(MyMediaPlayerService.START_PLAY, true);
				startService(intent);
				if (dService.CheckSuraIsDownloading(audioClass)) {
					GlobalConfig.ShowErrorToast(this, _scope.getResources()
							.getString(R.string.download_error));

				} else {
					dService.DownloadSura(audioClass);
					GlobalConfig.ShowSuccessToast(
							this,
							_scope.getResources().getString(
									R.string.download_start_pre)
									+ " "
									+ audioClass.getVerseName()
									+ " "
									+ _scope.getResources().getString(
											R.string.download_start_after));
				}

			}
		}
	}
	@TargetApi(14)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("creat", "creat");
		ToggleFullScreen();
		setContentView(R.layout.activity_main);

		// if (savedInstanceState == null) {
		// Fragment _fragment = null;
		// _fragment = new AdmobFragment();
		// FragmentManager _fragmentManager = getFragmentManager();

		// _fragmentManager.beginTransaction().add(R.id.container, _fragment)
		// .commit();

		// }
		_scope = this;
		Utils.updateLocal(_scope, GlobalConfig.local, GlobalConfig.lang_id);

		GlobalConfig.mainActivity = this;
		actionBar = getActionBar();
		// actionBar.setIcon(R.drawable.ic_player_icon);
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));
		// What's hot, We will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons
				.getResourceId(6, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons
				.getResourceId(7, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons
				.getResourceId(8, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons
				.getResourceId(9, -1)));
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
		// .getResourceId(3, -1), true, ""));
		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter1 = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter1);

		// enabling action bar app icon and behaving it as toggle button
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					displayViewRun(0);

				}
			}, 200);
		}
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));

		Log.e("Main Start D Service", "Main service Download");
		// start Download Service

		// ///connect

		Intent intent = new Intent(this, DownloadService.class);
		this.bindService(intent, dConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		// Associate searchable configuration with the SearchView
		// SearchManager searchManager = (SearchManager)
		// getSystemService(Context.SEARCH_SERVICE);
		// searchView = (SearchView) menu.findItem(R.id.action_search)
		// .getActionView();
		// searchView.setSearchableInfo(searchManager
		// .getSearchableInfo(getComponentName()));

		// new code
		// getMenuInflater().inflate(R.menu.main, menu);
		// setupSearchView();
		// end
		return super.onCreateOptionsMenu(menu);
	}

	SearchView searchView;

	private void setupSearchView() {

		searchView.setIconifiedByDefault(true);
		int searchPlateId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		EditText searchPlate = (EditText) searchView
				.findViewById(searchPlateId);
		searchPlate.setTextColor(getResources().getColor(
				R.color.main_theme_color));
		// searchPlate.setBackgroundResource(R.color.main_theme_color);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			List<SearchableInfo> searchables = searchManager
					.getSearchablesInGlobalSearch();

			// Try to use the "applications" global search provider
			SearchableInfo info = searchManager
					.getSearchableInfo(getComponentName());
			for (SearchableInfo inf : searchables) {
				if (inf.getSuggestAuthority() != null
						&& inf.getSuggestAuthority().startsWith("applications")) {
					info = inf;
				}
			}
			// searchView.setSearchableInfo(info);
		}

		searchView.setOnQueryTextListener(_scope);
		searchView.setOnCloseListener(_scope);
	}

	public boolean onQueryTextChange(String newText) {
		Log.e("Query = ", newText);
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		Log.e("Query = ", query + " : submitted");
		return false;
	}

	public boolean onClose() {
		Log.e("Closed!", "close");
		return false;
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// display view for selected nav drawer item
			displayView(position);
		}
	}

	Fragment lfragment = null;
	int _position = 0;

	public void displayView(int position) {

		setTitle(getString(R.string.loading_text));
		int delay = 400;
		_position = position;
		switch (position) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:

			if (lfragment == null)
				lfragment = new LoadingFragment();
			FragmentManager fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, lfragment).commit();

			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			break;

		case 7:
		case 8:
		case 9:
			delay = 100;
			break;
		default:
			delay = 400;
			break;
		}
		mDrawerLayout.closeDrawer(mDrawerList);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				displayViewRun(_position);

			}
		}, delay);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	// public void OnPageViewClickListener() {

	// ((HomeFragment) (fragment)).OnPageViewClickListener();
	// }

	Fragment fragment = null;
	int currentPosition = 0;
	boolean add_to_undo = true;

	public void displayViewRun(int position) {
		// update the main content by replacing fragments
		UndoManager undoManager = UndoManager.getInstance();
		if (position != 7 && position != 8 && position != 9) {
			if (fragment != null) {
				getFragmentManager().beginTransaction().remove(fragment)
						.commit();

			}
		}
		fragment = null;
		Intent i = null;
		switch (position) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			if (add_to_undo)
				undoManager.addUndoAction(currentPosition, -1, -1);
			add_to_undo = true;
			break;

		}
		switch (position) {
		case 0:

			fragment = new MainFragment();
			break;
		case 1:

			fragment = new PlayerFragment();
			break;
		case 2:

			fragment = new RecitersFragment();
			break;
		case 3:
			fragment = new VersesFragment();
			break;
		case 4:
			fragment = new DownloaderFragment();
			break;
		case 5:
			fragment = new FoldersFragment();

			break;
		case 6:
			fragment = new PlayListsFragment();

			break;
		case 7:
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");

			String shareBody = getString(R.string.share_body)
					+ "http://play.google.com/store/apps/details?id="
					+ _scope.getPackageName();

			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

			startActivity(Intent.createChooser(sharingIntent,
					getString(R.string.share_title)));
			setTitle(navMenuTitles[currentPosition]);

			break;
		case 8:

			i = new Intent();
			i.setClass(_scope, SittingsActivity.class);
			i.putExtra("selectedTab", "1");
			mDrawerList.setItemChecked(position, false);
			setTitle(navMenuTitles[currentPosition]);
			mDrawerLayout.closeDrawer(mDrawerList);
			startActivityForResult(i, 103);
			break;
		case 9:

			mDrawerList.setItemChecked(position, false);
			setTitle(navMenuTitles[currentPosition]);
			mDrawerLayout.closeDrawer(mDrawerList);
			exitByBackKey();
			break;
		case 10:
			Fragment _fragment = new FilesFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.frame_container, _fragment).commit();
			mDrawerLayout.closeDrawer(mDrawerList);
			break;

		default:
			break;
		}

		if (fragment != null) {

			FragmentManager fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			currentPosition = position;
		} else {
			// error in creating fragment
			Log.e("MainActivity", "------");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("Config has changed", "changed");
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * On selecting action bar icons
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_search:
			// search action
			displayView(1);
			return true;
		case R.id.action_books:
			displayView(3);
			return true;
		case R.id.action_semi_chapters:
			// refresh
			displayView(2);
			return true;
		case R.id.action_settings:
			SittingsActivity.forceRefresh = false;
			displayView(5);
			return true;
		case R.id.exit:
			exitByBackKey();
			return true;
		case R.id.fav:
			displayView(6);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

		// toggle nav drawer on selecting action bar app icon/title

	}

	/**
	 * Launching new activity
	 * */

	/*
	 * Actionbar navigation item select listener
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Action to be taken after selecting a spinner item
		return false;
	}

	/**
	 * Async task to load the data from server
	 * **/
	private class SyncData extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);

			// refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			// not making real request in this demo
			// for now we use a timer to wait for sometime
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		@TargetApi(14)
		@Override
		protected void onPostExecute(String result) {
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
		}
	};

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.exit).setVisible(!drawerOpen);
		menu.findItem(R.id.fav).setVisible(!drawerOpen);
		if (fragment != null) {
			// Log.e("fragment", fragment.getTag());
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (GlobalConfig.REQUEST_PASSPORT_PURCHASE == requestCode) {
			if (Activity.RESULT_OK == resultCode) {
				SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
						.getInstance(_scope);
				sharedPreferencesManager.savePreferences(
						SharedPreferencesManager._no_ads, true);
				displayView(_position);
			} else {
				Log.e("sss", "error");
			}
		}
		if (requestCode == 1) {

			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				displayView(3);
			}
		}
		if (requestCode == 103) {// settings actions
			if (resultCode == Activity.RESULT_OK) {
				if (data.getExtras() != null) {
					if (data.getExtras().getInt("_type") == 103) {
						if (data.getExtras().getInt("_request") == 1) {
							displayView(8);
						}
						if (data.getExtras().getInt("_request") == 2) {
							ToggleFullScreen();
							finish();
							startActivity(new Intent(_scope, MainActivity.class));
						}
						if (data.getExtras().getInt("_request") == -1) {
							ToggleFullScreen();
						}
						if (data.getExtras().getInt("_request") == 3) {
							ToggleFullScreen();
						}
						if (data.getExtras().getInt("_request") == 4) {
							displayView(7);
						}
					}
				}
			}
		}

	}

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

	@Override
	public void onBackPressed() {
		UndoManager undoManager = UndoManager.getInstance();
		HashMap<String, Integer> map = undoManager.getUndoAction();
		if (map.get("_id") == -1)
			exitByBackKey();
		else {
			if (map.get("_id") == 1) {

			}
			add_to_undo = false;
			displayView(map.get("_id"));
		}

	}

	private void exitByBackKey() {

		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.exit))
				.setMessage(getResources().getString(R.string.app_exit))
				.setPositiveButton(
						getResources().getString(R.string.app_exit_confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								System.exit(0);

							}
						})
				.setNegativeButton(
						getResources().getString(R.string.app_exit_cancle),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Intent intent = new Intent(getApplicationContext(),
		// MyMediaPlayerService.class);
		// stopService(intent);
		Log.e("Main activity", "---->destroy");
		if (dBound) {

			unbindService(dConnection);
			dBound = false;
		}
		MyMediaPlayerService.isMainAppRunning = false;
		// start player service

	}

	@Override
	public void onStop() {
		super.onStop();
		if (dBound) {

			unbindService(dConnection);
			dBound = false;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// ///connect
		if (!dBound) {

			Intent intent = new Intent(this, DownloadService.class);
			this.bindService(intent, dConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		MyMediaPlayerService.isMainAppRunning = true;
		// Bind to LocalService
		// Intent intent = new Intent(_FragmentActivity,
		// MyMediaPlayerService.class);
		// _FragmentActivity.bindService(intent, mConnection,
		// Context.BIND_AUTO_CREATE);
	}

}
