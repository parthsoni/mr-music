package org.mult.daap.client;

import java.util.Comparator;

import org.badger.mr.music.library.Song;

public class SongIDComparator implements Comparator<Song> {
	public int compare(Song s1, Song s2) {
		if (s1.id < s2.id) {
			return -1;
		} else if (s1.id > s2.id) {
			return 1;
		} else
			return 0;
		// return (s1.id - s2.id);
	}
}
