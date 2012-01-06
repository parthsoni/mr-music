package org.badger.mr.music.library;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.badger.mr.music.AlbumComparator;
import org.badger.mr.music.ArtistComparator;
import org.badger.mr.music.SongComparator;
import org.badger.mr.music.download.DownloadSong;
import org.mult.daap.background.GetSongsForPlaylist;
import org.mult.daap.client.SongNameComparator;
import org.mult.daap.client.StringIgnoreCaseComparator;
import org.mult.daap.client.daap.DaapHost;

import android.util.Log;

public class Library {
	public static ArrayList<Song> songs = new ArrayList<Song>();
	public static ArrayList<Artist> artists = new ArrayList<Artist>();
	public static ArrayList<Album> albums = new ArrayList<Album>();
	public static ArrayList<Album> filteredAlbums = new ArrayList<Album>();
	public static ArrayList<Song> filteredSongs = new ArrayList<Song>();
	public static ArrayList<Song> playQueue = new ArrayList<Song>();
	public static ArrayList<DownloadSong> downloadList = new ArrayList<DownloadSong>();
	public static String artistFilter = "";
	public static String albumFilter = "";
	public static int playposition;
	
	public static InetAddress address;
	
	public static DaapHost daapHost;
	public static GetSongsForPlaylist getSongsForPlaylist = null;

	
	public static void setFilters() {
		filteredSongs.clear();
		filteredAlbums.clear();
		if (artistFilter.length() + albumFilter.length() == 0) {
			filteredAlbums.addAll(albums);
			filteredSongs.addAll(songs);
		}
		else if (albumFilter.length() == 0) {
			Artist filterArtist = getArtist(artistFilter);
			if (filterArtist != null) {
				filteredSongs.addAll(filterArtist.getSongs());
				filteredAlbums.addAll(filterArtist.getAlbums());
				AlbumComparator albc = new AlbumComparator();
		        Collections.sort(filteredAlbums,albc);
			    SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);
			}
		}
		else if (artistFilter.length() == 0) {
			Album filterAlbum = getAlbum(albumFilter);
			if (filterAlbum != null) {
				filteredAlbums.add(filterAlbum);
				filteredSongs.addAll(filterAlbum.getSongs());
			    SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);
			}
		}
		else {
			Album filterAlbum = getAlbum(albumFilter);
			Artist filterArtist = getArtist(artistFilter);
			if (filterAlbum != null) {
				filteredAlbums.add(filterAlbum);
				if (filterArtist != null) {
					filteredSongs.addAll(filterAlbum.getSongs(filterArtist));
				    SongComparator snc = new SongComparator();
			        Collections.sort(filteredSongs,snc);
				}
			}
		}
	}
	
	public static void sortLists() {
		 	
        ArtistComparator artc = new ArtistComparator();
        Collections.sort(artists,artc);
        
        AlbumComparator albc = new AlbumComparator();
        Collections.sort(albums,albc);
		
        
        SongComparator snc = new SongComparator();
        Collections.sort(songs,snc);
    }
	
	
	
	public static void setPlayQueue() {
		playQueue.clear();
		playQueue.addAll(filteredSongs);
		setSongPosition(0);
	}
	
	public static void addToDownloadQueue(ArrayList<Song> dlList) {
		for (Song s :dlList) {
    		downloadList.add(DownloadSong.toDownloadSong(s));
    	}
	}
	
	public static void setSongPosition(int pos) {
		playposition = pos;
	}
	
	public static void addSong(Song s) {
		Log.i("Library","Adding Song" + s.toString());
		Song existing = getSong(s);
		if (existing == null) {
			Artist artist = getArtist(s.artist);
			if (artist == null) {
				artist = new Artist(s.artist);
			}
			artist.addSong(s);
			songs.add(s);
		}
	}
	
	public static void addArtist(Artist a)
	{
		Artist existing = getArtist(a.name);
		if (existing == null)
			artists.add(a);
	}
	
	public static void addAlbum(Album a)
	{
		Album existing = getAlbum(a.name);
		if (existing == null)
			albums.add(a);
	}
	
	public static Artist getArtist(String name) {
		Artist ret = null;
		for (Artist artist: artists) {
			if (artist.isSame(name)) {
				ret = artist;
				break;
			}
		}
		return ret;
	}
	
	public static Album getAlbum(String name)
	{
		Album ret = null;
		for (Album album: albums) {
			if (album.isSame(name)) {
				ret = album;
				break;
			}
		}
		return ret;
	}
	
	public static Song getSong(Song s) {
		Song ret = null;
		for (Song song: songs) {
			if (song.isSame(s)) {
				ret = song;
				break;
			}
		}
		return ret;
	}
	
	public static void clearLists() {
		artistFilter = "";
		albumFilter = "";
        songs.clear();
        artists.clear();
        albums.clear();
        filteredAlbums.clear();
        filteredSongs.clear();
        downloadList.clear();
        playQueue.clear();
        
    }
}
