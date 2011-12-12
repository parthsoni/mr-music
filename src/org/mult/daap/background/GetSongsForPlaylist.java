package org.mult.daap.background;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import org.badger.mr.music.local.LocalPlaylist;

import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
import org.mult.daap.PlaylistBrowser;
import org.mult.daap.client.Song;
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

    public GetSongsForPlaylist(DaapPlaylist playList) {
        this.playList = playList;
        this.lastMessage = INITIALIZED;
    }
    public GetSongsForPlaylist()
    {
    	this.playList = null;
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
    	
    	if (songs.size() == 0) {
            notifyAndSet(EMPTY);
            return;
        }
        for (Song song : songs) {
            if (Contents.ArtistElements.containsKey(song.artist)) {
                Contents.ArtistElements.get(song.artist).add(song.id);
            }
            else {
                ArrayList<Integer> t = new ArrayList<Integer>();
                t.add(song.id);
                Contents.ArtistElements.put(song.artist, t);
            }
            if (Contents.AlbumElements.containsKey(song.album)) {
                Contents.AlbumElements.get(song.album).add(song.id);
            }
            else {
                ArrayList<Integer> t = new ArrayList<Integer>();
                t.add(song.id);
                Contents.AlbumElements.put(song.album, t);
            }
            Contents.songListAdd(song);
        }
        Contents.sortLists();
        notifyAndSet(FINISHED);
    
    }
    
    
    public void run() {
    	
        MediaPlayback.clearState();
        Contents.clearLists();
        ArrayList<Song> rawList = new ArrayList<Song>();
        try {
        	//Build Local Playlist
        	Log.i("GSFP","Building Local Playlist");
        	localList = new LocalPlaylist(activityContext);
        	rawList = localList.getSongs();
        	Log.i("GSFP","Playlist size: " + rawList.size());
        	if (playList != null) {
        		
	        	//Build Daap Playlist
	        	Log.i("GSFP","Building Daap Playlist");
	            if (playList.all_songs == true)
	            	rawList.addAll(Contents.daapHost.getSongs());
	            	//Collections.copy(rawList, Contents.daapHost.getSongs());
	            else
	            	rawList.addAll((ArrayList<Song>) playList.getSongs());
	            	//Collections.copy(rawList,(ArrayList<Song>) playList.getSongs());
	            Log.i("GSFP","Playlist size: " + rawList.size());
	        }
        	
            processContents(rawList);	
                            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
