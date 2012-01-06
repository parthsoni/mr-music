package org.mult.daap.client;

import java.util.Comparator;

import org.badger.mr.music.library.Song;

public class SongTrackComparator implements Comparator<Song> {
	public int compare(Song s1, Song s2) {
		return (s1.track - s2.track);
	}
}