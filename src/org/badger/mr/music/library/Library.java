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
		Log.i("Library","Setting Filters");
		if (artistFilter.length() + albumFilter.length() == 0) {
			Log.i("Library","   Unfiltered ");
			filteredAlbums.addAll(albums);
			filteredSongs.addAll(getAllSongs());
		}
		else if (artistFilter.length() > 0) {
			Artist filterArtist = getArtist(artistFilter);
			Log.i("Library","   Artist Filter: " + filterArtist);
			//if (filterArtist != null) {
				filteredSongs.addAll(filterArtist.getSongs());
				filteredAlbums.addAll(filterArtist.getAlbums());
				Log.i("Library","   Albums: " + filteredAlbums.size());
				Log.i("Library","   Songs: " + filteredSongs.size());
				AlbumComparator albc = new AlbumComparator();
		        Collections.sort(filteredAlbums,albc);
			    SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);
			//}
		}
		else if (albumFilter.length() > 0) {
			Album filterAlbum = getAlbum(albumFilter);
			Log.i("Library","   Album filter: " + filterAlbum);
			//if (filterAlbum != null) {
				filteredAlbums.addAll(albums);
				filteredSongs.addAll(filterAlbum.getSongs());
				Log.i("Library","   Songs: " + filteredSongs.size());
				SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);
			//}
		}
		else {
			Album filterAlbum = getAlbum(albumFilter);
			Artist filterArtist = getArtist(artistFilter);
			Log.i("Library","   Filter Artist " + filterArtist + " Filter album: " + filterAlbum );
			
			//if (filterAlbum != null) {
				filteredAlbums.add(filterAlbum);
			//	if (filterArtist != null) {
					filteredSongs.addAll(filterAlbum.getSongs(filterArtist));
					Log.i("Library","   Songs: " + filteredSongs.size());
					SongComparator snc = new SongComparator();
			        Collections.sort(filteredSongs,snc);
			//	}
			//}
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
			//songs.add(s);
		}
	}
	
	public static ArrayList<Song> getAllSongs() {
		ArrayList<Song> slist = new ArrayList<Song>();
		for (Album a : albums) {
			slist.addAll(a.getSongs());
		}
		return slist;
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
		Artist findartist = getArtist(s.artist);
		if (findartist == null) {
			return ret;
		}
		Album findalbum = getAlbum(s.album);
		if (findalbum == null) {
			return ret;
		}
		for (Song song: findalbum.getSongs(findartist)) {
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
