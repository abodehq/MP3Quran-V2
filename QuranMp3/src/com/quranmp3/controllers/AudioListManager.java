package com.quranmp3.controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import com.quranmp3.model.AudioClass;
import com.quranmp3.model.Reciters;
import com.quranmp3.model.Verses;
import com.quranmp3.utils.GlobalConfig;
import com.quranmp3.utils.Utils;

public class AudioListManager {
	public static String reciterId = null;
	public static Boolean updatePlayer = true;
	private static AudioListManager instance = null;
	private ArrayList<AudioClass> playerAudioList = new ArrayList<AudioClass>();
	private ArrayList<AudioClass> audioList = new ArrayList<AudioClass>();

	public static AudioListManager getInstance() {
		if (instance == null) {
			instance = new AudioListManager();
		}
		return instance;
	}

	public Boolean getUpdatePlayerStatus() {
		return updatePlayer;
	}

	public void setUpdatePlayerStatus(Boolean status) {
		updatePlayer = status;
	}

	// Constructor
	public AudioListManager() {
		if (reciter == null) {
			reciter = GlobalConfig.GetmyDbHelper()
					.get_random_reciters(GlobalConfig.lang_id).get(0);

		}
		if (verses == null) {
			verses = GlobalConfig.GetmyDbHelper()
					.get_random_verses(GlobalConfig.lang_id).get(0);

		}
		FillRandomAudio();

	}

	public void FillRandomAudio() {
		if (playerAudioList.isEmpty()) {
			AudioClass audio = new AudioClass();
			audio.setReciterId(reciter.getId());
			audio.setReciterName(reciter.getName());
			audio.setImage(reciter.getImage());

			audio.setVerseId(verses.getId());
			audio.setVerseName(verses.getName());
			audio.setPlaceId(verses.getPlaceId());
			audio.setAyahCount(verses.getAyahCount());
			String versesId = Utils.getAudioMp3Name(verses.getId());
			audio.setAudioPath(reciter.getAudioBasePath() + versesId);
			_AudioClass = audio;
			playerAudioList.add(audio);
		}
	}

	// ----------------------
	Reciters reciter = null;
	Verses verses = null;

	public Reciters getSelectedReciter() {
		return reciter;
	}

	AudioClass _AudioClass = null;

	public AudioClass getRandomAudioClass() {
		return _AudioClass;
	}

	public void setSelectedReciter(Reciters _reciter) {
		reciter = _reciter;
	}

	// -------------

	public ArrayList<AudioClass> getPlayList() {
		return playerAudioList;
	}

	public void AddNewSura(AudioClass sura) {
		playerAudioList.add(sura);

	}

	public void AddNewSuraAt(int index, AudioClass sura) {
		playerAudioList.add(index, sura);

	}

	public void deletAllSuras() {
		playerAudioList.clear();
	}

	public void SetSongs(ArrayList<AudioClass> suras) {
		playerAudioList.clear();
		playerAudioList = suras;
	}

	public Boolean isVerseExist(int reciterId, int verseId) {
		for (int i = 0; i < playerAudioList.size(); i++) {
			if (playerAudioList.get(i).getReciterId() == reciterId
					&& playerAudioList.get(i).getVerseId() == verseId)
				return true;
		}
		return false;
	}

	public void SetaudioList(ArrayList<AudioClass> suras) {
		audioList.clear();
		audioList = suras;
	}

	public ArrayList<AudioClass> getPlayListSuras() {

		return audioList;
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
