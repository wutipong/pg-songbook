package com.playground_soft.chord.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.playground_soft.chord.R;

public class TransposeDialog extends android.app.DialogFragment implements
        View.OnClickListener, DialogInterface.OnClickListener {

    private int mSelectedValue = 0;
    private int mTempValue = 0;
    private boolean mIsSharp;
    private ToggleButton mSharpOrFlatButton;
    private EditText mEditSemitone;
    private Button mPlusButton, mMinusButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(this.getActivity());

        View v = inflater.inflate(R.layout.transpose_dialog, null);

        mEditSemitone = (EditText) v.findViewById(R.id.edit_semitones);
        mEditSemitone.setText(Integer.toString(mTempValue));
        mEditSemitone.setKeyListener(null);

        mSharpOrFlatButton = (ToggleButton) v
                .findViewById(R.id.button_sharp_or_flat);
        mSharpOrFlatButton.setChecked(mIsSharp);

        mPlusButton = (Button) v.findViewById(R.id.button_plus);
        mMinusButton = (Button) v.findViewById(R.id.button_minus);

        mPlusButton.setOnClickListener(this);
        mMinusButton.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity()).setView(v)
                .setTitle("Transpose")
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this).create();
    }

    public int getValue() {
        return mSelectedValue;
    }

    public void setValue(int mSelectedValue) {
        this.mSelectedValue = mSelectedValue;
        mTempValue = mSelectedValue;
    }

    public boolean isSharp() {
        return mSharpOrFlatButton.isChecked();
    }

    public void setIsSharp(boolean mIsSharp) {
        this.mIsSharp = mIsSharp;
    }

    @Override
    public void onClick(View v) {
        if (v == mPlusButton) {
            mTempValue = mTempValue + 1;
        } else if (v == mMinusButton) {
            mTempValue = mTempValue - 1;
        }

        mEditSemitone.setText(Integer.toString(mTempValue));

        mPlusButton.setEnabled(mTempValue != 11);
        mMinusButton.setEnabled(mTempValue != -11);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            setValue(mTempValue);
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            break;
        }

        dialog.dismiss();
    }
}
