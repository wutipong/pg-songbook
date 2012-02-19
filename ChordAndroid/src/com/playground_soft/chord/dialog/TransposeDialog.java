package com.playground_soft.chord.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.playground_soft.chord.R;

public class TransposeDialog
        extends DialogFragment
        implements OnClickListener {

    //private String[] mItems;
    private int[] mValues;
    private int mSelectedValue = 0;
    private boolean mIsSharp;
    private ToggleButton mSharpOrFlatButton;
    private ListView mListView;
    private EditText mEditSemitone;
    private Button mPlusButton, mMinusButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.transpose_dialog, container);

        mEditSemitone = (EditText) v.findViewById(R.id.edit_semitones);
        mEditSemitone.setText(Integer.toString(getValue()));
        mEditSemitone.setKeyListener(null);

        mSharpOrFlatButton = (ToggleButton) v
                .findViewById(R.id.button_sharp_or_flat);
        mSharpOrFlatButton.setChecked(mIsSharp);

        mPlusButton = (Button)v.findViewById(R.id.button_plus);
        mMinusButton = (Button)v.findViewById(R.id.button_minus);

        mPlusButton.setOnClickListener(this);
        mMinusButton.setOnClickListener(this);

        this.getDialog().setTitle("Transpose");

        return v;
    }

    public int getValue() {
        return mSelectedValue;
    }

    public void setValue(int mSelectedValue) {
        this.mSelectedValue = mSelectedValue;
    }

    public boolean isSharp() {
        return mSharpOrFlatButton.isChecked();
    }

    public void setIsSharp(boolean mIsSharp) {
        this.mIsSharp = mIsSharp;
    }

    @Override
    public void onClick(View v) {
        int value = getValue();

        if(v == mPlusButton) {
            value = value+1;
        } else if(v == mMinusButton) {
            value = value-1;
        }

        setValue(value);
        mEditSemitone.setText(Integer.toString(getValue()));

        mPlusButton.setEnabled(value != 11);
        mMinusButton.setEnabled(value != -11);
    }
}
