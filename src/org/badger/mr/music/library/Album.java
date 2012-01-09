package org.badger.mr.music.library;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.provider.MediaStore;
import android.util.Log;

public class Album implements Comparable<Object> {
	public String name;
	public LinkedHashMap<String,Song> songs;
	public ArrayList<String> artistList;
	public int HasLocal;
	public int HasDaap;
	
	public Album(String n) {
		name = n;
		songs = new LinkedHashMap<String,Song>();
		Library.addAlbum(this);
		artistList = new ArrayList<String>();
		Log.i("Album","Created Album " + name);
		HasLocal = Library.HAS_NONE;
		HasDaap = Library.HAS_NONE;
	}
	
	public void addSong(Song s) {
		Song existing = getSong(s);
		if (existing == null) {
			Log.i("Album","Adding " + s.name);
			songs.put(s.getHashKey(),s);
			Library.songs.put(s.getHashKey(),s);
		}
		else {
			Log.i("Album","Merging " + s.name);
			existing.mergeSong(s);
		}
		if (!artistList.contains(s.artist))
			artistList.add(s.artist);
		if (s.isDaap)
			HasDaap = Library.HAS_SOME;
		if (s.isLocal)
			HasLocal = Library.HAS_SOME;
			
			
	}
	
	
	public Song getSong(Song s) {
		return songs.get(s.getHashKey());
	}
	
	
	public LinkedHashMap<String,Song> getSongs() {
		return songs;
	}
	
	public LinkedHashMap<String,Song> getSongs(Artist artist) {
		
		LinkedHashMap<String,Song> songlist = new LinkedHashMap<String,Song>();
		for (String songkey  : songs.keySet()) {
			if (artist.isSame(songs.get(songkey).artist))
					songlist.put(songkey,songs.get(songkey));
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
		String key = MediaStore.Audio.keyFor(this.name);
		if (key.length() == 0)
			key = toString().toLowerCase();
		return key;
		
	}
	
	public String getSortSection(){
		String section = name;
		section.replaceFirst("^((the\\s+)|(a\\s+)|(an\\s+))", "");
		return section.substring(0, 1).toUpperCase();
	}
	
	public String getArtists() {
		String artists = "";
		for (String a: artistList) {
			if (a.length() > 1) {
				artists = artists + ", " + a;
			}
			else
				artists = a;
		}
		return artists;
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
