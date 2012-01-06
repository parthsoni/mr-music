package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Artist;


public class ArtistComparator  implements Comparator<Artist> {
	

	public int compare(Artist a1, Artist a2) {
		return a1.getKey().compareTo(a2.getKey());
		
	}
}

