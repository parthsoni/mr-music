package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.badger.mr.music.SongsFragment.SongListFragment;
import org.badger.mr.music.library.Song;

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

public class SongListAdapter<T> extends ArrayAdapter<T> implements SectionIndexer,
		Filterable {
	ArrayList<Song> mySongs;
	HashMap<String, Integer> alphaIndexer;
	ArrayList<String> sectionList;
	Context vContext;
	int font_size;
	private int sectionType;
	
	@SuppressWarnings("unchecked")
	public SongListAdapter(Context context, int textViewResourceId,
			List<T> objects, int sType) {
		super(context, textViewResourceId, objects);
		Log.i("SongListAdapter","Creating Adapter. Items: " + objects.size());
		SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		font_size = Integer.valueOf(mPrefs.getString("font_pref", "18"));
		vContext = context;
		mySongs = (ArrayList<Song>) objects;
		sectionType = sType;
		alphaIndexer = new HashMap<String, Integer>();
		int size = mySongs.size();
		for (int i = size - 1; i >= 0; i--) {
			Song s = mySongs.get(i);
			switch (sectionType) {
				case SongListFragment.SECTION_TYPE_ARTIST: 
					alphaIndexer.put(s.artist.toUpperCase(), i);
					break;
				case SongListFragment.SECTION_TYPE_ALBUM:
					alphaIndexer.put(s.album.toUpperCase(), i);
					break;
				case SongListFragment.SECTION_TYPE_SONG:
					//No Indexer
					break;
			}
		}
		sectionList = new ArrayList<String>(alphaIndexer.keySet()); // list can be
		// sorted
		Collections.sort(sectionList);
	}

	@Override
	public int getCount() {
		return mySongs.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(vContext.getApplicationContext());
		tv.setTextSize(font_size);
		//tv.setTextColor(Color.WHITE);
		if (mySongs.get(position).isLocal)
			tv.setTextColor(Color.WHITE);
		else
			tv.setTextColor(Color.LTGRAY);
		
		switch (sectionType) {
			case SongListFragment.SECTION_TYPE_ARTIST: 
				tv.setText(mySongs.get(position).toTrackTitleString());
				break;
			case SongListFragment.SECTION_TYPE_ALBUM:
				tv.setText(mySongs.get(position).toTrackTitleString());
				break;
			case SongListFragment.SECTION_TYPE_SONG:
				tv.setText(mySongs.get(position).toString());
				break;
		}
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