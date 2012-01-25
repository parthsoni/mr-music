package org.badger.mr.music.library;



import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import android.provider.MediaStore;
import android.util.Log;

public class Artist implements Comparable<Object>{
	public String name;
	public SortedMap<String,Album> albums;
	public int HasLocal;
	public int HasDaap;
	public boolean isAllArtists;

	public Artist(String n) {
		name = n;
		albums = new TreeMap<String,Album>();
		//Log.i("Artist","Creating Artist "+ name);
		//Log.i("Artist","Key: "+ getKey());
		isAllArtists = false;
		HasLocal = Library.HAS_NONE;
		HasDaap = Library.HAS_NONE;
		Library.addArtist(this);
		
	}
	
	public Artist() {
		name = "";
		//albums.putAll(Library.albums);
		HasLocal = Library.HAS_NONE;
		HasDaap = Library.HAS_NONE;
		isAllArtists = true;
	}
	
	public void addSong(Song s) {
		//Find the album and add to it
		//Log.i("Artist","Adding " + s.name + " to album " + s.album);
		Album a = getAlbum(s.album);
		if (a == null) {
			a = new Album(s.album);
			addAlbum(a);
		}
		if (s.isDaap)
			HasDaap = Library.HAS_SOME;
		if (s.isLocal)
			HasLocal = Library.HAS_SOME;
		a.addSong(s);
		
	}
	
	public boolean isSame(String othername) {
		return getKey().equals(Library.KeyFor(othername));
	}
	
	public SortedMap<String,Song> getSongs() {
		
		SortedMap<String,Song> songlist = new TreeMap<String,Song>();
		for (String albumkey  : albums.keySet()) {
			songlist.putAll(albums.get(albumkey).getSongs());
		}
		
		return songlist;
	}
	
	
	public SortedMap<String,Album> getAlbums() {
		return albums;
	}
	
	public void addAlbum(Album a) {
		albums.put(a.getKey(),a);
	}
	
	public Album getAlbum(String title) {
		
		return albums.get(Library.KeyFor(title));
	}
	
	public String toString(){
		return name;
	}
	
	public String getSortSection(){
		String section = name;
		section.replaceFirst("^((the\\s+)|(a\\s+)|(an\\s+))", "");
		return section.substring(0, 1).toUpperCase();
	}
	
	public String getKey() {
		return Library.KeyFor(this.name);
		/**
		String key = Library.KeyFor(this.name);
		if (key.length() == 0)
			key = toString().toLowerCase();
		return key;
		**/
	}
	
	public int compareTo(Object another) {
		int ret;
		
		if (another instanceof Artist) {
			ret = this.getKey().compareTo(((Artist) another).getKey());
	
		} else if (another instanceof String) {
			ret = this.getKey().compareTo(Library.KeyFor((String) another));
		}
		else
			ret = 0;
		
		
		return ret;
	}
	
}
