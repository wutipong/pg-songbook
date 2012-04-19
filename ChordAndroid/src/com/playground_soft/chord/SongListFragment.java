package com.playground_soft.chord;

import com.playground_soft.chord.type.Song;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SongListFragment extends ListFragment implements
        RefreshThread.OnFinishHandler {

    private DatabaseHelper mDbHelper;
    private Song[] mSongs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDbHelper.close();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this.getActivity(),
                SongDisplayActivity.class);
        
        //intent.putExtra("songid", mSongs[position].id);
        intent.setData(Uri.fromFile(mSongs[position].file));
        this.startActivity(intent);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = this.getActivity().getIntent();
        String artist = intent.getStringExtra("artist");
        mDbHelper = new DatabaseHelper(this.getActivity());

        this.updateSongList(artist);

    }

    public void updateSongList(String artist) {

        mSongs = mDbHelper.getSongs(artist, true, null, true);
        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(getActivity(), 
                android.R.layout.simple_list_item_1, 
                mSongs);
        
        setListAdapter(adapter);
    }


    @Override
    public void onFinished() {
        updateSongList(null);

    }

}
