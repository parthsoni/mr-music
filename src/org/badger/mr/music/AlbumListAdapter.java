package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.badger.mr.music.library.Album;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class AlbumListAdapter<T> extends ArrayAdapter<T> implements SectionIndexer,
		Filterable {
	ArrayList<Album> myAlbums;
	HashMap<String, Integer> alphaIndexer;
	ArrayList<String> sectionList;
	Context vContext;
	int font_size;
	
	@SuppressWarnings("unchecked")
	public AlbumListAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		Log.i("ArtistListAdapter","Creating Adapter. Items: " + objects.size());
		//SharedPreferences mPrefs = PreferenceManager
		//		.getDefaultSharedPreferences(context);
		//font_size = Integer.valueOf(mPrefs.getString("font_pref", "18"));
		vContext = context;
		myAlbums = (ArrayList<Album>) objects;
		alphaIndexer = new HashMap<String, Integer>();
		int size = myAlbums.size();
		for (int i = size - 1; i >= 0; i--) {
			Album a = myAlbums.get(i);
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
		return myAlbums.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(vContext.getApplicationContext());
		//tv.setTextSize(font_size);
		//tv.setTextColor(Color.WHITE);
		tv.setTextColor(Color.WHITE);
		tv.setText(myAlbums.get(position).toString());
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