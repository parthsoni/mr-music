package org.badger.mr.music.library;



import java.util.ArrayList;

import android.provider.MediaStore;
import android.util.Log;

public class Artist implements Comparable<Object>{
	public String name;
	public ArrayList<Album> albums;

	public Artist(String n) {
		name = n;
		albums = new ArrayList<Album>();
		Library.addArtist(this);
		Log.i("Artist","Creating Artist "+ name);
	}
	
	public void addSong(Song s) {
		//Find the album and add to it
		Log.i("Artist","Adding " + s.name + " to album " + s.album);
		Album a = getAlbum(s.album);
		if (a == null) {
			a = new Album(s.album);
			addAlbum(a);
		}
		a.addSong(s);
		
	}
	
	public boolean isSame(String othername) {
		return getKey().equals(MediaStore.Audio.keyFor(othername));
	}
	
	public ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<Song>();
		for (Album a: albums) {
			songs.addAll(a.getSongs(this));
		}
		return songs;
	}
	
	
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	public void addAlbum(Album a) {
		albums.add(a);
	}
	
	public Album getAlbum(String title) {
		Album ret = null;
		for (Album a : albums) {
			if (a.isSame(title)) {
				ret = a;
				break;
			}
		}
		return ret;
	}
	
	public String toString(){
		return name;
	}
	
	public String getKey() {
		return MediaStore.Audio.keyFor(this.name);
	}
	
	public int compareTo(Object another) {
		int ret;
		
		if (another instanceof Artist) {
			ret = this.getKey().compareTo(((Artist) another).getKey());
	
		} else if (another instanceof String) {
			ret = this.getKey().compareTo(MediaStore.Audio.keyFor((String) another));
		}
		else
			ret = 0;
		
		
		return ret;
	}
	
}
