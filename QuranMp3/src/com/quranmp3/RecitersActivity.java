package com.quranmp3;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class RecitersActivity extends Activity {
	private static final int CONTENT_VIEW_ID = 10101010;
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		ActionBar actionbar;
		actionbar = getActionBar();
		actionbar.setTitle(getResources().getString(R.string.menu_reciters));
		actionbar.setIcon(R.drawable.reciter_icon);
		FrameLayout frame = new FrameLayout(this);
		frame.setId(CONTENT_VIEW_ID);
		setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		if (savedInstanceState == null) {
			Fragment newFragment = new RecitersFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(CONTENT_VIEW_ID, newFragment).commit();
		}
	}

}