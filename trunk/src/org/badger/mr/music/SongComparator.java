package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Song;



import android.provider.MediaStore;

public class SongComparator  implements Comparator<Song> {
	

	public int compare(Song s1, Song s2) {
		int ret;
		
		ret =  MediaStore.Audio.keyFor(s1.artist).compareTo(MediaStore.Audio.keyFor(s2.artist));
		if (ret == 0) {
			ret = MediaStore.Audio.keyFor(s1.album).compareTo(MediaStore.Audio.keyFor(s2.album));
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

