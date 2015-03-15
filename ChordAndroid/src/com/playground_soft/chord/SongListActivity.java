package com.playground_soft.chord;

import android.app.Activity;
import android.os.Bundle;

public class SongListActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        setTitle(R.string.song_list_title);
    }
}
