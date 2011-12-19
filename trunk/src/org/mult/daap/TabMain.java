package org.mult.daap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import org.badger.mr.music.MediaSources;
import org.badger.mr.music.R;
import org.mult.daap.background.GetSongsForPlaylist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class TabMain extends TabActivity implements Observer {
	private ProgressDialog pd = null;
	private TextView libSource;
	private TabHost tabHost;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("TabMain","Creating Main Tab");
		
		
		setResult(Activity.RESULT_OK);
		setContentView(R.xml.tab_main);
		libSource = (TextView) findViewById(R.id.library_source);
		libSource.setOnClickListener(libSourceListener);
		if (Contents.daapHost == null)
			libSource.setText("Local Library (Touch to change)");
		else
			libSource.setText("Remote Library (Touch to change)");
		if (Contents.songList.size() == 0)
		{
			Log.i("TabMain","Playlist is empty. Loading the local list");
			try {
				Contents.address = InetAddress.getLocalHost();
			}
			catch(UnknownHostException e) {
				
			}
			buildLocalPlaylist();
		}
		Resources res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent = new Intent(); // Reusable Intent for each tab
		intent.putExtra("from", "TabMain");
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent.setClass(this, ArtistBrowser.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("artists")
				.setIndicator(getString(R.string.artists),
						res.getDrawable(R.xml.ic_tab_artists))
				.setContent(intent);
		tabHost.addTab(spec);
		
		
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, AlbumBrowser.class);
		intent.putExtra("from", "TabMain");
		spec = tabHost
				.newTabSpec("albums")
				.setIndicator(getString(R.string.albums),
						res.getDrawable(R.xml.ic_tab_albums))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, SongBrowser.class);
		intent.putExtra("from", "TabMain");
		spec = tabHost
				.newTabSpec("songs")
				.setIndicator(getString(R.string.songs),
						res.getDrawable(R.xml.ic_tab_songs)).setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);
		Log.i("TabMain","TabHost: "+ tabHost.toString());
		Log.i("TabMain","Finished creating main tab");
		
	}
	
	private View.OnClickListener libSourceListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Intent intent = new Intent(TabMain.this, MediaSources.class);
            startActivity(intent);
		}
	};
	

	
	private void buildLocalPlaylist() {
		Log.i("TabMain","Building the local playlist");
    	NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Contents.clearLists();
        MediaPlayback.clearState();
        GetSongsForPlaylist gsfp = new GetSongsForPlaylist();
        Contents.getSongsForPlaylist = gsfp;
        gsfp.addObserver(this);
        gsfp.activityContext = this.getBaseContext();
        Thread thread = new Thread(gsfp);
        thread.start();
        update(gsfp, GetSongsForPlaylist.START);
    
    }
	
	private Handler uiHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            if (msg.what == GetSongsForPlaylist.FINISHED) { // Finished
	                if (pd != null) {
	                    pd.dismiss();
	                }
	                Log.i("TabMain","Finished Loading Music, continuing to the music browser");
	                Contents.getSongsForPlaylist = null; 
	                tabHost.setCurrentTab(0);
	                final Intent intent = new Intent(TabMain.this, TabMain.class);
	                startActivity(intent);
	                TabMain.this.finish();
	            }
	            else if (msg.what == GetSongsForPlaylist.EMPTY) {
	                if (pd != null) {
	                    pd.dismiss();
	                }
	                Contents.getSongsForPlaylist = null;
	                Toast tst = Toast.makeText(TabMain.this,
	                        getString(R.string.empty_playlist), Toast.LENGTH_LONG);
	                tst.setGravity(Gravity.CENTER, tst.getXOffset() / 2,
	                        tst.getYOffset() / 2);
	                tst.show();
	            }
	        }
	    };

	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (((Integer) data).compareTo(GetSongsForPlaylist.START) == 0) {
            pd = ProgressDialog.show(this,
                    getString(R.string.fetching_music_title),
                    getString(R.string.fetching_music_detail), true, false);
 		}
	    else if (((Integer) data).compareTo(GetSongsForPlaylist.FINISHED) == 0) {
	        uiHandler.sendEmptyMessage(GetSongsForPlaylist.FINISHED);
	    }
	    else if (((Integer) data).compareTo(GetSongsForPlaylist.EMPTY) == 0) {
	        uiHandler.sendEmptyMessage(GetSongsForPlaylist.EMPTY);
	    }
	}
}