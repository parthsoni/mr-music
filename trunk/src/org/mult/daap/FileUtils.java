package org.mult.daap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.Context;




import java.util.ArrayList;

public class FileUtils {
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    static void deleteIfExists(File file) {
        if (file != null) {
            if (file.exists() == true) {
                file.delete();
            }
        }
    }
    
}

