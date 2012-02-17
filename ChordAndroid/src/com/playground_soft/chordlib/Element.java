package com.playground_soft.chordlib;

public class Element {
    public enum Type {
        Text, Chord, Linebreak, StartOfChorus, EndOfChorus, StartOfTab, EndOfTab, Directive, Comment
    }

    public final Type type;
    public final String data;

    private Element(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public static Element create(Type type, String data) {
        Element e = new Element(type, data);
        return e;
    }
}
