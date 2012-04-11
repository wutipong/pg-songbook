package com.playground_soft.chord.about;

import com.actionbarsherlock.app.SherlockFragment;
import com.playground_soft.chord.R;

import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment 
extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.about_fragment, container);
        TextView txtVersion = (TextView)v.findViewById(R.id.txt_version);
        Resources res = getResources();
        String ver = "";
        try {
            ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String strVersion = res.getString(R.string.version).replace("%VERSION%", ver);
        txtVersion.setText(strVersion);
        
        getActivity().setTitle("About");
        
        return v;
    }
}
