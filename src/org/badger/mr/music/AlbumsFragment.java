package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;

import org.badger.mr.music.download.DownloadBrowser;
import org.badger.mr.music.library.Album;
import org.badger.mr.music.library.Artist;
import org.badger.mr.music.library.Library;
//import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
import org.mult.daap.Preferences;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppListFragment;

public class AlbumsFragment extends FragmentActivity {
	
	public AlbumsFragment() {
		Log.i("AlbumsFragment","Building Albums Fragment");
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("ArtistsFragment.java","Loading Albums Fragment");
    	super.onCreate(savedInstanceState);
        
    	
    	
    	FragmentManager fm = getSupportFragmentManager();
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            AlbumListFragment list = new AlbumListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }
    
    
    
    
    public static class AlbumListFragment extends ListFragment
     {
    	AlbumListAdapter<Album> adapter;
    	//private ListView albumList;
    	ArrayList<Album> albumList;
    	//private String artistName;
        private static final int MENU_ABOUT = 1;
    	private static final int MENU_PREFS = 2;
        private static final int MENU_SEARCH = 3;
        private static final int MENU_DOWNLOADS = 4;
        private static final int CONTEXT_PLAY_ALBUM = 4;
        private static final int CONTEXT_SAVE_ALBUM = 5;
    	
    	@Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            registerForContextMenu(getListView());
            getListView().setFastScrollEnabled(true);
            // Give some text to display if there is no data.  In a real
            // application this would come from a resource.
            setEmptyText("No albums");
            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);
   		 	Log.i("AlbumListFragment","Artist Filter: " + Library.artistFilter);
   		 
   		    //adapter.notifyDataSetChanged();
        }
    	
    	@Override
    	public void onResume() {
    		 super.onResume();
    		 Log.i("AlbumListFragment","Resuming Album list");
    		 //albumList =  Library.getAlbumList(Library.filteredAlbums); 
    		 //albumList.add(0, new Album());
    		 //adapter.notifyDataSetChanged();
    		 albumList =  Library.getAlbumList(Library.filteredAlbums); 
    		 albumList.add(0, new Album());
    		 adapter = new AlbumListAdapter<Album>(MrMusic.context,albumList);
    		 Log.i("AlbumListFragment","Created Filtered Album List. Size: " + adapter.getCount());
             setListAdapter(adapter);
             
    	}
    	@Override
        public  void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(getString(R.string.options));
            menu.add(0, CONTEXT_PLAY_ALBUM, 0, R.string.play_album);
            menu.add(0,CONTEXT_SAVE_ALBUM,1, "Download Album");
        }
    	
    	
    	
    	@Override
        public boolean onContextItemSelected(MenuItem aItem) {
            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                    .getMenuInfo();
            Album album = albumList.get(menuInfo.position); 
            Log.i("AlbumsFragment","Context Selected album : (" + menuInfo.position + ") " + album.toString());
            //Library.albumFilter = album.toString();
            //Library.setFilters();
            
            switch (aItem.getItemId()) {
                case CONTEXT_PLAY_ALBUM:
                    Intent intent = new Intent(getActivity().getBaseContext(),
                            MediaPlayback.class);
                    Library.setPlayQueue(Library.getSongsList(album.getSongs()));
                    //Library.setPlayQueue();
                    MediaPlayback.clearState();
                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    startActivityForResult(intent, 1);
                    return true;
                case CONTEXT_SAVE_ALBUM:
                	Library.addToDownloadQueue(Library.getSongsList(album.getSongs()));
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
            menu.add(0, MENU_DOWNLOADS, 0, getString(R.string.downloader)).setIcon(android.R.drawable.ic_menu_save);
            menu.add(0, MENU_ABOUT, 0, R.string.about_info).setIcon(
    				R.drawable.ic_menu_about);
            
        }
    	@Override 
    	public boolean onOptionsItemSelected(MenuItem item) {
    		
    	    	   Intent intent;
    	           Builder builder = new AlertDialog.Builder(getActivity());
    	           switch (item.getItemId()) {
    	              // case MENU_SEARCH:
    	               //    onSearchRequested();
    	               //    return true;
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
    	       		case MENU_DOWNLOADS:
    	       			Intent dlintent = new Intent(getActivity().getBaseContext(),
                    			DownloadBrowser.class);
                    	startActivity(dlintent);
                    	return true;
    	       	
    	           }
    	        return false;
    	    }
    	
    	
        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("AlbumsFragment", "Item clicked: " + id +" at position " + position);
            
            Album album = albumList.get(position);
            Library.albumFilter = album.toString();
            Library.setFilters();
            Log.i("AlbumsFragment","Item Selected album : (" + position + ") " + album.toString());
            
            final Intent intent = new Intent(getActivity(), MainPager.class);
            intent.putExtra("tab", 2);
            startActivity(intent);
            getActivity().finish();
            
            //    Intent intent = new Intent(getActivity().getBaseContext(), SongBrowser.class);
            //    intent.putExtra("from", "album");
            //    intent.putExtra("albumName", Contents.albumNameList.get(position));
            //    startActivityForResult(intent, 1);
            }
            
           
        }
    	
    	

}


