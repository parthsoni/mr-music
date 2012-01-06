package org.mult.daap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


import org.mult.daap.client.StringIgnoreCaseComparator;

import org.badger.mr.music.DownloadBrowser;
import org.badger.mr.music.R;
import org.badger.mr.music.download.DownloadSong;
import org.badger.mr.music.library.Song;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArtistBrowser extends ListActivity {
    private ListView artistList;
    //private static final int MENU_PLAY_QUEUE = 1;
    //private static final int MENU_VIEW_QUEUE = 2;
    private static final int MENU_SEARCH = 3;
    private static final int CONTEXT_PLAY_ARTIST = 4;
    private static final int CONTEXT_SAVE_ARTIST = 5;
    private static final int MENU_ABOUT = 1;
	private static final int MENU_PREFS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_OK);
        if (Contents.address == null) {
            // We got kicked out of memory probably
            MediaPlayback.clearState();
            Contents.clearLists();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }
        if (Contents.artistNameList.size() == 0) {
            for (Map.Entry<String, ArrayList<Integer>> entry : Contents.ArtistElements
                    .entrySet()) {
                String key = entry.getKey();
                if (key.length() == 0) {
                    Contents.artistNameList
                            .add(getString(R.string.no_artist_name));
                }
                else {
                    Contents.artistNameList.add(key);
                }
            }
            Comparator<String> snicc = new StringIgnoreCaseComparator();
            Collections.sort(Contents.artistNameList, snicc);
        }
        setContentView(R.xml.music_browser);
        createList();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private void createList() {
        artistList = (ListView) findViewById(android.R.id.list);
        MyIndexerAdapter<String> adapter = new MyIndexerAdapter<String>(
                getApplicationContext(), R.xml.long_list_text_view,
                Contents.artistNameList);
        setListAdapter(adapter);
        artistList.setOnItemClickListener(musicGridListener);
        artistList.setFastScrollEnabled(true);
        artistList
                .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                    public void onCreateContextMenu(ContextMenu menu, View v,
                            ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(getString(R.string.options));
                        menu.add(0, CONTEXT_PLAY_ARTIST, 0,
                                R.string.play_artist);
                        menu.add(0, CONTEXT_SAVE_ARTIST, 1, "Download Artist");
                    }
                });
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
                Intent intent = new Intent(ArtistBrowser.this,
                        MediaPlayback.class);
                
                Contents.setSongPosition(Contents.filteredArtistSongList, 0);
                MediaPlayback.clearState();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                startActivityForResult(intent, 1);
                return true;
            case CONTEXT_SAVE_ARTIST:
            	//new Thread(new FileCopier(Contents.filteredArtistSongList,getApplicationContext())).start();
            	for (Song s : Contents.filteredArtistSongList) {
            		Contents.downloadList.add(DownloadSong.toDownloadSong(s));
            	}
            	Intent dlintent = new Intent(ArtistBrowser.this,
            			DownloadBrowser.class);
            	startActivity(dlintent);
            	return true;
        }
        return false;
    }

    private OnItemClickListener musicGridListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                long id) {
            String artist = Contents.artistNameList.get(position);
            Contents.ArtistAlbumElements.clear();
            for (Song song : Contents.songList) {
                if (song.artist.equals(artist)) {
                    if (Contents.ArtistAlbumElements.containsKey(song.album)) {
                        Contents.ArtistAlbumElements.get(song.album).add(
                                song.id);
                    }
                    else {
                        ArrayList<Integer> t = new ArrayList<Integer>();
                        t.add(song.id);
                        Contents.ArtistAlbumElements.put(song.album, t);
                        Contents.filteredAlbumNameList.add(song.album);
                    }
                }
            }
            Contents.filteredAlbumNameList.clear();
            Intent intent = new Intent(ArtistBrowser.this,
                    ArtistAlbumBrowser.class);
            intent.putExtra("from", "artist");
            intent.putExtra("artistName", Contents.artistNameList.get(position));
            startActivityForResult(intent, 1);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SEARCH, 0, getString(R.string.search)).setIcon(
                android.R.drawable.ic_menu_search);
        menu.add(0, MENU_PREFS, 0, getString(R.string.preferences)).setIcon(
				android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_ABOUT, 0, R.string.about_info).setIcon(
				R.drawable.ic_menu_about);
		return true;
    }

    

    @Override
    public boolean onSearchRequested() {
        Contents.searchResult = null;
        startSearch(null, false, null, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case MENU_SEARCH:
                onSearchRequested();
                return true;
            case MENU_ABOUT:
    			builder.setTitle(getString(R.string.about_dialog_title));
    			builder.setMessage(getString(R.string.info));
    			builder.setPositiveButton(getString(android.R.string.ok), null);
    			builder.show();
    			return true;
    		case MENU_PREFS:
    			intent = new Intent(ArtistBrowser.this, Preferences.class);
    			startActivity(intent);
    			return true;
    	
        }
        return false;
    }
}