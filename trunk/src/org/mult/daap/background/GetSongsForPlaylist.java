package org.mult.daap.background;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import org.badger.mr.music.library.Library;
import org.badger.mr.music.library.LocalPlaylist;
import org.badger.mr.music.library.Song;

//import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
//import org.mult.daap.PlaylistBrowser;
//import org.mult.daap.client.Song;
import org.mult.daap.client.daap.DaapPlaylist;

import android.content.Context;
import android.util.Log;

public class GetSongsForPlaylist extends Observable implements Runnable {
    private DaapPlaylist playList;
    private LocalPlaylist localList;
    private int lastMessage;
    public Context activityContext;
    public static int FINISHED = 21;
    public static int INITIALIZED = 22;
    public static int EMPTY = 23;
    public static int START = 24;
    public static int MERGING = 25;
    public static int SORTING = 26;

    public GetSongsForPlaylist(DaapPlaylist playList) {
        this.playList = playList;
        this.lastMessage = INITIALIZED;
    }
    public GetSongsForPlaylist()
    {
    	this.playList = null;
    	this.lastMessage = INITIALIZED;
    }

    public int getLastMessage() {
        return lastMessage;
    }

    private void notifyAndSet(int message) {
        lastMessage = message;
        setChanged();
        notifyObservers(message);
    }

    public void processContents(ArrayList<Song> songs){
    	Log.i("GetSongsForPlaylist","Processing Playlist. Raw Song Count: "+ songs.size());
    	notifyAndSet(MERGING);
    	if (songs.size() == 0) {
            notifyAndSet(EMPTY);
            return;
        }
        for (Song song : songs) {
        	Library.addSong(song);
        }
        Log.i("GetSongsForPlaylist","Songs: " + Library.songs.size());
        Log.i("GetSongsForPlaylist","Albums: " + Library.songs.size());
        Log.i("GetSongsForPlaylist","Artists: " + Library.songs.size());
        Library.setFilters();
        notifyAndSet(SORTING);
        
        Library.artistBrowseList = Library.getArtistList(Library.artists);
        Library.sortLists();
        notifyAndSet(FINISHED);
    
    }
    
    
    public void run() {
    	
        MediaPlayback.clearState();
        Library.clearLists();
        ArrayList<Song> rawList = new ArrayList<Song>();
        try {
        	//Build Local Playlist
        	Log.i("GSFP","Building Local Playlist");
        	localList = new LocalPlaylist(activityContext);
        	rawList = localList.getSongs();
        	Log.i("GSFP","Local Playlist size: " + rawList.size());
        	if (playList != null) {
        		
	        	//Build Daap Playlist
	        	Log.i("GSFP","Building Daap Playlist");
	            if (playList.all_songs == true)
	            	rawList.addAll(Library.daapHost.getSongs());
	            	//Collections.copy(rawList, Contents.daapHost.getSongs());
	            else
	            	rawList.addAll((ArrayList<Song>) playList.getSongs());
	            	//Collections.copy(rawList,(ArrayList<Song>) playList.getSongs());
	            Log.i("GSFP","Daap Playlist size: " + rawList.size());
	        }
        	else
    			Library.address = InetAddress.getLocalHost();

        	
            processContents(rawList);	
                            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
