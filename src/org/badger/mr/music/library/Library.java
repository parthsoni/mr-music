package org.badger.mr.music.library;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
//import java.util.LinkedHashMap;
import java.util.Random;

import org.badger.mr.music.AlbumComparator;
import org.badger.mr.music.ArtistComparator;
import org.badger.mr.music.SongArtistAlbumTrackComparator;
import org.badger.mr.music.download.DownloadSong;
import org.badger.mr.music.download.FileDownloader;
import org.mult.daap.background.GetSongsForPlaylist;
import org.mult.daap.background.LoginManager;
import org.mult.daap.background.SearchThread;
import org.mult.daap.client.daap.DaapHost;

import android.provider.MediaStore;
import android.util.Log;

public class Library {
	public static SortedMap<String,Song> songs = new TreeMap<String,Song>();
	public static SortedMap<String,Artist> artists = new TreeMap<String,Artist>();
	public static SortedMap<String,Album> albums = new TreeMap<String,Album>();
	public static SortedMap<String,Album> filteredAlbums = new TreeMap<String,Album>();
	public static SortedMap<String,Song> filteredSongs = new TreeMap<String,Song>();
	public static ArrayList<Song> playQueue = new ArrayList<Song>();
	public static ArrayList<DownloadSong> downloadList = new ArrayList<DownloadSong>();
	public static String artistFilter = "";
	public static String albumFilter = "";
	public static int playposition;
	public static int HAS_SOME = 0;
	public static int HAS_NONE = 1;
	public static int HAS_ALL = 2;
	
	
	public static InetAddress address;
	
	public static DaapHost daapHost;
	public static GetSongsForPlaylist getSongsForPlaylist = null;
	public static SearchThread searchResult;
    
	
    public static final int SORT_SONG_TRACK = 2;
	public static final int SORT_SONG_TITLE = 3;
	public static final int SORT_SONG_ALBUM = 1;
	public static int songSortType;
	public static org.badger.mr.music.download.DownloadBrowser.FileDownloader downloader;
	public static LoginManager loginManager;
	
	public static boolean shuffle = false;
    public static boolean repeat = false;
	public static int playlist_position;
	
	public static void setFilters() {
		filteredSongs.clear();
		filteredAlbums.clear();
		Log.i("Library","Setting Filters");
		if (artistFilter.length() + albumFilter.length() == 0) {
			Log.i("Library","   Unfiltered ");
			filteredAlbums.putAll(albums);
			filteredSongs.putAll(songs);
			//If we have no artist filter and no album filter, then sort by song title
			songSortType = SORT_SONG_TITLE;
		}
		else if ((artistFilter.length() > 0) && (albumFilter.length() == 0)) {
			Artist filterArtist = getArtist(artistFilter);
			Log.i("Library","   Artist Filter: " + filterArtist);
			//if (filterArtist != null) {
				filteredSongs.putAll(filterArtist.getSongs());
				filteredAlbums.putAll(filterArtist.getAlbums());
				//If we have an artist filter and no album filter then sort by album then track
				songSortType = SORT_SONG_ALBUM;
				/*AlbumComparator albc = new AlbumComparator();
		        Collections.sort(filteredAlbums,albc);
			    SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);*/
			//}
		}
		else if ((albumFilter.length() > 0) && (artistFilter.length() == 0)) {
			Album filterAlbum = getAlbum(albumFilter);
			Log.i("Library","   Album filter: " + filterAlbum);
			//if (filterAlbum != null) {
				filteredAlbums.putAll(albums);
				filteredSongs.putAll(filterAlbum.getSongs());
				//If we have no artist filter and an album filter then sort by song
				songSortType = SORT_SONG_TRACK;
                /**SongComparator snc = new SongComparator();
		        Collections.sort(filteredSongs,snc);**/
			//}
		}
		else {
			Album filterAlbum = getAlbum(albumFilter);
			Artist filterArtist = getArtist(artistFilter);
			Log.i("Library","   Filter Artist " + filterArtist + " Filter album: " + filterAlbum );
			//If we have an artist filter and an album filter then sort by song
			songSortType = SORT_SONG_TRACK;
			//if (filterAlbum != null) {
			//	filteredAlbums.add(filterAlbum);
			//	if (filterArtist != null) {
			filteredAlbums.putAll(filterArtist.getAlbums());
			filteredSongs.putAll(filterAlbum.getSongs(filterArtist));
			Log.i("Library","   Songs: " + filteredSongs.size());
			/**SongComparator snc = new SongComparator();
			 Collections.sort(filteredSongs,snc);**/
			//	}
			//}
		}
		//Log.i("Library","Sorting Lists");
		//albumBrowseList = getAlbumList(Library.filteredAlbums);
		//AlbumComparator albc = new AlbumComparator();
        //Collections.sort(albumBrowseList,albc);
        //songBrowseList = Library.getSongsList(Library.filteredSongs);
		//SongComparator snc = new SongComparator();
        //Collections.sort(songBrowseList,snc);
        //Log.i("Library","Finished Sorting");
	}
	
