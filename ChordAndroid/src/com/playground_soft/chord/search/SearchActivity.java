package com.playground_soft.chord.search;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.playground_soft.chord.SongDisplayActivity;
import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chord.type.Song;
import com.playground_soft.chord.widget.SongListAdapter;

public class SearchActivity extends SherlockListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            Song songs[] = dbHelper.querySong(query, false, query, false);
            SongListAdapter adapter = new SongListAdapter(this, songs);
            setListAdapter(adapter);
            
            setTitle("Search results for '"+query+"'");
        }
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this,
                SongDisplayActivity.class);
        Song song = (Song)getListAdapter().getItem(position);
        intent.setData(Uri.fromFile(song.file));
        intent.putExtra("internal", true);
        this.startActivity(intent);
    }
    
}
