package com.playground_soft.chord.dialog;

import com.playground_soft.chord.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutDialog extends DialogFragment {
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.about_dialog, container);
		
		getDialog().setTitle("About");
		return v;
	}
}
