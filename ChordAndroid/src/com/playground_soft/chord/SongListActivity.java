package com.playground_soft.chord;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SongListActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        setTitle(R.string.song_list_title);
    }
}
