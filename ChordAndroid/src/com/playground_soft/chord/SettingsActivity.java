package com.playground_soft.chord;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.playground_soft.chord.R;


public class SettingsActivity extends SherlockPreferenceActivity {
    @Override 
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
    
    @Override
    public void onPause(){
        super.onPause();
    }
}
