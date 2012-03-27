package com.playground_soft.chord;

import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences settings = 
                PreferenceManager.getDefaultSharedPreferences(this);
        
        int lastRunVersion = settings.getInt("lastRunVersion", 0);
        boolean firstRun = settings.getBoolean("firstRun", true);
        
        int version = 0;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        if (version != lastRunVersion) {
            ArtistListFragment arf =
                    (ArtistListFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.artist_list_fragment);
            
            RefreshThread refreshThread = new RefreshThread(this, arf);
            refreshThread.start();
        }
        
        if (version != lastRunVersion ||
                firstRun == true) {
            SharedPreferences.Editor editor = settings.edit();
            if(firstRun) {
                editor.putBoolean("firstRun", false);
            }
            
            if(version != lastRunVersion) {
                editor.putInt("lastRunVersion", version);
            }
            
            editor.commit();
        }
    }
}
