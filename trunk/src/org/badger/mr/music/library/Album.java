package org.badger.mr.music.library;

import java.util.ArrayList;
import android.provider.MediaStore;
import android.util.Log;

public class Album implements Comparable<Object> {
	public String name;
	public ArrayList<Song> songs;
	
	public Album(String n) {
		name = n;
		songs = new ArrayList<Song>();
		Library.addAlbum(this);
		Log.i("Album","Created Album " + name);
	}
	
	public void addSong(Song s) {
		Song existing = getSong(s);
		if (existing == null) {
			Log.i("Album","Adding " + s.name);
			songs.add(s);
			//Library.songs.add(s);
		}
		else {
			Log.i("Album","Merging " + s.name);
			existing.mergeSong(s);
		}
			
			
	}
	
	public Song getSong(Song s) {
		Song ret = null;
		for (Song song: songs) {
			if (song.isSame(s)) {
				ret = song;
				break;
			}
		}
		return ret;
	}
	
	
	public ArrayList<Song> getSongs() {
		return songs;
	}
	
	public ArrayList<Song> getSongs(Artist artist) {
		ArrayList<Song> songlist = new ArrayList<Song>();
		for (Song s : songs) {
			if (artist.isSame(s.artist))
					songlist.add(s);
		}
		return songlist;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean isSame(String othertitle) {
		return getKey().equals(MediaStore.Audio.keyFor(othertitle));
	}
	
	public String getKey() {
		return MediaStore.Audio.keyFor(this.name);
	}
	
	public int compareTo(Object another) {
		int ret;
		
		if (another instanceof Album) {
			ret = this.getKey().compareTo(((Album) another).getKey());
	
		} else if (another instanceof String) {
			ret = this.getKey().compareTo(MediaStore.Audio.keyFor((String) another));
		}
		else
			ret = 0;
		
		
		return ret;
	}
}
