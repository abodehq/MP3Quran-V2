/**
 * Copyright (c) 2011 Mujtaba Hassanpur.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.quranmp3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;
import android.os.Message;

import com.quranmp3.model.DownloadClass;

/**
 * Downloads a file in a thread. Will send messages to the AndroidFileDownloader
 * activity to update the progress bar.
 */
public class DownloaderThread extends Thread {
	// constants
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;

	// instance variables
	private DownloadClass downloadClass;
	private String downloadUrl;
	private DownloadService parentActivity;

	/**
	 * Instantiates a new DownloaderThread object.
	 * 
	 * @param downloadService
	 * 
	 * @param parentActivity
	 *            Reference to AndroidFileDownloader activity.
	 * @param inUrl
	 *            String representing the URL of the file to be downloaded.
	 */
	public DownloaderThread(DownloadClass _downloadClass,
			DownloadService downloadService) {
		parentActivity = downloadService;
		downloadClass = _downloadClass;
		downloadUrl = downloadClass.getAudioClass().getAudioPath();
	}

	/**
	 * Connects to the URL of the file, begins the download, and notifies the
	 * AndroidFileDownloader activity of changes in state. Writes the file to
	 * the root of the SD card.
	 */
	@Override
	public void run() {
		URL url;
		URLConnection conn;
		int fileSize, lastSlash;
		String fileName;
		BufferedInputStream inStream;
		BufferedOutputStream outStream;
		File outFile;
		FileOutputStream fileStream;
		Message msg;

		// we're going to connect now
		// msg = Message.obtain(parentActivity.activityHandler,
		// AndroidFileDownloader.MESSAGE_CONNECTING_STARTED, 0, 0,
		// downloadUrl);
		// parentActivity.activityHandler.sendMessage(msg);

		try {
			url = new URL(downloadUrl);
			conn = url.openConnection();
			conn.setUseCaches(false);
			fileSize = conn.getContentLength();

			// get the filename
			lastSlash = url.toString().lastIndexOf('/');
			fileName = "file.bin";
			if (lastSlash >= 0) {
				fileName = url.toString().substring(lastSlash + 1);
			}
			if (fileName.equals("")) {
				fileName = "file.bin";
			}

			// notify download start
			int fileSizeInKB = fileSize / 1024;
			msg = Message.obtain(parentActivity.activityHandler,
					DownloadService.MESSAGE_DOWNLOAD_STARTED,
					downloadClass.getId(), 0, null);
			parentActivity.activityHandler.sendMessage(msg);

			downloadClass.setProgress(0);
			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/MP3Quran");
			boolean success = true;
			if (!folder.exists()) {
				success = folder.mkdir();
			}
			folder = new File(Environment.getExternalStorageDirectory()
					+ "/MP3Quran/"
					+ downloadClass.getAudioClass().getReciterId());

			if (!folder.exists()) {
				success = folder.mkdir();
			}

			String versesId = downloadClass.getAudioClass().getVerseId() + "";

			if (versesId.length() == 2)
				versesId = "0" + versesId;
			if (versesId.length() == 1)
				versesId = "00" + versesId;

			versesId = versesId + "_temp.mp3";
			// start download
			inStream = new BufferedInputStream(conn.getInputStream());
			outFile = new File("/sdcard/MP3Quran/"
					+ downloadClass.getAudioClass().getReciterId() + "/"
					+ versesId);
			fileStream = new FileOutputStream(outFile);
			outStream = new BufferedOutputStream(fileStream,
					DOWNLOAD_BUFFER_SIZE);
			byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
			int bytesRead = 0, totalRead = 0;
			while (!isInterrupted()
					&& (bytesRead = inStream.read(data, 0, data.length)) >= 0) {
				outStream.write(data, 0, bytesRead);

				// update progress bar
				totalRead += bytesRead;
				int totalReadInKB = totalRead / 1024;
				// msg = Message.obtain(parentActivity.activityHandler,
				// AndroidFileDownloader.MESSAGE_UPDATE_PROGRESS_BAR,
				// totalReadInKB, 0);
				// parentActivity.activityHandler.sendMessage(msg);

				downloadClass
						.setProgress((int) (((float) totalRead / (float) fileSize) * 100));
			}

			outStream.close();
			fileStream.close();
			inStream.close();

			if (isInterrupted()) {
				msg = Message.obtain(parentActivity.activityHandler,
						DownloadService.MESSAGE_DOWNLOAD_CANCELED,
						downloadClass.getId(), 0, null);
				parentActivity.activityHandler.sendMessage(msg);
				// the download was canceled, so let's delete the partially
				// downloaded file
				outFile.delete();
			} else {
				downloadClass.setProgress(101);

				File dir = new File(Environment.getExternalStorageDirectory()
						+ "/MP3Quran/"
						+ downloadClass.getAudioClass().getReciterId());
				versesId = downloadClass.getAudioClass().getVerseId() + "";

				if (versesId.length() == 2)
					versesId = "0" + versesId;
				if (versesId.length() == 1)
					versesId = "00" + versesId;
				String versesTo = versesId + ".mp3";
				versesId = versesId + "_temp.mp3";

				if (dir.exists()) {
					File from = new File(dir, versesId);
					File to = new File(dir, versesTo);
					if (from.exists())
						from.renameTo(to);
				}
				msg = Message.obtain(parentActivity.activityHandler,
						DownloadService.MESSAGE_DOWNLOAD_COMPLETE,
						downloadClass.getId(), 0, null);
				parentActivity.activityHandler.sendMessage(msg);
			}
		} catch (MalformedURLException e) {
			msg = Message.obtain(parentActivity.activityHandler,
					DownloadService.MESSAGE_ENCOUNTERED_ERROR,
					downloadClass.getId(), 0, null);
			parentActivity.activityHandler.sendMessage(msg);

		} catch (FileNotFoundException e) {
			msg = Message.obtain(parentActivity.activityHandler,
					DownloadService.MESSAGE_ENCOUNTERED_ERROR,
					downloadClass.getId(), 0, null);
			parentActivity.activityHandler.sendMessage(msg);

		} catch (Exception e) {
			msg = Message.obtain(parentActivity.activityHandler,
					DownloadService.MESSAGE_ENCOUNTERED_ERROR,
					downloadClass.getId(), 0, null);
			parentActivity.activityHandler.sendMessage(msg);

		}
	}

}