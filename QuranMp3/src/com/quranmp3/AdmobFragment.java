package com.quranmp3;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quranmp3.controllers.SharedPreferencesManager;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class AdmobFragment extends Fragment {
	// private AdView mAdView;

	public AdmobFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(this.getActivity());
		View rootView;

		boolean no_ads = sharedPreferencesManager.getBooleanPreferences(
				SharedPreferencesManager._no_ads, false);
		if (no_ads) {
			rootView = inflater.inflate(R.layout.empty_ad, container, false);
			return rootView;
		}
		rootView = inflater.inflate(R.layout.fragment_ad, container, false);
		// mAdView = (AdView) rootView.findViewById(R.id.adView);
		// rootView.setVisibility(View.GONE);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		Log.e("test", "on activity start");
		// Gets the ad view defined in layout/ad_fragment.xml with ad unit
		// ID set in
		// values/strings.xml.

		Log.e("test", "add start");
		// Create an ad request. Check logcat output for the hashed device
		// ID to
		// get test ads on a physical device. e.g.
		// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
		// AdRequest adRequest = new AdRequest.Builder().addTestDevice(
		// "ABCDEF012345").build();
		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager
				.getInstance(this.getActivity());
		boolean no_ads = sharedPreferencesManager.getBooleanPreferences(
				SharedPreferencesManager._no_ads, false);
		if (!no_ads) {
			// AdRequest adRequest = new AdRequest.Builder().build();
			// Start loading the ad in the background.
			// mAdView.loadAd(adRequest);
		}
	}

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		// if (mAdView != null) {
		// mAdView.pause();
		// }
		super.onPause();
	}

	/** Called when returning to the activity */
	@Override
	public void onResume() {
		super.onResume();
		// if (mAdView != null) {
		// mAdView.resume();
		// }
	}

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		// if (mAdView != null) {
		// mAdView.destroy();
		// }
		super.onDestroy();
	}
}
