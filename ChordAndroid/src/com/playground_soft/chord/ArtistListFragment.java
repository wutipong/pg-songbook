package com.playground_soft.chord;

import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.playground_soft.chord.about.AboutActivity;
import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chord.type.Artist;

public class ArtistListFragment extends ListFragment implements
        RefreshThread.OnFinishHandler {
    private ArrayAdapter<Artist> mAdapter;
    private DatabaseHelper mDbHelper;
    private SongListFragment mSonglistFragment;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String artistName;
        Artist artist = mAdapter.getItem(position);
        if (artist.getId() == -1) {
            artistName = null;
        } else {
            artistName = artist.getName();
        }

        if (mSonglistFragment != null) {
            mSonglistFragment.updateSongList(artistName);
        } else {
            Intent intent = new Intent(this.getActivity(),
                    SongListActivity.class);

            intent.putExtra("artist", artistName);
            this.startActivity(intent);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = this.getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mSonglistFragment = (SongListFragment) this.getFragmentManager()
                .findFragmentById(R.id.song_list_fragment);
        updateArtistList();
        lv.setItemChecked(0, true);
    }

    private void updateArtistList() {
        int layout = android.R.layout.simple_list_item_1;
        if (mSonglistFragment != null) {
            layout = android.R.layout.simple_list_item_activated_1;
        }
        mAdapter = new ArrayAdapter<Artist>(this.getActivity(), layout);

        mAdapter.add(new Artist("All", -1));

        mDbHelper = new DatabaseHelper(getActivity());

        for (Artist artist : mDbHelper.getArtistList()) {
            mAdapter.add(artist);
        }
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.menu_item_settings: {
            Intent intent = new Intent(this.getActivity(),
                    SettingsActivity.class);
            startActivity(intent);
            break;
        }

        case R.id.menu_item_synchronize: {
            RefreshThread thread = new RefreshThread(this.getActivity(), this);
            thread.start();
            break;
        }

        case R.id.menu_item_about: {
            Intent intent = new Intent(this.getActivity(), AboutActivity.class);
            this.startActivity(intent);
            break;
        }

        case R.id.menu_item_help: {
            Intent intent = new Intent(this.getActivity(), HelpActivity.class);
            this.startActivity(intent);
            break;
        }

        case R.id.menu_item_search: {
            getActivity().onSearchRequested();
            break;
        }

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);

        // Get the SearchView and set the search-able configuration

        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);

        View searchView = new SearchView(this.getActivity());
        // if search view is compatible
        if (searchView != null) {
            MenuItem item = menu.findItem(R.id.menu_item_search);
            item.setActionView(searchView);
            ((SearchView) searchView).setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
        }
    }

    @Override
    public void onFinished() {
        updateArtistList();
        SongListFragment songlistFragment = (SongListFragment) this
                .getFragmentManager().findFragmentById(R.id.song_list_fragment);

        if (songlistFragment != null) {
            songlistFragment.updateSongList(null);
        }
    }

}
