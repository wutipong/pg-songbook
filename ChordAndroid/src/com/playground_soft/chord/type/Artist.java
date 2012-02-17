package com.playground_soft.chord.type;

public class Artist {
    private final String mName;
    private final int mId;

    public Artist(String name, int id) {
        mName = name;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public String toString() {
        return mName;
    }
}
