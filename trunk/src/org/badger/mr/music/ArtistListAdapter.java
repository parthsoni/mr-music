package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.badger.mr.music.library.Artist;
import org.badger.mr.music.library.Library;
import org.badger.mr.music.library.Song;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ArtistListAdapter<T> extends ArrayAdapter<T> implements SectionIndexer,
		Filterable {
	ArrayList<Artist> myArtists;
	HashMap<String, Integer> alphaIndexer;
	ArrayList<String> sectionList;
	Context vContext;
	int font_size;
	private static int tvResourceID = R.xml.media_row_view;
	
	@SuppressWarnings("unchecked")
	public ArtistListAdapter(Context context, 
			List<T> objects) {
		super(context,  tvResourceID, objects);
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
				alphaIndexer.put(a.getSortSection(), i);
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
		/**TextView tv = new TextView(vContext.getApplicationContext());
		tv.setTextSize(font_size);
		if (myArtists.get(position).isSame(Library.artistFilter)) {
			tv.setBackgroundColor(Color.LTGRAY);
			tv.setTextColor(Color.BLACK);
			tv.setSelected(true);
		}
		else
			tv.setTextColor(Color.WHITE);
		tv.setText(myArtists.get(position).toString());
		return tv;**/
		View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) MrMusic.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate( tvResourceID, null);
        }
        Artist a = myArtists.get(position);
        if (a != null) {
                ImageView ivLocal  = (ImageView) v.findViewById(R.id.SourceLocalImg);
                ImageView ivRemote =  (ImageView) v.findViewById(R.id.SourceRemoteImg);
                TextView tvTitle = (TextView) v.findViewById(R.id.TitleRow);
                TextView tvAlbumArtist = (TextView) v.findViewById(R.id.SecondRow);
                //TextView tvLength = (TextView) v.findViewById(R.id.songLength);
                
                if (a.HasLocal == Library.HAS_SOME)
               	 ivLocal.setVisibility(View.VISIBLE);
                else
               	 ivLocal.setVisibility(View.INVISIBLE);
                
                if (a.HasDaap == Library.HAS_SOME)
               	 ivRemote.setVisibility(View.VISIBLE);
                else
               	 ivRemote.setVisibility(View.INVISIBLE);
                if (a.isAllArtists) {
                	tvTitle.setText("All Artists");
                }
                else
                {
                	tvTitle.setText(a.toString());
                }
                tvTitle.setTextSize(font_size);
                tvAlbumArtist.setVisibility(View.GONE);
              
                // tvLength.setText(s.getTime());
        }
        return v;
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