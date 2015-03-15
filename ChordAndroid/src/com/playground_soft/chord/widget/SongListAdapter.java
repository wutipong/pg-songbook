package com.playground_soft.chord.widget;

import com.playground_soft.chord.R;
import com.playground_soft.chord.type.Song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongListAdapter extends ArrayAdapter<Song> {

    public SongListAdapter(Context context, Song[] objects) {
        super(context, android.R.layout.simple_list_item_2, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.simple_list_item, parent, false);
        } else {
            row = convertView;
        }

        ((TextView) row.findViewById(R.id.textView1))
                .setText(getItem(position).name);
        ((TextView) row.findViewById(R.id.textView2))
                .setText(getItem(position).artist);

        return row;
    }

}
