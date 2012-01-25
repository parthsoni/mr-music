package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Library;
import org.badger.mr.music.library.Song;



import android.provider.MediaStore;

public class SongTitleComparator  implements Comparator<Song> {
	

	public int compare(Song s1, Song s2) {
		int ret;
		//Sort by Artist, then Album, then track
		ret =  Library.KeyFor(s1.name).compareTo(Library.KeyFor(s2.name));
		return ret;
	}
}

