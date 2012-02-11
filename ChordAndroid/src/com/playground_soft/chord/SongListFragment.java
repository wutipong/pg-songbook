package com.playground_soft.chord;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;


public class SongListFragment 
		extends ListFragment 
		implements ImportThread.OnFinishHandler{
	private SimpleCursorAdapter mAdapter;
	private DatabaseHelper mDbHelper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
	}

	@Override 
	public void onDestroy() {
		super.onDestroy();
		mDb.close();
		mDbHelper.close();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this.getActivity(),
				SongDisplayActivity.class);
		intent.putExtra("songid", id);
		this.startActivity(intent);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Intent intent = this.getActivity().getIntent();
		String artist = intent.getStringExtra("artist");
		mDbHelper = new DatabaseHelper(this.getActivity());
		mDb = mDbHelper.getReadableDatabase();
		this.updateSongList(artist);
		
	}
	
	public void updateSongList(String artist) {
		String condition = null;
		if (artist != null && !artist.equals("")) {
			condition = "artist='" + artist + "'";
		}
			
		Cursor cursor = mDb.query(
				"song", 
				new String[]{"_ROWID_ as _id", "name", "artist", "filename"}, 
				condition, 
				null, 
				null, 
				null, 
				"name");
		
		if (mAdapter == null) {
			mAdapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_list_item_2, 
					cursor, 
					new String[] {"name", "artist" }, 
					new int[] { android.R.id.text1,	android.R.id.text2 },
					0);
		} else {
			cursor = mAdapter.swapCursor(cursor);
			cursor.close();
		}
		
		setListAdapter(mAdapter);
	}
	
	private SQLiteDatabase mDb;
	@Override
	public void onFinished() {
		updateSongList(null);
		
	}

}
