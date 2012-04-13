package com.playground_soft.chord;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class HelpFragment extends SherlockListFragment {
    class HelpEntry{
        public final String name;
        public final String url;
        
        public HelpEntry(String name, String url) {
            this.name = name;
            this.url = url;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
    }
    
    private ArrayAdapter<HelpEntry> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        HelpEntry entry = adapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.url));
        
        startActivity(intent);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        
        Activity activity = this.getActivity();
        Resources resources = activity.getResources();
        String[] items = resources.getStringArray(R.array.help_items);
        String[] urls = resources.getStringArray(R.array.help_urls);
        
        HelpEntry[] entries = new HelpEntry[items.length];
        for(int i = 0; i < entries.length; i++){
            entries[i] = new HelpEntry(items[i], urls[i]);
        }
        
        adapter = new ArrayAdapter<HelpEntry>(activity, android.R.layout.simple_list_item_1, entries);
        this.setListAdapter(adapter);
    }
}
