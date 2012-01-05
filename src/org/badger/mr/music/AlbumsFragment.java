package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.badger.mr.music.download.DownloadSong;
import org.mult.daap.ArtistAlbumBrowser;
//import org.mult.daap.ArtistBrowser;
import org.mult.daap.AlbumBrowser;
import org.mult.daap.Contents;
import org.mult.daap.MediaPlayback;
import org.mult.daap.MyIndexerAdapter;
import org.mult.daap.Preferences;
import org.mult.daap.SongBrowser;
import org.mult.daap.client.Song;
import org.mult.daap.client.StringIgnoreCaseComparator;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppEntry;
//import com.example.android.supportv4.app.LoaderCustomSupport.AppListLoader;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppListAdapter;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppEntry;
//import com.example.android.supportv4.app.LoaderCustomSupport.AppListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    	MyIndexerAdapter<String> adapter;
    	//private ListView albumList;
    	private String artistName;
        private static final int MENU_ABOUT = 1;
    	private static final int MENU_PREFS = 2;
        private static final int MENU_SEARCH = 3;
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
   		 	Log.i("AlbumListFragment","Artist Filter: " + Contents.artistFilter);
   		 	//Contents.filteredAlbumNameList.clear();
   		 	/**if (Contents.artistFilter.length() > 0) {
            	//setTitle(Contents.artistFilter);
            	 for (Map.Entry<String, ArrayList<Integer>> entry : Contents.ArtistAlbumElements
                        .entrySet()) {
                    String key = entry.getKey();
                    if (key.length() == 0) {
                        Contents.filteredAlbumNameList
                                .add(getString(R.string.no_album_name));
                    }
                    else {
                        Contents.filteredAlbumNameList.add(key);
                    }
                }	
   		 	}**/
   		 	if (Contents.artistFilter.length() > 0) {
   		 		//Set Title
   		 		artistName = Contents.artistFilter;
   		 	}
   		 	else {
   		 		Contents.filteredAlbumNameList.clear();
            	Contents.filteredAlbumNameList.addAll(Contents.albumNameList);
            	artistName = "All Artists";
   		 	}
   		 	
   		 	;
   		    KeyComparator kc = new KeyComparator();
            Collections.sort(Contents.filteredAlbumNameList,kc);
   		 	
   		 	adapter = new MyIndexerAdapter<String>(MrMusic.context, R.xml.long_list_text_view, Contents.filteredAlbumNameList);
   		 	adapter.insert("All Albums for " + artistName, 0);
   		 	Log.i("AlbumListFragment","Created Filtered Album List. Size: " + adapter.getCount());
            
            //Log.i("Albums Fragment","Setting up the albums list");
            //KeyComparator kc = new KeyComparator();
            //Collections.sort(Contents.albumNameList,kc);
            
            //adapter = new MyIndexerAdapter<String>(MrMusic.context, R.xml.long_list_text_view, Contents.albumNameList);
            
            //Log.i("AlbumListFragment","Created Album List. Size: " + adapter.getCount());
            setListAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }
    	
    	@Override
    	public void onResume() {
    		 super.onResume();
    		 Log.i("AlbumListFragment","Resuming Album list");

             adapter.notifyDataSetChanged();
    	}
    	@Override
        public  void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(getString(R.string.options));
            menu.add(0, CONTEXT_PLAY_ALBUM, 0, R.string.play_album);
            menu.add(0,CONTEXT_SAVE_ALBUM,1, "Download Album");
        }
    	
    	public void buildSelectedSongList() {
    		String albName = new String(
                    Contents.filteredAlbumNameList.get(menuInfo.position));
            if (albName.equals(getString(R.string.no_album_name))) {
                albName = "";
            }
            Contents.filteredAlbumSongList.clear();
            for (Song s : Contents.songList) {
                if (s.album.equals(albName)) {
                	if (Contents.artistFilter.length() > 0) {
                		if (s.artist.equals(Contents.artistFilter))
                			Contents.filteredSongList.add(s);
                	}
                	else
                		Contents.filteredSongList.add(s);
                }
            }
    	}
    	
    	@Override
        public boolean onContextItemSelected(MenuItem aItem) {
            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                    .getMenuInfo();
            buildSelectedSongList();
            switch (aItem.getItemId()) {
                case CONTEXT_PLAY_ALBUM:
                    Intent intent = new Intent(getActivity().getBaseContext(),
                            MediaPlayback.class);
                    Contents.setSongPosition(Contents.filteredSongList, 0);
                    MediaPlayback.clearState();
                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    startActivityForResult(intent, 1);
                    return true;
                case CONTEXT_SAVE_ALBUM:
                	//New Thread(new FileCopier(Contents.filteredAlbumSongList,getApplicationContext())).start();
                	for (Song s : Contents.filteredSongList) {
                		Contents.downloadList.add(DownloadSong.toDownloadSong(s));
                	}
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
    	       	
    	           }
    	        return false;
    	    }
    	
        @Override public void onListItemClick(ListView l, View v, int position, long id) {
            // Insert desired behavior here.
            Log.i("AlbumsFragment", "Item clicked: " + id);
            if (id == 0) {
            	Contents.albumFilter = "";
            }
            else
            {
            	String album = Contents.filteredAlbumNameList.get(position);
	            Contents.albumFilter = album;
	            Contents.filteredSongNameList.clear();
	            for (Song song : Contents.songList) {
	                if (song.album.equals(album)) {
	                	if (!Contents.filteredAlbumNameList.contains(song.album))
	                		Contents.filteredAlbumNameList.add(song.album);
	                    /**if (Contents.ArtistAlbumElements.containsKey(song.album)) {
	                        Contents.ArtistAlbumElements.get(song.album).add(
	                                song.id);
	                    }
	                    else {
	                        ArrayList<Integer> t = new ArrayList<Integer>();
	                        t.add(song.id);
	                        Contents.ArtistAlbumElements.put(song.album, t);
	                        Contents.filteredAlbumNameList.add(song.album);
	                    }**/
	                }
	            }
            }
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


