package com.sk.aceinstaller2.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.ContextCompat;

import com.sk.aceinstaller2.R;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by Saroj Kaashyap on 8/3/2017.
 */
public class Utils {


    public static boolean isTablet(Context context){
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static File getStoragePath() {
        File rootfolder = new File("/storage/");
        File fileList[] = new File("/storage/").listFiles();
        if(fileList!=null && fileList.length>0) {
            for (File file : fileList) {
                if (!file.getAbsolutePath().equalsIgnoreCase(getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead()) {
                    return file;
                }
            }
        }


        return getExternalStorageDirectory();
    }



    public static String getStoragepathString(Context context){
        //for pre-marshmallow versions
        String path = System.getenv("SECONDARY_STORAGE");

        // For Marshmallow, use getExternalCacheDirs() instead of System.getenv("SECONDARY_STORAGE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            File[] externalCacheDirs = context.getExternalCacheDirs();
            for (File file : externalCacheDirs) {
                if (Environment.isExternalStorageRemovable(file)) {
                    // Path is in format /storage.../Android....
                    // Get everything before /Android
                    path = file.getPath().split("/Android")[0];
                    break;
                }
            }
        }

        return  path;
    }



    public static ArrayList<File> getfiles(File dir) {
        ArrayList<File> fileList = new ArrayList<File>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                fileList.add(listFile[i]);
            }

        }
        return fileList;
    }

    public static int getFileCount(File dir) {
        int count = 0;
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                Boolean isDir = listFile[i].isDirectory();
                if (isDir) {
                    int recCount = getFileCount(new File(dir.toString().concat("/").concat(listFile[i].getName())));
                    count += recCount;
                } else {
                    count += 1;
                }
            }
        }

        return count;
    }

    public static ArrayList<File> getApkFiles(File dir) {
        ArrayList<File> fileList = new ArrayList<File>();
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getName().endsWith(".apk")) {
                    fileList.add(listFile[i]);
                }
            }

        }
        return fileList;
    }
}

