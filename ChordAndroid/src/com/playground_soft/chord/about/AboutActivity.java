package com.playground_soft.chord.about;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.playground_soft.chord.R;

public class AboutActivity 
extends SherlockFragmentActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.about_screen);
    }
}
