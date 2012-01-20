package org.badger.mr.music.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.badger.mr.music.R;
import org.badger.mr.music.library.Library;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadBrowser extends ListActivity {
	ProgressBar dlprogress;
	TextView activedownload;
	FileDownloader downloader;
	dlArrayAdapter<DownloadSong> downloadAdapter;
	public static final int STATUS_COMPLETE = 0;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_FAILED = 2;
	public static final int STATUS_PENDING = 3;
		
	
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setResult(Activity.RESULT_OK);
	        
	        setContentView(R.layout.download_browser);
	        dlprogress =  (ProgressBar)findViewById(R.id.downloadprogress);
	        dlprogress.setMax(100);
	        activedownload = (TextView) findViewById(R.id.active_download);
	        createList();
	        
	        
	    }
	 public void onDestroy() {
	        super.onDestroy();
	    }

	    @Override
	    public void onResume() {
	        super.onResume();
	        
	        if (downloader == null) {
	        	Log.i("DownloadBrowser","Creating new Downloader");
	        	//downloader = new FileDownloader(this,activedownload);
	        	downloader = new FileDownloader(this);
	        	downloader.execute((Void) null);
	        }
	        /**else if (downloader.status == FileDownloader.STATUS_IDLE) {
	        	Log.i("DownloadBrowser","Idle Downloader Rebuilding");
	        	downloader = new FileDownloader(this,dlprogress,activedownload);
	        	downloader.execute((Void) null);
	        }**/
	        
	      //  if (getListView() != null) {
	      //      getListView().clearTextFilter();
	      //  }
	        
	    }

	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (resultCode == Activity.RESULT_CANCELED) {
	            setResult(Activity.RESULT_CANCELED);
	            finish();
	        }
	    }
	    
	    public void progressupdate(int progress){
	    	dlprogress.setProgress(progress);
	    }
	    
	    public void setViews(){
	    	if ((downloader == null) || (downloader.status == FileDownloader.STATUS_IDLE)) {
	    		activedownload.setText(getString(R.string.noactivedownload));
	    		dlprogress.setProgress(0);
	    	}
	    	else {
	    		createList();
	    		
	    	}
	    	
	    }

	    private void createList() {
	        //downloadList = getListView();
	    	if (downloadAdapter == null) {
	    		downloadAdapter = new dlArrayAdapter<DownloadSong>(this,
	                    R.xml.long_list_text_view,Library.downloadList);
	    		setListAdapter(downloadAdapter);
	    	}
	    	else {
	    		downloadAdapter.notifyDataSetChanged();
	    	}
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
	    		ImageView imageView = (ImageView) rowView.findViewById(R.id.download_status_icon);
	    		DownloadSong s = myElements.get(position);
	    		textView.setText(s.toString());
	    		
	    		switch (s.status) {
	    			case DownloadSong.STATUS_INPROGRESS:
	    				activedownload.setText(s.toString());
	    				imageView.setImageResource(android.R.drawable.stat_sys_download);
	    				break;
	    			case DownloadSong.STATUS_COMPLETE:
	    				imageView.setImageResource(android.R.drawable.stat_sys_download_done);
	    				break;
	    			case DownloadSong.STATUS_CANCELLED:
	    				imageView.setImageResource(android.R.drawable.stat_notify_error);
	    				break;
	    			case DownloadSong.STATUS_FILELOCAL:
	    				imageView.setImageResource(android.R.drawable.stat_sys_download_done);
	    				break;
	    			case DownloadSong.STATUS_NOTSTARTED:
	    				imageView.setImageResource(android.R.drawable.stat_notify_sync_noanim);
	    				break;
	    			default:
	    				imageView.setImageResource(android.R.drawable.stat_notify_sync_noanim);
	    				break;
	    					
	    		}
	    		
	    		return rowView;
	    	}
	        
	    }
	        
	    class FileDownloader extends AsyncTask <Void, Integer, Integer>  {
	    	
	    	private String savePath;
	    	private Context parentContext;
	    	//private DownloadSong s;
	    	public int status;
	    	public static final int STATUS_IDLE = 0;
	    	public static final int STATUS_RUNNING = 1;
	    	
	    	
	    	public FileDownloader(Context c){
	    		status = STATUS_IDLE;
	    		parentContext = c;
	    		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(parentContext);
	    		savePath = mPrefs.getString("path_pref", parentContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString());
	    		//progressbar = pg;
	    		//activedownload = ad;
	    		
	    		
	    	}
	    	

	    	protected Integer doInBackground(Void... params) {
	    		// TODO Auto-generated method stub
	    		DownloadSong s;
	    		Log.i("FileDownloader","Starting File Downloader");
	    		int i = getNext();
	    		status = STATUS_RUNNING;
	    		
	    		
	    		while (i >= 0) {
	    			s = Library.downloadList.get(i);
	    			//progressbar.setProgress(0);
	    			//activedownload.setText(s.toString());
	    			s.status = DownloadSong.STATUS_INPROGRESS;
	    			
	    			publishProgress(0);
	    			if (s.isLocal) {
	    				s.status = DownloadSong.STATUS_FILELOCAL;
	    				Log.i("FileDownloader", s.name + " is already local");
	    				publishProgress(100);
	    			}
	    			else {
	    				String safeArtist = s.artist.replace('/', '_');
	    				String safeAlbum = s.album.replace("/", "_");
	    				String safeName = s.name.replace("/", "_");
	    				try {
	    			    	File directory = new File(savePath, safeArtist + "/" + safeAlbum);
	    			    	directory.mkdirs();
	    			    	File destination = new File(directory, s.track + "-" + safeName
	    			    			+ "." + s.format);
	    			    	InputStream songStream = Library.daapHost
	    						.getSongStream(s);
	    			    	FileOutputStream destinationStream = new FileOutputStream(
	    			    			destination);
	    			    	Log.i("FileDownloader","Saving " + destination.toString());
	    			    	Log.i("FileDownloader","Filesize: " + s.size);
	    			    	byte[] buffer = new byte[1024];
	    			    	int len;
	    			    	int bytesDl = 0;
	    			    	while ((len = songStream.read(buffer)) > 0) {
	    			    		destinationStream.write(buffer, 0, len);
	    			    		bytesDl += len;
	    			    		publishProgress((int) (bytesDl*100)/s.size);
	    			    		//Log.i("FileDownloader","Downloaded Pct: " + (int) (bytesDl*100)/s.size );
	    			    	}
	    			    	if (songStream != null)
	    			    		songStream.close();
	    			    	if (destinationStream != null)
	    			    	{
	    			    		destinationStream.close();
	    			    	}
	    			    	Log.i("FileDownloader","Saved " + bytesDl + "bytes");
	    			    	destination.deleteOnExit();
	    			    	s.status = DownloadSong.STATUS_COMPLETE;
	    			    	//Refresh the Mediastore
	    					//Add the local song to the media list
	    	
	    			    } 
	    			    catch (Exception e) {
	    			    	s.status = DownloadSong.STATUS_ERROR;
	    			    	e.printStackTrace();
	    			    }
	    				
	    			}
	    			i = getNext();
	    			
	    		}
	    		status = STATUS_IDLE;
	    		return null;
	    		
	    	}

	    @Override
	    	protected void onProgressUpdate(Integer... value) {
	    	   // TODO Auto-generated method stub
	    	   //dlprogress.setProgress(value[0]);
	    	progressupdate(value[0]);
	    	setViews();
	    	   //activedownload.setText(activetitle);
	    	  // Log.i("FileDownloader","Progress:" + value[0]);
	    	  }
	    	
	    	@SuppressWarnings("null")
	    	private int getNext(){
	    		int retVal = -1;
	    		for (int i = 0; i < Library.downloadList.size(); i = i + 1) { // Test and Loop
	    			   if (Library.downloadList.get(i).status == DownloadSong.STATUS_NOTSTARTED) {
	    				   retVal = i;
	    				   Log.i("DownloadSongs","Next Available Song: " + Library.downloadList.get(i).name + "Index " + i);
	    				   break;
	    			   }
	    			
	    		}
	    		return retVal;
	    				
	    	}
	    	// protected void onPostExecute(Long result) {
	        //     setViews();
	        // }

	    }

}
