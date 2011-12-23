package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
import org.mult.daap.client.Song;
import org.mult.daap.client.SongDiscNumComparator;
import org.mult.daap.client.SongTrackComparator;
import org.badger.mr.music.download.DownloadSong;
import org.badger.mr.music.local.FileDownloader;

import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadBrowser extends ListActivity {
	ProgressBar dlprogress;
	TextView activedownload;
	
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setResult(Activity.RESULT_OK);
	        
	        setContentView(R.layout.download_browser);
	        dlprogress =  (ProgressBar)findViewById(R.id.downloadprogress);
	        dlprogress.setMax(100);
	        activedownload = (TextView) findViewById(R.id.active_download);
	        createList();
	        if (Contents.downloader == null) {
	        	Log.i("DownloadBrowser","Creating new Downloader");
	        	Contents.downloader = new FileDownloader(this,dlprogress,activedownload);
	        	Contents.downloader.execute((Void) null);
	        }
	        else if (Contents.downloader.status == Contents.downloader.STATUS_IDLE) {
	        	Log.i("DownloadBrowser","Idle Downloader Rebuilding");
	        	Contents.downloader = new FileDownloader(this,dlprogress,activedownload);
	        	Contents.downloader.execute((Void) null);
	        }
	        
	    }
	 public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	        if (getListView() != null) {
	            getListView().clearTextFilter();
	        }
	        
	    }

	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode == Activity.RESULT_CANCELED) {
	            setResult(Activity.RESULT_CANCELED);
	            finish();
	        }
	    }

	    private void createList() {
	        //downloadList = getListView();
	        setListAdapter(new dlArrayAdapter<DownloadSong>(this,
	                    R.xml.long_list_text_view, Contents.downloadList));
	        }
	    
	    class dlArrayAdapter<T> extends ArrayAdapter<T> {
	        ArrayList<DownloadSong> myElements;
	        HashMap<String, Integer> alphaIndexer;
	        ArrayList<String> letterList;
	        Context vContext;
	        int font_size;

	        public dlArrayAdapter(Context context, 
					int textViewResourceId, ArrayList<DownloadSong> downloadList) {
				// TODO Auto-generated constructor stub
	        	super(context, textViewResourceId);
	        	SharedPreferences mPrefs = PreferenceManager
	                    .getDefaultSharedPreferences(context);
	            font_size = Integer.valueOf(mPrefs.getString("font_pref", "18"));
	            vContext = context;
	            myElements = (ArrayList<DownloadSong>) downloadList;
			}

	        @Override
	        public int getCount() {
	            return myElements.size();
	        }
	        
	        @Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		LayoutInflater inflater = ((Activity) vContext).getLayoutInflater();
	    		View rowView = inflater.inflate(R.layout.download_row, null, true);
	    		TextView textView = (TextView) rowView.findViewById(R.id.label);
	    		textView.setTextSize(font_size);
	    		//ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    		DownloadSong s = myElements.get(position);
	    		textView.setText(s.toString());
	    		//if (s.startsWith("Windows7") || s.startsWith("iPhone")
	    		//		|| s.startsWith("Solaris")) {
//
	//    			imageView.setImageResource(R.drawable.no);
	  //  		} else {
	    //			imageView.setImageResource(R.drawable.ok);
	    	//	}

	    		return rowView;
	    	}
	        
	    }

}
