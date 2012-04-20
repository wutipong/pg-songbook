package com.playground_soft.chord.widget;

import com.playground_soft.chord.type.Song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;

public class SongListAdapter extends ArrayAdapter<Song>{

    public SongListAdapter(Context context,
            Song[] objects) {
        super(context, android.R.layout.simple_list_item_2, objects);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        TwoLineListItem row;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            row = (TwoLineListItem)
                    inflater.inflate(android.R.layout.simple_list_item_2, null);
        } else {
            row = (TwoLineListItem)convertView;
        }
       
        row.getText1().setText(getItem(position).name);
        row.getText2().setText(getItem(position).artist);
        
        return row;
    }

}
