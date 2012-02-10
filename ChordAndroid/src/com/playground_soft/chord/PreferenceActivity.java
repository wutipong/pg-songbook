package com.playground_soft.chord;

import android.os.Bundle;
import android.support.v4.app.SherlockPreferenceActivity;

public class PreferenceActivity extends SherlockPreferenceActivity {
	@Override
	public void onCreate(Bundle savesavedInstanceState) {
		super.onCreate(savesavedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}
	
}
