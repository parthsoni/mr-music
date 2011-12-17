package org.mult.daap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.Context;



import org.mult.daap.client.Song;
import java.util.ArrayList;

public class FileUtils {
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    static void deleteIfExists(File file) {
        if (file != null) {
            if (file.exists() == true) {
                file.delete();
            }
        }
    }
    public static class FileCopier implements Runnable {
    	
    	private ArrayList<Song> songlist = new ArrayList<Song>(); 
    	private String savePath;
    	public boolean enableNotification;
    	private Context parentContext;
    	
    	
    	public FileCopier(Song songToCopy, Context c){
    		songlist.add(songToCopy);
    		parentContext = c;
    		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
    		savePath = mPrefs.getString("path_pref", parentContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());
    		enableNotification = false;
    		
    	}
    	
    	public FileCopier(ArrayList<Song> listToCopy, Context c) {
    		songlist = listToCopy;
    		parentContext = c;
    		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
    		savePath = mPrefs.getString("path_pref", Environment.getExternalStorageDirectory() + "/DAAP/");
    		enableNotification = false;
    		
    	}
    	

    	
    	public void run() {
    			if (Environment.getExternalStorageState().equals(
    					Environment.MEDIA_MOUNTED)) {
    				for (Song s : songlist) {
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
    				    		destinationStream.close();
    				    	destination.deleteOnExit();
    				    } 
    				    catch (Exception e) {
    				    	e.printStackTrace();
    				    }
    				    
    				}
    				
    			}
    			
    	}

    }
}

