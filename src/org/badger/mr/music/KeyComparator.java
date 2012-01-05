package org.badger.mr.music;

import java.util.Comparator;

import android.provider.MediaStore;

public class KeyComparator  implements Comparator<String> {
	   public int compare(String s1, String s2) {
		      return MediaStore.Audio.keyFor(s1).compareTo(MediaStore.Audio.keyFor(s2));
		      //return s1.toString().compareToIgnoreCase(s2.toString());
		   }
		}

