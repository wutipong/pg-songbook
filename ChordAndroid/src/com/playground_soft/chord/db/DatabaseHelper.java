package com.playground_soft.chord.db;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.playground_soft.chord.type.Artist;
import com.playground_soft.chord.type.Song;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String CREATE_SONGLIST_TABLE = "CREATE TABLE song ("
            + "name TEXT, " + "artist TEXT, " + "filename TEXT);";

    private static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE playlist ("
            + "name TEXT, " + "songrowid INTEGER);";

    private static final String DATABASE_NAME = "song_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SONGLIST_TABLE);
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
       
    public synchronized Song[] querySong(
            String searchArtist,
            boolean exactArtist,
            String searchSong,
            boolean exactSong) {
        
        List<Song> songList = new LinkedList<Song>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String condition = "";
        
        if (searchArtist != null && !searchArtist.equals("")) {
            if(exactArtist)
                condition = "artist='" + searchArtist + "'";
            else
                condition = "artist like '%" + searchArtist + "%'";
        }
        
        if (searchSong != null && !searchSong.equals("")) {
            if(!condition.isEmpty())
                condition = condition + " or ";
            if(exactSong)
                condition += "name='" + searchSong + "'";
            else
                condition += "name like '%"+searchSong + "%'";
        }
       
        Cursor cursor =  db.query("song", 
                new String[] { "_ROWID_ as _id", "name", "artist", "filename" }, 
                condition, null, null, null, "name");
        
        
        for( cursor.moveToFirst(); 
                !cursor.isAfterLast(); 
                cursor.moveToNext()) {
            
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String artist = cursor.getString(2);
            String file = cursor.getString(3);
            
            Song song = new Song(name, artist, new File(file), id );
            songList.add(song);
        }
        
        cursor.close();
        db.close();
        
        return songList.toArray(new Song[0]);
    }

    public synchronized boolean createOrUpdate(String name, String artist,
            String filename) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("song", null, "filename=" + "'" + filename
                + "'", null, null, null, null);

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("artist", artist);
        values.put("filename", filename);

        if (cursor.getCount() == 0) {
            db.insert("song", null, values);
        } else {
            db.update("song", values, "filename=" + "'" + filename + "'", null);
        }
        cursor.close();
        db.close();
        return true;
    }

    public synchronized boolean deleteAllSong() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("song", null, null);
        return true;
    }

    public synchronized Artist[] getArtistList() {

        ArrayList<Artist> artistList = new ArrayList<Artist>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("song", new String[] { "max(_ROWID_) as _id",
                "name", "artist", "filename" }, null, null, "artist", null,
                "artist", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(2);
                int id = cursor.getInt(0);

                Artist artist = new Artist(name, id);
                artistList.add(artist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artistList.toArray(new Artist[0]);
    }
}
