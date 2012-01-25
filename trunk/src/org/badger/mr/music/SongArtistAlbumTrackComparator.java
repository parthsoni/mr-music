package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Library;
import org.badger.mr.music.library.Song;



import android.provider.MediaStore;

public class SongArtistAlbumTrackComparator  implements Comparator<Song> {
	

	public int compare(Song s1, Song s2) {
		int ret;
		//Sort by Artist, then Album, then track
		ret =  Library.KeyFor(s1.artist).compareTo(Library.KeyFor(s2.artist));
		if (ret == 0) {
			ret = Library.KeyFor(s1.album).compareTo(Library.KeyFor(s2.album));
		}
		if (ret == 0) {
			ret = s1.disc_num - s2.disc_num; 
		}
		if (ret == 0) {
			ret = (s1.track - s2.track);
		}
		return ret;
	}
}

