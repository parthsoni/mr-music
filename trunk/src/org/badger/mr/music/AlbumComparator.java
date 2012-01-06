package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Album;

public class AlbumComparator  implements Comparator<Album> {
	

	public int compare(Album a1, Album a2) {
		return a1.getKey().compareTo(a2.getKey());
		
	}
}

