package org.badger.mr.music.local;

import java.util.ArrayList;
import org.badger.mr.music.library.Song;
import org.mult.daap.client.Playlist;
//import org.mult.daap.client.Song;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;


public class LocalPlaylist extends Playlist {
	public int id;
	public String persistent_id;
	public boolean smart_playlist;
	public int song_count = 0;
	protected ArrayList<Song> songs;
	public int count;
	private Context activityContext;
	
	public LocalPlaylist(Context c) {
		activityContext = c;
		
	}
	    
	@SuppressWarnings("finally")
	@Override
	public ArrayList<Song> getSongs() {
		Cursor musiccursor;
	    int id_column;
	    int name_column;
	    int album_column;
	    int artist_column;
	    int track_column;
	    int duration_column;
	    int filepath_column;
	    int size_column;
	    
	    Song s;
	    
	    try {
		    String[] proj = { MediaStore.Audio.Media._ID,
					 MediaStore.Audio.Media.TITLE,
					 MediaStore.Audio.Media.ALBUM,
					 MediaStore.Audio.Media.ARTIST,
					 MediaStore.Audio.Media.TRACK,
					 MediaStore.Audio.Media.DURATION,
					 MediaStore.Audio.Media.DATA,
					 MediaStore.Audio.Media.SIZE
					  };
		    Log.i("LocalPlaylist","Attempting to Query MediaStore");
			musiccursor = activityContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					 proj, null, null, null);
			id_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
			name_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			album_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
			artist_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			track_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
			duration_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
			filepath_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			size_column = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
			
			count = musiccursor.getCount();
			Log.i("LocalPlaylist","Items recieved from the store: " + count);
			songs = new ArrayList<Song>();
			while (musiccursor.moveToNext()) {
	           
				s = new Song();
				s.album = musiccursor.getString(album_column);
				s.artist = musiccursor.getString(artist_column);
				s.id = musiccursor.getInt(id_column);
				s.isLocal = true;
				s.name = musiccursor.getString(name_column);
				s.size = musiccursor.getInt(size_column); 
				s.time = musiccursor.getInt(duration_column);
				//s.track = musiccursor.getString(track_column);
				s.track = 0;
				s.localPath = musiccursor.getString(filepath_column);
				//Log.i("LocalPlaylist","Adding " + s.toString());
			    songs.add(s);
	        }
	    }
		finally
		{
	       	return songs;
	    }
	}
	 

}
