package com.playground_soft.chord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.playground_soft.chord.dialog.FontSizeDialog;
import com.playground_soft.chord.dialog.ThemeListDialog;
import com.playground_soft.chord.dialog.TransposeDialog;
import com.playground_soft.chordlib.Document;

public class SongDisplayFragment 
        extends SherlockFragment 
        implements View.OnLayoutChangeListener{

    private Drawable mOutputDrawable;
    private Document mDocument;
    private int mTransposeValue = 0;
    private boolean mIsTransposeInSharp = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getActivity().getIntent();

        long songid = intent.getLongExtra("songid", 0);
        DatabaseHelper db = new DatabaseHelper(getActivity());
        String filename = db.getSongFileName(songid);
        db.close();

        File file = new File(filename);

        StringBuilder builder = new StringBuilder();

        try {
            FileInputStream stream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream));

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

            reader.close();
            stream.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mDocument = Document.create(builder.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.chord_fragment, container,
                false);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.song, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: {
            Intent intent = new Intent(this.getActivity(), MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
            break;
        }
        case R.id.menu_item_font_size: {
            FontSizeDialog dialog = new FontSizeDialog() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    super.onDismiss(dialog);
                    updateOutput();
                }
            };
            dialog.show(this.getFragmentManager(), "");
            break;
        }
        case R.id.menu_item_color_theme: {
            ThemeListDialog dialog = new ThemeListDialog() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                    super.onDismiss(dialog);
                    updateOutput();
                }
            };
            dialog.show(this.getFragmentManager(), "");
            break;
        }
        case R.id.menu_item_transpose: {
            TransposeDialog dialog = new TransposeDialog() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    super.onDismiss(dialog);
                    mTransposeValue = getValue();
                    mIsTransposeInSharp = isSharp();
                    updateOutput();
                }
            };
            dialog.setValue(mTransposeValue);
            dialog.setIsSharp(mIsTransposeInSharp);
            dialog.show(this.getFragmentManager(), "");
        }
        default:
            break;
        }
        updateOutput();

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();

        //updateOutput();
        View songdisp = getActivity().findViewById(R.id.song_display);
        songdisp.addOnLayoutChangeListener(this);
        
        this.getActivity().setTitle(
                String.format("%s by %s", mDocument.title, mDocument.subtitle));
    }

    private void updateOutput() {
        ImageView view = (ImageView) this.getActivity().findViewById(
                R.id.imageView1);
        View container = getActivity().findViewById(R.id.song_display);
        
        mOutputDrawable = SongFactory.create(mDocument, this.getActivity(),
                mTransposeValue, mIsTransposeInSharp, container.getWidth(), container.getHeight());
        view.setImageDrawable(mOutputDrawable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
            int arg4, int arg5, int arg6, int arg7, int arg8) {
        // TODO Auto-generated method stub
        updateOutput();
    }
}
