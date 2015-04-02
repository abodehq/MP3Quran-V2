package com.quranmp3.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quranmp3.MainActivity;
import com.quranmp3.R;
import com.quranmp3.model.DataBaseHelper;

public class GlobalConfig extends Activity {
	public static final String SKU = "com.quranmp3.noadds";
	public static final String BASE_64_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhMvG5Sj+gS/ctSKKPDGaD5TpOlUOrJ6KuiWAp0URAKGB4h2ZatFujVLpTGsx1610dWvFHHEdOgt5tQPhPtJX/3cUZSTZtWMMCLim71LT2bPnR30dDLLh1DpOMnb9eREfaigYROvGiQ0+Lqnu/WEUOikukqQOiCEVUtQmGk8lJa7KF2naUv4jXGQ9pWVBidQLXADH9fv8HZZntpcb6fmORtrzKUOJpOna+vSOx2ZXf6JljvxU1bI36Ir/2Ye9cR9/Dujzqt+UsM5V2KDw+c4s7guRV49FlQDEs+wtBdVy4NV3582hax5Gn8pNMxdq6qt6Tze0MLIaFxcfDthq7gqMywIDAQAB";
	// test
	public static final String BASE_64_KEY1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArFKsWOCguKlHkKngnwp3JFU/VF+ChVUNSgNlWdOXPKoW+ZEHZsWRD9CReNhiJoLvnkhlGioWyl729J9uNZF0j3qOAzTIBJpkDfYcGz8nYmUJrWcPxzGuQ7OMcIArJYJvpnd3bxafomhdqc1OzEhK/fEX26lbzh4HHvOpcFmrhfUjgkLvwzO/+mlvHAv9wUWMNplnEMrAOJkMyewz3qZuzAHY04m6OQYu/Xqp6cGtBXfntzx3UtaSY9bpjE0c3dGZQsf+4J9JtLqsGEfMf7JvAiqpBLeYwfNhYMrL7eeBlqTEN/pTkqJRsRdP24jxePWgA0VMK3R8ueh55/Tl0cvN3QIDAQAB";

	public static final String SKU1 = "android.test.purchased"; // Replace this

	public static final int REQUEST_PASSPORT_PURCHASE = 2012; // with your

	public static String _DBcontext = "";
	public static String local = "ar";
	public static String lang_id = "1";
	public static MainActivity mainActivity = null;
	public static int fontSize = 18;
	public static int fontColor = -16777216;
	public static int bgColor = -1;

	// default values
	public static int defaultFontSize = 18;
	public static int defaultFColor = -16777216;
	public static int defaultBgColor = -1;
	// ////
	public static int titleFontSize = 18;
	public static String fontPath = "fonts/arabic.ttf";
	public static String storageDomainRoot = "http://s3.amazonaws.com";
	public static String shareAppPath = "https://play.google.com/store/apps/details?id=eqratech.fatiha";
	public static String htmlContentStructure = "";
	public static int screenWidth = 750;// 570;// 240;// 750;
	public static int screenHeight = 1177;// 816;// 320;// 1177;
	public static DataBaseHelper myDbHelper = null;
	// log file..
	public static Boolean showLog = true;

	public static void Log(String tag, String msg) {
		if (showLog)
			try {
				android.util.Log.e(tag, msg);
			} catch (Exception e) {

			}
	}

	public static void ShowSuccessToast(Context context, String msg) {
		clearTosts();
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);

		LayoutInflater inflater1 = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater1.inflate(R.layout.ly_toast, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		textMessage.setText(msg);
		toast.setGravity(Gravity.TOP | Gravity.LEFT | Gravity.FILL_HORIZONTAL,
				0, ((Activity) context).getActionBar().getHeight());

		toast.setView(view);
		toast.show();
	}

	public static void ShowErrorToast(Context context, String msg) {
		clearTosts();
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);

		LayoutInflater inflater1 = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater1.inflate(R.layout.ly_toast_error, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		textMessage.setText(msg);
		toast.setGravity(Gravity.TOP | Gravity.LEFT | Gravity.FILL_HORIZONTAL,
				0, ((Activity) context).getActionBar().getHeight());

		toast.setView(view);
		toast.show();
	}

	static Toast toast = null;

	public static void ShowInfoToast(Context context, String msg) {
		clearTosts();
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);

		LayoutInflater inflater1 = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater1.inflate(R.layout.ly_info_toast, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		textMessage.setText(msg);
		toast.setGravity(Gravity.TOP | Gravity.LEFT | Gravity.FILL_HORIZONTAL,
				0, ((Activity) context).getActionBar().getHeight());

		toast.setView(view);
		toast.show();

	}

	public static DataBaseHelper GetmyDbHelper() {
		try {
			if (myDbHelper == null) {
				myDbHelper = new DataBaseHelper(null);
				myDbHelper.InitDB();
			}
		} catch (Exception e) {

		}
		return myDbHelper;
	}

	public static void clearTosts() {
		if (toast != null)
			toast.cancel();
	}

	public static void ShowToast(Context context, String msg) {
		clearTosts();
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);

		LayoutInflater inflater1 = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater1.inflate(R.layout.ly_toast, null);
		TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
		textMessage.setText(msg);
		toast.setGravity(Gravity.TOP | Gravity.LEFT | Gravity.FILL_HORIZONTAL,
				0, ((Activity) context).getActionBar().getHeight());

		toast.setView(view);
		toast.show();

	}
}