package org.badger.mr.music;



import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DownloaderService extends Service {

    public class LocalBinder extends Binder {
    	DownloaderService getService() {
            return DownloaderService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    
    @Override
    public IBinder onBind(Intent intent) {
    	
		Log.v("DownloaderService", "onBind called");
    	
		// Make sure we stay running
		startService(new Intent(this, DownloaderService.class));
    	
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
    
    }
}
