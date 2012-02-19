package com.playground_soft.chord.dialog;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.playground_soft.chord.R;

public class FontSizeDialog extends DialogFragment implements
        OnSeekBarChangeListener{
    private SeekBar mSeekBar;
    private TextView mTextViewValue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.font_size_dialog, container);
        Resources resources = getActivity().getResources();

        mSeekBar = (SeekBar) v.findViewById(R.id.seek_bar_font_size);
        mTextViewValue = (TextView) v.findViewById(R.id.text_font_size_value);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());

        String selected = preferences.getString(
                resources.getString(R.string.selected_font_size), "16");
        mSeekBar.setProgress(Integer.parseInt(selected));
        mTextViewValue.setText(selected);

        mSeekBar.setOnSeekBarChangeListener(this);

        getDialog().setTitle("Font Size");
        return v;
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        Resources resources = getActivity().getResources();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(resources.getString(R.string.selected_font_size),
                Integer.toString(progress));

        mTextViewValue.setText(Integer.toString(progress));
        editor.commit();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }
}
