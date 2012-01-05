package org.mult.daap;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

import org.badger.mr.music.download.DownloadSong;
import org.badger.mr.music.local.FileDownloader;
import org.mult.daap.background.GetSongsForPlaylist;
import org.mult.daap.background.LoginManager;
import org.mult.daap.background.SearchThread;
import org.mult.daap.client.Song;
import org.mult.daap.client.SongNameComparator;
import org.mult.daap.client.StringIgnoreCaseComparator;
import org.mult.daap.client.daap.DaapHost;

import android.util.Log;

public class Contents {
    public static ArrayList<Song> songList = new ArrayList<Song>();
    public static ArrayList<Song> filteredAlbumSongList = new ArrayList<Song>();
    public static ArrayList<Song> filteredArtistSongList = new ArrayList<Song>();
    public static ArrayList<Song> queue = new ArrayList<Song>(10);
    public static ArrayList<Song> activeList = new ArrayList<Song>();
    public static ArrayList<DownloadSong> downloadList = new ArrayList<DownloadSong>();
    public static ArrayList<String> stringElements = new ArrayList<String>();
    public static ArrayList<String> artistNameList = new ArrayList<String>();
    public static ArrayList<String> albumNameList = new ArrayList<String>();
    public static ArrayList<String> filteredAlbumNameList = new ArrayList<String>();
    //public static ArrayList<String> artistAlbumNameList = new ArrayList<String>();
    public static TreeMap<String, ArrayList<Integer>> ArtistElements = new TreeMap<String, ArrayList<Integer>>();
    public static TreeMap<String, ArrayList<Integer>> AlbumElements = new TreeMap<String, ArrayList<Integer>>();
    public static TreeMap<String, ArrayList<Integer>> ArtistAlbumElements = new TreeMap<String, ArrayList<Integer>>();
    public static DaapHost daapHost;
    public static GetSongsForPlaylist getSongsForPlaylist = null;
    public static InetAddress address;
    public static LoginManager loginManager;
    public static SearchThread searchResult;
    public static short playlist_position = -1;
    public static boolean shuffle = false;
    public static boolean repeat = false;
    public static boolean lastUsedAlbumActivity = false;
    public static String artistFilter = "";
    public static String albumFilter = "";
    private static int position = 0;
    public static FileDownloader downloader;

    public static void songListAdd(Song s) {
    	//Need to Prevent Dupes Somehow
    	if (Contents.stringElements.contains(s.toString())) {
    		//This is a dupe
    		//Log.i("Contents","Adding Duplicate song: " + s.toString());
    		if (s.isLocal) {
    			//Keep the local copy
    			//Find the dupe and replace it with this one
    			for (Song song : songList) {
    	           if ((s.name == song.name) && (s.artist == song.artist) && (s.album == song.album)) {
    	        	   //This is the one. Overwrite it.
    	        	   //don't overwrite the ID -- it's used by the artist and album trees
    	        	   song.name = s.name;
    	        	   song.disc_num = s.disc_num;
    	        	   song.localPath = s.localPath;
    	        	   song.isLocal = s.isLocal;
    	        	   song.format = s.format;
    	        	   song.genre = s.genre;
    	        	   song.host = s.host;
    	        	   song.size = s.size;
    	        	   song.time = s.time;
    	        	   song.track = s.track;
    	           }
    	        }
    		}
    	}
    	else {
    		Contents.songList.add(s);
            Contents.stringElements.add(s.toString());	
    	}
    	if (!Contents.artistNameList.contains(s.artist))
    		Contents.artistNameList.add(s.artist);
    	if (!Contents.albumNameList.contains(s.album))
    		Contents.albumNameList.add(s.album);
    	
    }

    public static void setSongPosition(ArrayList<Song> list, int id) {
        activeList = list;
        Contents.position = id;
    }

    public static Song getSong() throws IndexOutOfBoundsException {
        Song song;
        // Not the queue
        if (activeList.size() > 0 && position < activeList.size()
                && position >= 0) {
            song = activeList.get(position);
            return song;
        }
        else {
            throw new IndexOutOfBoundsException("End of list");
        }
    }

    public static Song getNextSong() throws IndexOutOfBoundsException {
        position++;
        return getSong();
    }

    public static Song getRandomSong() throws IndexOutOfBoundsException {
        position = new Random(System.currentTimeMillis()).nextInt(activeList
                .size());
        return getSong();
    }

    public static Song getPreviousSong() {
        position--;
        return getSong();
    }

    public static void sortLists() {
        Comparator<Song> snc = new SongNameComparator();
        Comparator<String> snicc = new StringIgnoreCaseComparator();
        Collections.sort(stringElements, snicc); // Must be sorted!
        Collections.sort(songList, snc);
    }

    public static void clearLists() {
        songList.clear();
        stringElements.clear();
        queue.clear();
        ArtistElements.clear();
        AlbumElements.clear();
        artistNameList.clear();
        albumNameList.clear();
    }

    public static void addToQueue(Song s) throws IndexOutOfBoundsException {
        if (queue.size() > 9) {
            throw new IndexOutOfBoundsException("Can't add more than 10");
        }
        else {
            queue.add(s);
        }
    }
}