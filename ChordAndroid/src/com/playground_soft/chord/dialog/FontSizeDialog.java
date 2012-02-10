package com.playground_soft.chord.dialog;

import com.playground_soft.chord.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FontSizeDialog 
extends DialogFragment
implements OnItemClickListener{
	private String[] mItems;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.list_dialog, container);
		Resources resources = getActivity().getResources();
		
		mItems = resources.getStringArray(R.array.pref_font_size_options_values);
		
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_single_choice,
						mItems);
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		String selected = preferences.getString(
				resources.getString(R.string.selected_font_size), "16");
		int index = 0;
		for ( ; index < mItems.length; index++) {
			if (mItems[index].equals(selected)) {
				break;
			}
		}
		ListView lv = (ListView) v.findViewById(R.id.list);
		lv.setAdapter(adapter);
		lv.setItemChecked(index, true);
		lv.setOnItemClickListener(this);

		getDialog().setTitle("Select Font Size");
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SharedPreferences preferences = 
				PreferenceManager.getDefaultSharedPreferences(this.getActivity());
		Resources resources = getActivity().getResources();
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(
				resources.getString(R.string.selected_font_size),
				mItems[arg2]);

		editor.commit();
		
	}
}
