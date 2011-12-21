package org.mult.daap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.mult.daap.FileUtils.FileCopier;
import org.mult.daap.client.Song;
import org.mult.daap.client.StringIgnoreCaseComparator;

import org.badger.mr.music.DownloadBrowser;
import org.badger.mr.music.R;
import org.badger.mr.music.download.DownloadSong;

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

public class AlbumBrowser extends ListActivity {
    private ListView albumList;
    private static final int MENU_ABOUT = 1;
	private static final int MENU_PREFS = 2;
    private static final int MENU_SEARCH = 3;
    private static final int CONTEXT_PLAY_ALBUM = 4;
    private static final int CONTEXT_SAVE_ALBUM = 5;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        // if we haven't obtained the album list yet
        if (Contents.albumNameList.size() == 0) {
            for (Map.Entry<String, ArrayList<Integer>> entry : Contents.AlbumElements
                    .entrySet()) {
                String key = entry.getKey();
                if (key.length() == 0) {
                    Contents.albumNameList
                            .add(getString(R.string.no_album_name));
                }
                else {
                    Contents.albumNameList.add(key);
                }
            }
            Comparator<String> snicc = new StringIgnoreCaseComparator();
            Collections.sort(Contents.albumNameList, snicc);
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
        albumList = (ListView) findViewById(android.R.id.list);
        MyIndexerAdapter<String> adapter = new MyIndexerAdapter<String>(
                getApplicationContext(), R.xml.long_list_text_view,
                Contents.albumNameList);
        setListAdapter(adapter);
        albumList.setOnItemClickListener(musicGridListener);
        albumList.setFastScrollEnabled(true);
        albumList
                .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                    public void onCreateContextMenu(ContextMenu menu, View v,
                            ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(getString(R.string.options));
                        menu.add(0, CONTEXT_PLAY_ALBUM, 0, R.string.play_album);
                        menu.add(0,CONTEXT_SAVE_ALBUM,1, "Download Album");
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(MenuItem aItem) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
                .getMenuInfo();
        String albName = new String(
                Contents.albumNameList.get(menuInfo.position));
        if (albName.equals(getString(R.string.no_album_name))) {
            albName = "";
        }
        Contents.filteredAlbumSongList.clear();
        for (Song s : Contents.songList) {
            if (s.album.equals(albName)) {
                Contents.filteredAlbumSongList.add(s);
            }
        }
        switch (aItem.getItemId()) {
            case CONTEXT_PLAY_ALBUM:
                Intent intent = new Intent(AlbumBrowser.this,
                        MediaPlayback.class);
                Contents.setSongPosition(Contents.filteredAlbumSongList, 0);
                MediaPlayback.clearState();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                startActivityForResult(intent, 1);
                return true;
            case CONTEXT_SAVE_ALBUM:
            	//New Thread(new FileCopier(Contents.filteredAlbumSongList,getApplicationContext())).start();
            	for (Song s : Contents.filteredAlbumSongList) {
            		Contents.downloadList.add(DownloadSong.toDownloadSong(s));
            	}
            	Intent dlintent = new Intent(AlbumBrowser.this,
            			DownloadBrowser.class);
            	startActivity(dlintent);
            	return true;
        }
        return false;
    }

    private OnItemClickListener musicGridListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                long id) {
            Intent intent = new Intent(AlbumBrowser.this, SongBrowser.class);
            intent.putExtra("from", "album");
            intent.putExtra("albumName", Contents.albumNameList.get(position));
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
       			intent = new Intent(AlbumBrowser.this, Preferences.class);
       			startActivity(intent);
       			return true;
       	
           }
        return false;
    }
}