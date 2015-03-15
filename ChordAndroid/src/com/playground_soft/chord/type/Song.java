package com.playground_soft.chord.type;

import java.io.File;

public class Song {
    public final String name;
    public final String artist;
    public final File file;
    public final int id;

    public Song(String name, String artist, File file, int id) {
        this.name = name;
        this.artist = artist;
        this.file = file;
        this.id = id;
    }

    @Override
    public String toString() {
        return name + "//" + artist;
    }
}
