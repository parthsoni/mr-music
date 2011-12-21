package org.badger.mr.music.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.badger.mr.music.download.DownloadSong;
import org.mult.daap.Contents;
import org.mult.daap.client.Song;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;



public class FileDownloader extends AsyncTask <Void, Integer, Integer>  {
	
	private String savePath;
	private Context parentContext;
	
	public FileDownloader(Context c){
		parentContext = c;
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
		savePath = mPrefs.getString("path_pref", parentContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());

	}
	

	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		DownloadSong s = getNext();
		
		while (s != null) {
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
		    	byte[] buffer = new byte[1024];
		    	int len;
		    	int bytesDl = 0;
		    	while ((len = songStream.read(buffer)) > 0) {
		    		destinationStream.write(buffer, 0, len);
		    		bytesDl += len;
		    		publishProgress(bytesDl);
		    	}
		    	if (songStream != null)
		    		songStream.close();
		    	if (destinationStream != null)
		    	{
		    		destinationStream.close();
		    	}
		    	
		    	destination.deleteOnExit();
		    	s.status = DownloadSong.STATUS_COMPLETE;
		    	//Refresh the Mediastore
				//Add the local song to the media list

		    } 
		    catch (Exception e) {
		    	s.status = DownloadSong.STATUS_COMPLETE;
		    	e.printStackTrace();
		    }
		}
		
		return null;
		
	}
	
	private DownloadSong getNext(){
	
		DownloadSong nextSong = null;
		for (DownloadSong dls : Contents.downloadList) {
			if (dls.status == DownloadSong.STATUS_NOTSTARTED)
				nextSong = dls;
			break;
		}
		return nextSong;
		
	}

}
