package org.badger.mr.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.badger.mr.music.MainPager.TabsAdapter;
import org.badger.mr.music.download.DownloadSong;
import org.mult.daap.ArtistAlbumBrowser;
import org.mult.daap.ArtistBrowser;
import org.mult.daap.MediaPlayback;
import org.mult.daap.MyIndexerAdapter;
import org.mult.daap.Preferences;
//import org.mult.daap.ArtistBrowser;
import org.mult.daap.Contents;
//import org.mult.daap.MyIndexerAdapter;
import org.mult.daap.client.Song;


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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

//import com.example.android.supportv4.app.LoaderCustomSupport.AppListFragment;

public class ArtistsFragment extends FragmentActivity {
	
	public ArtistsFragment() {
		Log.i("ArtistsFragment","Building Artists Fragment");
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i("ArtistsFragment","Loading Artists Fragment");
    	super.onCreate(savedInstanceState);
        
    	FragmentManager fm = getSupportFragmentManager();
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            ArtistListFragment list = new ArtistListFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }
    }
    
       
    
    public static class ArtistListFragment extends ListFragment
     {
    	MyIndexerAdapter<String> adapter;
    	private static final int MENU_SEARCH = 3;
        private static final int CONTEXT_PLAY_ARTIST = 4;
        private static final int CONTEXT_SAVE_ARTIST = 5;
        private static final int MENU_ABOUT = 1;
    	private static final int MENU_PREFS = 2;
    	
    	@Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Give some text to display if there is no data.  In a real
            // application this would come from a resource.
            setEmptyText("No artists");
            registerForContextMenu(getListView());
            getListView().setFastScrollEnabled(true);
            // We have a menu item to show in action bar.
            setHasOptionsMenu(true);
            
            //Remove the All Artists before sorting
            if (Contents.artistNameList.size() > 0)
            	Contents.artistNameList.remove(0);
            
            // Create an empty adapter we will use to display the loaded data.
            KeyComparator kc = new KeyComparator();
            Collections.sort(Contents.artistNameList,kc);
            adapter = new MyIndexerAdapter<String>(MrMusic.context, R.xml.long_list_text_view, Contents.artistNameList);
            if (!Contents.artistNameList.contains("All Artists"))
            	adapter.insert("All Artists", 0);
            Log.i("ArtistListFragment","Created Artist Adapter. Items: " +adapter.getCount());
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
            menu.add(0, CONTEXT_PLAY_ARTIST, 0,
                    R.string.play_artist);
            menu.add(0, CONTEXT_SAVE_ARTIST, 1, "Download Artist");
        }
    	
    	@Override
        public boolean onContextItemSelected(MenuItem aItem) {
            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                    .getMenuInfo();
            String albName = new String(
                    Contents.artistNameList.get(menuInfo.position));
            if (albName.equals(getString(R.string.no_artist_name))) {
                albName = "";
            }
            Contents.filteredArtistSongList.clear();
            for (Song s : Contents.songList) {
                if (s.artist.equals(albName)) {
                    Contents.filteredArtistSongList.add(s);
                }
            }
            switch (aItem.getItemId()) {
                case CONTEXT_PLAY_ARTIST:
                    Intent intent = new Intent(getActivity().getBaseContext(),
                            MediaPlayback.class);
                    
                    Contents.setSongPosition(Contents.filteredArtistSongList, 0);
                    MediaPlayback.clearState();
                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    startActivityForResult(intent, 1);
                    return true;
                case CONTEXT_SAVE_ARTIST:
                	//new Thread(new FileCopier(Contents.filteredArtistSongList,getApplicationContext())).start();
                	for (Song s : Contents.filteredArtistSongList) {
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
            Log.i("ArtistsFragment", "Item clicked: " + id);
            if (id == 0) {
            	Contents.artistFilter = "";
            }
            else
            {
	            String artist = Contents.artistNameList.get(position);
	            Contents.artistFilter = artist;
	            Contents.filteredAlbumNameList.clear();
	            for (Song song : Contents.songList) {
	                if (song.artist.equals(artist)) {
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
	            //Contents.artistAlbumNameList.clear();
	            //Intent intent = new Intent(getActivity().getBaseContext(),
	            //        ArtistAlbumBrowser.class);
	            //intent.putExtra("from", "artist");
	            //intent.putExtra("artistName", Contents.artistNameList.get(position));
	            //startActivityForResult(intent, 1);
	            
	            //Intent intent = new Intent(getActivity().getBaseContext(),
	            //		AlbumsFragment.class);
	            //startActivity(intent);
	            
	            //MrMusic.switchTab(1);
            }
            
            final Intent intent = new Intent(getActivity(), MainPager.class);
            intent.putExtra("tab", 1);
            startActivity(intent);
            getActivity().finish();
        }
    	
    	

		    }

}