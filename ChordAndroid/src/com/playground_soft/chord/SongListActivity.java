package com.playground_soft.chord;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SongListActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        setTitle(R.string.song_list_title);
    }
}
