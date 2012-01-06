package org.badger.mr.music.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.badger.mr.music.R;
import org.badger.mr.music.download.DownloadSong;
import org.mult.daap.Contents;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;



public class FileDownloader extends AsyncTask <Void, Integer, Integer>  {
	
	private String savePath;
	private Context parentContext;
	//private DownloadSong s;
	public int status;
	public static int STATUS_IDLE = 0;
	public static int STATUS_RUNNING = 1;
	private ProgressBar progressbar;
	private TextView activedownload;
	private String activetitle;
	
	public FileDownloader(Context c, ProgressBar pg, TextView ad){
		status = STATUS_IDLE;
		parentContext = c;
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
		savePath = mPrefs.getString("path_pref", parentContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());
		progressbar = pg;
		activedownload = ad;
		
		
	}
	

	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		DownloadSong s;
		Log.i("FileDownloader","Starting File Downloader");
		int i = getNext();
		status = STATUS_RUNNING;
		
		
		while (i >= 0) {
			s = Contents.downloadList.get(i);
			//progressbar.setProgress(0);
			//activedownload.setText(s.toString());
			activetitle = s.toString();
			publishProgress(0);
			if (s.isLocal) {
				s.status = DownloadSong.STATUS_FILELOCAL;
				Log.i("FileDownloader", s.name + " is already local");
				publishProgress(100);
			}
			else {
				String safeArtist = s.artist.replace('/', '_');
				String safeAlbum = s.album.replace("/", "_");
				String safeName = s.name.replace("/", "_");
				s.status = DownloadSong.STATUS_INPROGRESS;
				try {
			    	File directory = new File(savePath, safeArtist + "/" + safeAlbum);
			    	directory.mkdirs();
			    	File destination = new File(directory, s.track + "-" + safeName
			    			+ "." + s.format);
			    	InputStream songStream = Contents.daapHost
						.getSongStream(s);
			    	FileOutputStream destinationStream = new FileOutputStream(
			    			destination);
			    	Log.i("FileDownloader","Saving " + destination.toString());
			    	Log.i("FileDownloader","Filesize: " + s.size);
			    	byte[] buffer = new byte[1024];
			    	int len;
			    	int bytesDl = 0;
			    	while ((len = songStream.read(buffer)) > 0) {
			    		destinationStream.write(buffer, 0, len);
			    		bytesDl += len;
			    		publishProgress((int) (bytesDl*100)/s.size);
			    		//Log.i("FileDownloader","Downloaded Pct: " + (int) (bytesDl*100)/s.size );
			    	}
			    	if (songStream != null)
			    		songStream.close();
			    	if (destinationStream != null)
			    	{
			    		destinationStream.close();
			    	}
			    	Log.i("FileDownloader","Saved " + bytesDl + "bytes");
			    	destination.deleteOnExit();
			    	s.status = DownloadSong.STATUS_COMPLETE;
			    	//Refresh the Mediastore
					//Add the local song to the media list
	
			    } 
			    catch (Exception e) {
			    	s.status = DownloadSong.STATUS_ERROR;
			    	e.printStackTrace();
			    }
				
			}
			i = getNext();
			
		}
		status = STATUS_IDLE;
		return null;
		
	}

@Override
	protected void onProgressUpdate(Integer... value) {
	   // TODO Auto-generated method stub
	   progressbar.setProgress(value[0]);
	   activedownload.setText(activetitle);
	  // Log.i("FileDownloader","Progress:" + value[0]);
	  }
	
	@SuppressWarnings("null")
	private int getNext(){
		int retVal = -1;
		for (int i = 0; i < Contents.downloadList.size(); i = i + 1) { // Test and Loop
			   if (Contents.downloadList.get(i).status == DownloadSong.STATUS_NOTSTARTED) {
				   retVal = i;
				   Log.i("DownloadSongs","Next Available Song: " + Contents.downloadList.get(i).name + "Index " + i);
				   break;
			   }
			
		}
		return retVal;
				
	}

}
