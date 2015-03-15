package com.playground_soft.chord;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chord.type.Song;
import com.playground_soft.chord.widget.SongListAdapter;

public class SongListFragment extends android.app.ListFragment implements
        RefreshThread.OnFinishHandler {

    private DatabaseHelper mDbHelper;

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
        Intent intent = new Intent(getActivity(),
                SongDisplayActivity.class);
        
        Song song = (Song)getListAdapter().getItem(position);
        intent.setData(Uri.fromFile(song.file));
        intent.putExtra("iternal", true);
        
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

        Song mSongs[] = mDbHelper.querySong(artist, true, null, true);
        SongListAdapter adapter = new SongListAdapter(getActivity(), 
                mSongs);
        
        setListAdapter(adapter);
    }


    @Override
    public void onFinished() {
        updateSongList(null);

    }

}
