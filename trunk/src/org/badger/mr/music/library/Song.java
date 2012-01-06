/*
 * Created on May 7, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * Copyright 2003 Joseph Barnett
 * 
 * This File is part of "one 2 oh my god"
 * 
 * "one 2 oh my god" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * Free Software Foundation; either version 2 of the License, or
 * your option) any later version.
 * 
 * "one 2 oh my god" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with "one 2 oh my god"; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.badger.mr.music.library;

import org.mult.daap.client.Host;

import android.provider.MediaStore;
import android.util.Log;



/** @author jbarnett
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments */
public class Song implements Comparable<Object> {
	public String name;
	public int id;
	public int time;
	public String album;
	public String artist;
	public short track;
	public short disc_num;
	public String format;
	public int size;
	public Host host;
	public String genre;
	public boolean isLocal;
	public String localPath;
	
	// public int status;
	// public static int STATUS_OK = 0;
	// public static int STATUS_NOT_FOUND = 2;
	// public static int STATUS_ERROR = 3;
	// public boolean compilation;
	// public int bitrate;
	// public String persistent_id;
	// 
	// public boolean is_available;
	public Song() {
		name = "";
		id = 0;
		album = "";
		artist = "";
		track = -1;
		genre = "";
		format = "";
		time = 0;
		size = 0;
		// compilation = false;
		host = null;
		isLocal = false;
		localPath = "";
		// status = Song.STATUS_OK;

	}

	public boolean isSame(Song another) {
		return isSame(another.artist, another.album, another.name);
				
		    		
	}
	
	public boolean isSame(String otherartist, String otheralbum, String othertitle) {
		boolean ret;
		//Log.i("Song","Comparing " + othertitle + " to " + this.name);
		ret = MediaStore.Audio.keyFor(this.name).equals(MediaStore.Audio.keyFor(othertitle)) &&
				 MediaStore.Audio.keyFor(this.artist).equals(MediaStore.Audio.keyFor(otherartist)) &&
			     MediaStore.Audio.keyFor(this.album).equals(MediaStore.Audio.keyFor(otheralbum));
		return ret;
	}
	
	public void mergeSong(Song another) {
		//Fill in missing information
		if (another.isLocal) {
			isLocal = true;
			localPath = another.localPath;
		}
		else {
			host = another.host;
		}
		if (genre.length() == 0)
			genre = another.genre;
		if (track == -1)
			track = another.track;
		if (time == 0)
			time = another.time;
		if (size == 0)
			size = another.size;
		if (format == "")
			format = another.format;
	}
	
	public String toString() {
		String ret = artist + (artist.length() > 0 ? " - " : "") + name;
		return ret;
	}
	
	public String toTrackTitleString() {
		//return track + " " + name;
		return name;
	}

	public int compareTo(Object another) {
		int ret;
		if (another instanceof Song) {
			Song s2 = (Song) another;
			ret =  MediaStore.Audio.keyFor(artist).compareTo(MediaStore.Audio.keyFor(s2.artist));
			if (ret == 0) {
				ret = MediaStore.Audio.keyFor(album).compareTo(MediaStore.Audio.keyFor(s2.album));
			}
			if (ret == 0) {
				ret = disc_num - s2.disc_num; 
			}
			if (ret == 0) {
				ret = (track - s2.track);
			}
		}
		else
			ret = 0;
		return ret;
	}
	
}