package org.badger.mr.music;

import android.app.Application;
import android.content.Context;

public class MrMusic extends Application{

    public static Context context;
    

    public void onCreate(){
        MrMusic.context=getApplicationContext();
    }

    public static Context getAppContext(){
    	return context;
    }
   

}
