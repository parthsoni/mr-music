package org.badger.mr.music.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.mult.daap.Contents;
import org.mult.daap.client.Song;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;



public class FileDownloader extends AsyncTask <Song, Integer, Integer>  {
	
	private String savePath;
	private Context parentContext;
	
	public FileDownloader(Context c){
		parentContext = c;
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
		savePath = mPrefs.getString("path_pref", parentContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());

	}
	

	@Override
	protected Integer doInBackground(Song... songs) {
		// TODO Auto-generated method stub
		for (Song s : songs) {
			String safeArtist = s.artist.replace('/', '_');
			String safeAlbum = s.album.replace("/", "_");
			String safeName = s.name.replace("/", "_");
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
		    	while ((len = songStream.read(buffer)) > 0) {
		    		destinationStream.write(buffer, 0, len);
		    	}
		    	if (songStream != null)
		    		songStream.close();
		    	if (destinationStream != null)
		    	{
		    		destinationStream.close();
		    	}
		    	
		    	destination.deleteOnExit();
		    	
		    } 
		    catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
		}
		//Refresh the Mediastore
		//Add the local song to the media list

		
		return null;
		
	}
}
