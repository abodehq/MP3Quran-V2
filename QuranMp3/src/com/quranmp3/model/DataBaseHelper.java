package com.quranmp3.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quranmp3.Albumb;
import com.quranmp3.DBActivity;
import com.quranmp3.R;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class DataBaseHelper extends SQLiteOpenHelper {

	public interface DataBaseHelperInterface {
		public void onRequestCompleted();
	}

	private DataBaseHelperInterface mCallback;

	private static String DB_PATH = "/data/data/" + GlobalConfig._DBcontext
			+ "/databases/";
	private static String DB_NAME = "book_db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private int searccTopResult = 30;
	private static final int DATABASE_VERSION = 1;
	String myPath;

	public DataBaseHelper(DataBaseHelperInterface callback) {

		super(DBActivity.getContext(), DB_NAME, null, DATABASE_VERSION);
		this.myContext = DBActivity.getContext();
		mCallback = callback;
		myPath = myContext.getFilesDir().getAbsolutePath()
				.replace("files", "databases")
				+ File.separator + DB_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("upgrade to next level", "start");
		// if (deleteDataBase())
		// InitDB();
		try {
			copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (oldVersion == 1 && newVersion >= 2) {
			// execute upgrade queries
			oldVersion = 2;
		}
		if (oldVersion == 2 && newVersion >= 3) {
			// execute database upgrade queries
			oldVersion = 3;
		}
		Log.e("d", "done");
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void InitDB()// Call when the application Run
	{
		try {

			boolean result = createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {

			openDataBase();

		} catch (SQLException sqle) {

			throw sqle;
		}
		if (mCallback != null)
			mCallback.onRequestCompleted();
	}

	public boolean createDataBase() throws IOException {
		myDataBase = null;
		boolean dbExist = checkDataBase();// check if we DB SQLlite Exist or not

		if (dbExist) {
			return false;
		} else {
			try {
				myDataBase = this.getReadableDatabase();
				myDataBase.close();
				copyDataBase();// //Copy the External DB to the application

				return true;
			} catch (IOException e) {
				return false;

			}
		}

	}

	public boolean checkDataBase() {
		try {
			// String myPath = DB_PATH + DB_NAME;

			File dbFile = new File(myPath);
			return dbFile.exists();
		} catch (SQLiteException e) {
		}
		return false;
	}

	public boolean deleteDataBase()// Delere on Upgrade
	{
		try {
			// String myPath = DB_PATH + DB_NAME;
			File dbFile = new File(myPath);
			if (dbFile.exists()) {
				dbFile.delete();
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		openDataBase();
		// creatVirtualDB();
	}

	public void openDataBase() throws SQLException { // Open the DB

		// Open the database
		// String myPath = DB_PATH + DB_NAME;
		// myDataBase = this.getReadableDatabase();
		if (myDataBase != null) {
			if (myDataBase.isOpen()) {
				// myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				// SQLiteDatabase.NO_LOCALIZED_COLLATORS);
				// myDataBase.op
				myDataBase.close();
			}

		}
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	public void creatVirtualDB() {
		myDataBase
				.execSQL("CREATE VIRTUAL TABLE  vcontents USING fts3(_id INTEGER PRIMARY KEY, content TEXT,chapter_id TEXT ,semi_chapter_id INTEGER ,vname TEXT );");

	}

	@Override
	public synchronized void close() {
		Log.e("close", "close DB");
		if (myDataBase != null)
			myDataBase.close();
		SQLiteDatabase db = this.getReadableDatabase();
		db.close();
		super.close();

	}

	// verses
	public ArrayList<Verses> get_verses(String langId)// get
														// allsemichapters
	{
		ArrayList<Verses> versesList = new ArrayList<Verses>();

		String selectQuery = "SELECT verses.id,verses.ayah_count,verses.place_id,verses_translation.name FROM verses,verses_translation where lang_id ="
				+ langId + " AND verses.id = verses_translation.verses_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Verses verses = new Verses();
					verses.setId(Integer.parseInt(cursor.getString(0)));
					verses.setName(cursor.getString(3));
					verses.setAyahCount(Integer.parseInt(cursor.getString(1)));
					verses.setPlaceId(Integer.parseInt(cursor.getString(2)));
					versesList.add(verses);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}

		return versesList;
	}

	// verses
	public ArrayList<Verses> get_verses_files(String langId,
			ArrayList<Integer> ids, ArrayList<Float> filesSize)// get
	// allsemichapters
	{
		ArrayList<Verses> versesList = new ArrayList<Verses>();
		String idQuery = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i == 0)
				idQuery = idQuery + ids.get(i);
			else
				idQuery = idQuery + "," + ids.get(i);
		}
		String selectQuery = "SELECT verses.id,verses.ayah_count,verses.place_id,verses_translation.name FROM verses,verses_translation where lang_id ="
				+ langId
				+ " AND verses.id IN ("
				+ idQuery
				+ ") "
				+ " AND verses.id = verses_translation.verses_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Verses verses = new Verses();
					verses.setId(Integer.parseInt(cursor.getString(0)));
					verses.setName(cursor.getString(3));
					verses.setAyahCount(Integer.parseInt(cursor.getString(1)));
					verses.setPlaceId(Integer.parseInt(cursor.getString(2)));
					versesList.add(verses);
					verses.setSize(getSize(
							Integer.parseInt(cursor.getString(0)), filesSize,
							ids));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}

		return versesList;
	}

	private float getSize(int id, ArrayList<Float> filesSize,
			ArrayList<Integer> ids) {
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id)
				return filesSize.get(i) / (float) 1048576.0;
		}
		return (float) 0;

	}

	public ArrayList<Verses> get_random_verses(String langId)// get
	// allsemichapters
	{
		ArrayList<Verses> versesList = new ArrayList<Verses>();

		String selectQuery = "SELECT verses.id,verses.ayah_count,verses.place_id,verses_translation.name FROM verses,verses_translation where lang_id ="
				+ langId
				+ " AND verses.id = verses_translation.verses_id ORDER BY RANDOM() LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {
					Verses verses = new Verses();
					verses.setId(Integer.parseInt(cursor.getString(0)));
					verses.setName(cursor.getString(3));
					verses.setAyahCount(Integer.parseInt(cursor.getString(1)));
					verses.setPlaceId(Integer.parseInt(cursor.getString(2)));
					versesList.add(verses);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return versesList;
	}

	// verses
	public Reciters get_reciters_by_id(int id, String langId)// get
	// allsemichapters
	{

		String selectQuery = "SELECT reciters.id,reciters.image,reciters.country_id,reciters.position,reciters.status,reciters_translation.name,reciters.audio_base_path FROM reciters,reciters_translation where lang_id ="
				+ langId
				+ " AND reciters.id="
				+ id
				+ " AND reciters.id = reciters_translation.reciter_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Reciters reciters = new Reciters();
		// looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {

					reciters.setId(Integer.parseInt(cursor.getString(0)));
					reciters.setName(cursor.getString(5));
					reciters.setImage(cursor.getString(1));
					reciters.setCountryId(Integer.parseInt(cursor.getString(2)));
					reciters.setOrder(Integer.parseInt(cursor.getString(3)));
					reciters.setStatus(Integer.parseInt(cursor.getString(4)));
					reciters.setAudioBasePath(cursor.getString(6));

				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return reciters;
	}

	public ArrayList<Reciters> get_reciters(String langId)// get
															// allsemichapters
	{
		ArrayList<Reciters> recitersList = new ArrayList<Reciters>();
		String selectQuery = "SELECT reciters.id,reciters.image,reciters.country_id,reciters.position,reciters.status,reciters_translation.name,reciters.audio_base_path FROM reciters,reciters_translation where lang_id ="
				+ langId + " AND reciters.id = reciters_translation.reciter_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {

					Reciters reciters = new Reciters();
					reciters.setId(Integer.parseInt(cursor.getString(0)));
					reciters.setName(cursor.getString(5));
					reciters.setImage(cursor.getString(1));
					reciters.setCountryId(Integer.parseInt(cursor.getString(2)));
					reciters.setOrder(Integer.parseInt(cursor.getString(3)));
					reciters.setStatus(Integer.parseInt(cursor.getString(4)));
					reciters.setAudioBasePath(cursor.getString(6));
					recitersList.add(reciters);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return recitersList;
	}

	public ArrayList<Reciters> get_reciters_folders(String langId,
			List<Albumb> list)// get
	// allsemichapters
	{

		String idQuery = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0)
				idQuery = idQuery + list.get(i).getName();
			else
				idQuery = idQuery + "," + list.get(i).getName();
		}
		ArrayList<Reciters> recitersList = new ArrayList<Reciters>();
		String selectQuery = "SELECT reciters.id,reciters.image,reciters.country_id,reciters.position,reciters.status,reciters_translation.name,reciters.audio_base_path FROM reciters,reciters_translation where lang_id ="
				+ langId
				+ " AND reciters.id IN ("
				+ idQuery
				+ ") "
				+ " AND reciters.id = reciters_translation.reciter_id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		try {
			if (cursor.moveToFirst()) {
				do {

					Reciters reciters = new Reciters();
					reciters.setId(Integer.parseInt(cursor.getString(0)));
					reciters.setName(cursor.getString(5));
					reciters.setImage(cursor.getString(1));
					reciters.setCountryId(Integer.parseInt(cursor.getString(2)));
					reciters.setOrder(Integer.parseInt(cursor.getString(3)));
					reciters.setStatus(Integer.parseInt(cursor.getString(4)));
					reciters.setAudioBasePath(cursor.getString(6));
					reciters.setItems(getFoldersLength(list,
							cursor.getString(0)));
					recitersList.add(reciters);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return recitersList;
	}

	private String getFoldersLength(List<Albumb> list, String reciter_id) {

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i).getName().equals(reciter_id)) {
				Log.e("folder", list.get(i).getData() + " : " + reciter_id);
				return list.get(i).getData();
			}
		}
		return "0";

	}

	public ArrayList<Reciters> get_random_reciters(String langId)// get
	// allsemichapters
	{
		ArrayList<Reciters> recitersList = new ArrayList<Reciters>();
		String selectQuery = "SELECT reciters.id,reciters.image,reciters.country_id,reciters.position,reciters.status,reciters_translation.name,reciters.audio_base_path FROM reciters,reciters_translation where lang_id ="
				+ langId
				+ " AND reciters.id = reciters_translation.reciter_id ORDER BY RANDOM() LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			if (cursor.moveToFirst()) {
				do {

					Reciters reciters = new Reciters();
					reciters.setId(Integer.parseInt(cursor.getString(0)));
					reciters.setName(cursor.getString(5));
					reciters.setImage(cursor.getString(1));
					reciters.setCountryId(Integer.parseInt(cursor.getString(2)));
					reciters.setOrder(Integer.parseInt(cursor.getString(3)));
					reciters.setStatus(Integer.parseInt(cursor.getString(4)));
					reciters.setAudioBasePath(cursor.getString(6));
					recitersList.add(reciters);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return recitersList;
	}

	// PlayLists
	// verses
	public ArrayList<Mp3PlayLists> get_play_lists()// get
													// allsemichapters
	{

		ArrayList<Mp3PlayLists> playlists = new ArrayList<Mp3PlayLists>();

		String selectQuery = "SELECT play_lists.id,play_lists.name,play_lists.position,play_lists.date,count(play_lists_verses.id) FROM play_lists LEFT OUTER JOIN play_lists_verses ON  play_lists.id=play_lists_verses.play_list_id group by play_lists.id";
		// selectQuery =
		// "SELECT play_lists.id,play_lists.name,play_lists.position,play_lists.date FROM play_lists,play_lists_verses where  play_lists.id = play_lists_verses.play_list_id ";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			if (cursor.moveToFirst()) {
				do {
					Mp3PlayLists playlist = new Mp3PlayLists();
					playlist.setId(Integer.parseInt(cursor.getString(0)));
					playlist.setName(cursor.getString(1));
					playlist.setOrder(Integer.parseInt(cursor.getString(2)));
					playlist.setDate(cursor.getString(3));
					playlist.setCount(Integer.parseInt(cursor.getString(4)));
					Log.e("playlist", playlist.getCount() + "");
					playlists.add(playlist);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}

		return playlists;
	}

	public ArrayList<Mp3PlayLists> delete_playlist_by_id(int id)// get
																// allsemichapters
	{
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			db.delete("play_lists", "id=" + id, null);

			db.delete("play_lists_verses", "play_list_id=" + id, null);
			GlobalConfig.ShowSuccessToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_del_list_s));

		} catch (Exception e) {
			GlobalConfig.ShowErrorToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_del_list_e));
		} finally {

			db.close();
		}

		return get_play_lists();
	}

	public void insert_playlist(Mp3PlayLists mp3PlayerList) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("name", mp3PlayerList.getName());
			values.put("date", mp3PlayerList.getDate());
			values.put("position", mp3PlayerList.getOrder());
			// Inserting Row
			db.insert("play_lists", null, values);

			GlobalConfig.ShowSuccessToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_ins_list_s));
		} catch (Exception e) {
			GlobalConfig.ShowErrorToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_ins_list_e));
		} finally {

			db.close();
		}
		// Closing database connection
	}

	public void UpdatePlaylist(int id, String label) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("name", label);

		try {
			db.update("play_lists", values, "id=" + id, null);

			GlobalConfig.ShowSuccessToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_up_list_s));
		} catch (Exception e) {
			GlobalConfig.ShowErrorToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_up_list_e));
		} finally {

			db.close();
		}

	}

	// verses
	public ArrayList<AudioClass> get_play_lists_verses(String langId,
			String playListId)// get
	// allsemichapters
	{
		ArrayList<AudioClass> audioClassList = new ArrayList<AudioClass>();

		String selectQuery = "SELECT  reciters.id,reciters.image,reciters_translation.name,reciters.audio_base_path,verses.id,verses.ayah_count,verses.place_id,verses_translation.name FROM reciters,reciters_translation,verses,verses_translation,play_lists_verses  where reciters_translation.lang_id ="
				+ langId
				+ " AND verses_translation.lang_id ="
				+ langId
				+ " AND play_lists_verses.play_list_id ="
				+ playListId
				+ " AND reciters.id = reciters_translation.reciter_id AND verses.id = verses_translation.verses_id  AND play_lists_verses.reciter_id=reciters.id And play_lists_verses.verses_id = verses.id";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try {
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {

					AudioClass audioClass = new AudioClass();
					audioClass.setReciterId(Integer.parseInt(cursor
							.getString(0)));
					audioClass.setImage(cursor.getString(1));
					audioClass.setReciterName(cursor.getString(2));
					audioClass
							.setVerseId(Integer.parseInt(cursor.getString(4)));
					audioClass.setAyahCount(Integer.parseInt(cursor
							.getString(5)));
					audioClass
							.setPlaceId(Integer.parseInt(cursor.getString(6)));
					audioClass.setVerseName(cursor.getString(7));
					String audioBasePath = cursor.getString(3);
					String versesId = Utils.getAudioMp3Name(Integer
							.parseInt(cursor.getString(4)));
					audioClass.setAudioPath(audioBasePath + versesId);
					audioClassList.add(audioClass);
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
			db.close();
		}
		return audioClassList;
	}

	public void insert_playlist_verses(Mp3PlayListsVerses mp3PlayListsVerses) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("play_list_id", mp3PlayListsVerses.getPlayListId());
			values.put("reciter_id", mp3PlayListsVerses.getReciterId());
			values.put("verses_id", mp3PlayListsVerses.getVerseId());
			// Inserting Row
			db.insert("play_lists_verses", null, values);
			GlobalConfig.ShowSuccessToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_ins_list_verse_s));
		} catch (SQLException e) {

			GlobalConfig.ShowErrorToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_ins_list_verse_e));
		} finally {

			db.close();
		}
		// Closing database connection
	}

	public void delete_playlist_verses(Mp3PlayListsVerses mp3PlayListsVerses) {

		SQLiteDatabase db = this.getReadableDatabase();
		try {
			db.delete("play_lists_verses",
					"play_list_id=? AND reciter_id=? AND verses_id=?",
					new String[] { mp3PlayListsVerses.getPlayListId() + "",
							mp3PlayListsVerses.getReciterId() + "",
							mp3PlayListsVerses.getVerseId() + "" });

			GlobalConfig.ShowSuccessToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_del_list_verse_s));
		} catch (SQLException e) {
			e.printStackTrace();
			GlobalConfig.ShowErrorToast(
					GlobalConfig.mainActivity,
					GlobalConfig.mainActivity.getResources().getString(
							R.string.db_del_list_verse_e));

		} finally {
			db.close();
		}

	}

	public boolean get_play_list_verses_exist(
			Mp3PlayListsVerses mp3PlayListsVerses) {
		boolean result = false;

		// Select All Query
		String selectQuery = "SELECT * FROM play_lists_verses where play_list_id= "
				+ mp3PlayListsVerses.getPlayListId()
				+ " AND reciter_id= "
				+ mp3PlayListsVerses.getReciterId()
				+ " AND verses_id= "
				+ mp3PlayListsVerses.getVerseId();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {

				result = true;
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return !result;
	}
}