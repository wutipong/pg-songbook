package com.playground_soft.chord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.playground_soft.chord.about.AboutActivity;
import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chord.dialog.TransposeDialog;
import com.playground_soft.chord.widget.FrameLayout;
import com.playground_soft.chordlib.Document;

public class SongDisplayFragment extends Fragment implements
        FrameLayout.OnSizeChangedListener {

    private Drawable mOutputDrawable;
    private Document mDocument;
    private int mTransposeValue = 0;
    private boolean mIsTransposeInSharp = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getActivity().getIntent();

        Uri uri = intent.getData();
        assert (uri != null);

        File file = new File(uri.getPath());

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
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.song, menu);

        Intent intent = this.getActivity().getIntent();

        Uri uri = intent.getData();
        assert (uri != null);

        File file = new File(uri.getPath());
        if (file.getParentFile().equals(FileSystemUtils.INTERNAL_DIR)) {
            menu.removeItem(R.id.menu_item_add_to_library);
        }

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
            break;
        }
        case R.id.menu_item_settings: {
            Intent intent = new Intent(this.getActivity(),
                    SettingsActivity.class);
            startActivity(intent);
            break;
        }
        case R.id.menu_item_help: {
            Intent intent = new Intent(this.getActivity(), HelpActivity.class);
            this.startActivity(intent);
            break;
        }
        case R.id.menu_item_about: {
            Intent intent = new Intent(this.getActivity(), AboutActivity.class);
            this.startActivity(intent);
            break;
        }
        case R.id.menu_item_add_to_library: {
            Intent intent = this.getActivity().getIntent();

            Uri uri = intent.getData();
            assert (uri != null);

            File file = new File(uri.getPath());
            DatabaseHelper dbHelper = new DatabaseHelper(this.getActivity());

            try {
                FileSystemUtils.copyToLibrary(file, dbHelper);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dbHelper.close();

            new AlertDialog.Builder(getActivity())
                    .setTitle("File is added")
                    .setMessage(file.getName() + " is copied into the library.")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            }).show();
        }
        default:
            break;
        }
        // updateOutput();

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();

        FrameLayout songdisp = (FrameLayout) getActivity().findViewById(
                R.id.song_display);
        songdisp.setOnSizeChangedListener(this);
    }

    private void updateOutput() {
        ImageView view = (ImageView) this.getActivity().findViewById(
                R.id.imageView1);

        View container = getActivity().findViewById(R.id.song_display);

        mOutputDrawable = SongFactory.create(mDocument, this.getActivity(),
                mTransposeValue, mIsTransposeInSharp, container.getWidth(),
                container.getHeight());

        view.setImageDrawable(mOutputDrawable);
        this.getActivity().setTitle(
                String.format("%s by %s", mDocument.title, mDocument.subtitle));

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        Resources resource = getActivity().getResources();

        String theme = preferences.getString(
                resource.getString(R.string.selected_color_theme),
                "theme_monochrome");
        int id = resource.getIdentifier(theme, "array",
                "com.playground_soft.chord");
        TypedArray array = resource.obtainTypedArray(id);
        int mBackgroundColor = array.getColor(0, 0xFFFFFFFF);
        array.recycle();
        container.setBackgroundColor(mBackgroundColor);
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
    public void onResume() {
        super.onResume();
        updateOutput();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateOutput();

    }
}