	public static void sortLists() {
	/**	 	
        ArtistComparator artc = new ArtistComparator();
        Collections.sort(artistBrowseList,artc);
        
        AlbumComparator albc = new AlbumComparator();
        Collections.sort(albums,albc);
	
        SongComparator snc = new SongComparator();
        Collections.sort(songs,snc);**/
    }
	
	public static ArrayList<Song> getSongsList(SortedMap<String,Song> songlist){
		return new ArrayList<Song>(songlist.values());
	}
	
	public static ArrayList<Artist> getArtistList(SortedMap<String,Artist> artistlist) {
		return new ArrayList<Artist>(artistlist.values());
	}
	
	public static ArrayList<Album> getAlbumList(SortedMap<String,Album> albumlist) {
		return new ArrayList<Album>(albumlist.values());
	}
	
	
	public static void setPlayQueue(ArrayList<Song> playList) {
		/**playQueue.clear();
		playQueue.addAll(playList);
		setSongPosition(0);**/
		setPlayQueue(playList,0);
	}
	
	public static void setPlayQueue(ArrayList<Song> playList, int pos) {
		playQueue.clear();
		playQueue.addAll(playList);
		setSongPosition(pos);
	}
	
	public static void addToDownloadQueue(ArrayList<Song> dlList) {
		for (Song s :dlList) {
			addToDownloadQueue(s);
    		//downloadList.add(DownloadSong.toDownloadSong(s));
    	}
	}
	public static void addToDownloadQueue(Song dlsong) {
		downloadList.add(DownloadSong.toDownloadSong(dlsong));
	}
	
	public static void setSongPosition(int pos) {
		playposition = pos;
	}
	
	public static void addSong(Song s) {
		//Log.i("Library","Adding Song" + s.toString());
		Artist artist = getArtist(s.artist);
		if (artist == null) {
			artist = new Artist(s.artist);
		}
		artist.addSong(s);

	}
	
	public static void addArtist(Artist a)
	{
		String artistKey = a.getKey();
		if (!artists.containsKey(artistKey))
			artists.put(artistKey, a);
	}
	
	public static void addAlbum(Album a)
	{
		String albumKey = a.getKey();
		if (!albums.containsKey(albumKey))
			albums.put(albumKey, a);
	}
	
	public static Artist getArtist(String name) {
		return artists.get(KeyFor(name));
	}
	
	public static Album getAlbum(String name)
	{
		return albums.get(KeyFor(name));
	}
	
	public static Song getSong(Song s) {
		/**Song ret = null;
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
		return ret**/
		return songs.get(s.getHashKey());
	}
	
	public static String KeyFor(String s) {
		String key = MediaStore.Audio.keyFor(s);
		if (key.length() == 0)
			key = s.toLowerCase();
		return key;
	}
	
	public static Song getPlayerSong() throws IndexOutOfBoundsException {
        Song song;
        // Not the queue
        if (playQueue.size() > 0 && playposition < playQueue.size()
                && playposition >= 0) {
            song = playQueue.get(playposition);
            return song;
        }
        else {
            throw new IndexOutOfBoundsException("End of list");
        }
    }
	
	public static Song getPreviousSong() {
		playposition--;
        return getPlayerSong();
    }
	
	 public static Song getNextSong() throws IndexOutOfBoundsException {
		 playposition++;
	        return getPlayerSong();
	    }
	
	public static Song getRandomSong() throws IndexOutOfBoundsException {
		playposition = new Random(System.currentTimeMillis()).nextInt(playQueue
                .size());
        return getPlayerSong();
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
