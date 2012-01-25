package org.badger.mr.music;

import java.util.Comparator;

import org.badger.mr.music.library.Library;

import android.provider.MediaStore;

public class KeyComparator  implements Comparator<String> {
	   public int compare(String s1, String s2) {
		      return Library.KeyFor(s1).compareTo(Library.KeyFor(s2));
		      //return s1.toString().compareToIgnoreCase(s2.toString());
		   }
		}

