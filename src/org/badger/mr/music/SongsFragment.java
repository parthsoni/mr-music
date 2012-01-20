package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;

import org.badger.mr.music.download.DownloadBrowser;
import org.badger.mr.music.library.Library;
import org.badger.mr.music.library.Song;
import org.mult.daap.MediaPlayback;
import org.mult.daap.Preferences;
//import org.mult.daap.ArtistBrowser;
//import org.mult.daap.Contents;
//import org.mult.daap.MyIndexerAdapter;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppListFragment;

public class SongsFragment extends FragmentActivity {
	
	public SongsFragment() {
		Log.i("SongsFragment","Building Songs Fragment");
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("SongsFragment","Loading Songs Fragment");
    	super.onCreate(savedInstanceState);
        
    	FragmentManager fm = getSupportFragmentManager();
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            SongListFragment list = new SongListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }
    
	public void onBackPressed(){
    	final Intent intent = new Intent(this, MainPager.class);
        intent.putExtra("tab", 1);
        startActivity(intent);
        this.finish();	
	}
    
       
    
    public static class SongListFragment extends ListFragment
     {
    	SongListAdapter<Song> adapter;
    	ArrayList<Song> songList;
        private static final int CONTEXT_QUEUE = 0;
        private static final int CONTEXT_SAVE = 1;
        private static final int MENU_ABOUT = 1;
    	private static final int MENU_PREFS = 2;;
        private static final int MENU_SEARCH = 3;

    	
    	@Override public void onActivityCreated(Bundle savedInstanceState) {
    		 super.onActivityCreated(savedInstanceState);
            registerForContextMenu(getListView());
            getListView().setFastScrollEnabled(true);
            setEmptyText("No songs");
            setHasOptionsMenu(true);
            
            songList = Library.songBrowseList;
   		    //SongComparator snc = new SongComparator();
            //Collections.sort(songList,snc);
            adapter = new SongListAdapter<Song>(MrMusic.context, songList,Library.songSortType);
            
            Log.i("SongListFragment","Created Song Adapter. Items: " +adapter.getCount());
            setListAdapter(adapter);
             
            // Start out with a progress indicator.
            //setListShown(false);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            //getLoaderManager().initLoader(0, null, this);
        }
    	
    	@Override
        public  void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(getString(R.string.options));
            menu.add(0, CONTEXT_SAVE, 1, "Download Song");
    	}
    	
    	public boolean onContextItemSelected(MenuItem aItem) {
            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                    .getMenuInfo();
            Song s;
            //if (from.equals("album")) {
            //    s = Contents.filteredAlbumSongList.get(menuInfo.position);
            //}
            //else if (from.equals("artist")) {
            //    s = Contents.filteredArtistSongList.get(menuInfo.position);
            //}
            //else {
                s = songList.get(menuInfo.position);
            //}
            switch (aItem.getItemId()) {
            	case CONTEXT_SAVE:
    	        	//new Thread(new FileCopier(s,getApplicationContext())).start();
            		Library.addToDownloadQueue(s);
                	Intent dlintent = new Intent(getActivity().getBaseContext(),
                			DownloadBrowser.class);
                	startActivity(dlintent);
                	return true;
            }
            return false;
        }
    	
    	@Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.add(0, MENU_SEARCH, 0, getString(R.string.search)).setIcon(
                    android.R.drawable.ic_menu_search);
            menu.add(0, MENU_PREFS, 0, getString(R.string.preferences)).setIcon(
    				android.R.drawable.ic_menu_preferences);
            menu.add(0, MENU_ABOUT, 0, R.string.about_info).setIcon(
    				R.drawable.ic_menu_about);
    		
        }
    	
    	@Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent;
            Builder builder = new AlertDialog.Builder(getActivity());
            switch (item.getItemId()) {
               // case MENU_SEARCH:
               //     onSearchRequested();
               //     return true;
                case MENU_ABOUT:
        			builder.setTitle(getString(R.string.about_dialog_title));
        			builder.setMessage(getString(R.string.info));
        			builder.setPositiveButton(getString(android.R.string.ok), null);
        			builder.show();
        			return true;
        		case MENU_PREFS:
        			intent = new Intent(getActivity().getBaseContext(), Preferences.class);
        			startActivity(intent);
        			return true;
            }
            return false;
        }
    	
    	@Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("SongsFragment", "Item clicked: " + id);
            /**if (from.equals("album")) {
                Contents.setSongPosition(Contents.filteredAlbumSongList,
                        position);
            }
            else if (from.equals("artist")) {
                Contents.setSongPosition(Contents.filteredArtistSongList,
                        position);
            }
            else {
                Contents.setSongPosition(Contents.songList, position);
            //}**/
            Library.setPlayQueue(songList, position);
            MediaPlayback.clearState();
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            Intent intent = new Intent(getActivity().getBaseContext(), MediaPlayback.class);
            startActivityForResult(intent, 1);
        }
    	
    	

		    }

}
