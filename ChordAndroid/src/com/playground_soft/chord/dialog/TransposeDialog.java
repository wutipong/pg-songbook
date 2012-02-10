package com.playground_soft.chord.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.playground_soft.chord.R;

public class TransposeDialog extends DialogFragment implements
		OnItemClickListener {

	private String[] mItems;
	private int[] mValues;
	private int mSelectedValue = 0;
	private boolean mIsSharp;
	private ToggleButton mSharpOrFlatButton;
	private ListView mListView;
	
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.transpose_dialog, container);
		Resources resources = getActivity().getResources();
		
		mItems = resources.getStringArray(R.array.transpose_options);
		
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_single_choice,
						mItems);
		
		mValues = resources.getIntArray(R.array.transpose_values);
		int index = 0;
		for ( ; index < mItems.length; index++) {
			if (mValues[index] == getValue()) {
				break;
			}
		}
		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setAdapter(adapter);
		mListView.setItemChecked(index, true);
		mListView.setOnItemClickListener(this);

		mSharpOrFlatButton = (ToggleButton) v.findViewById(R.id.button_sharp_or_flat);
		mSharpOrFlatButton.setChecked(mIsSharp);
		
		this.getDialog().setTitle("Transpose");
		
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		setValue(mValues[arg2]);
		mListView.setItemChecked(arg2, true);
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

}
