package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.badger.mr.music.library.Artist;
import org.badger.mr.music.library.Library;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ArtistListAdapter<T> extends ArrayAdapter<T> implements SectionIndexer,
		Filterable {
	ArrayList<Artist> myArtists;
	HashMap<String, Integer> alphaIndexer;
	ArrayList<String> sectionList;
	Context vContext;
	int font_size;
	
	@SuppressWarnings("unchecked")
	public ArtistListAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		Log.i("ArtistListAdapter","Creating Adapter. Items: " + objects.size());
		SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		font_size = Integer.valueOf(mPrefs.getString("font_pref", "18"));
		vContext = context;
		myArtists = (ArrayList<Artist>) objects;
		alphaIndexer = new HashMap<String, Integer>();
		int size = myArtists.size();
		for (int i = size - 1; i >= 0; i--) {
			Artist a = myArtists.get(i);
			if (a.name.length() != 0) { // no album/artist
				alphaIndexer.put(a.toString().substring(0, 1).toUpperCase(), i);
			} else {
				alphaIndexer.put(" ", i);
			}
		}
		sectionList = new ArrayList<String>(alphaIndexer.keySet()); // list can be
		// sorted
		Collections.sort(sectionList);
	}

	@Override
	public int getCount() {
		return myArtists.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(vContext.getApplicationContext());
		tv.setTextSize(font_size);
		if (myArtists.get(position).isSame(Library.artistFilter)) {
			tv.setBackgroundColor(Color.LTGRAY);
			tv.setTextColor(Color.BLACK);
			tv.setSelected(true);
		}
		else
			tv.setTextColor(Color.WHITE);
		tv.setText(myArtists.get(position).toString());
		return tv;
	}

	public int getPositionForSection(int section) {
		return alphaIndexer.get(sectionList.get(section));
	}

	public int getSectionForPosition(int position) {
		return 0;
	}

	public Object[] getSections() {
		return sectionList.toArray();
	}
}