package com.playground_soft.chord.about;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playground_soft.chord.R;

public class AboutFragment 
extends Fragment
implements OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.about_fragment, container);
        TextView txtVersion = (TextView)v.findViewById(R.id.txt_version);
        Resources res = getResources();
        String ver = "";
        try {
            ver = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0)
                    .versionName;
            
        } catch (NameNotFoundException e) {
           Log.e("about", "error getting version number", e);
        }
        String strVersion = res.getString(R.string.version).replace("%VERSION%", ver);
        txtVersion.setText(strVersion);
        
        getActivity().setTitle("About");
        
        v.findViewById(R.id.iv_facebook).setOnClickListener(this);
        v.findViewById(R.id.iv_google_plus).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch( view.getId()) {
        case R.id.iv_facebook:
            intent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("http://www.facebook.com/PlaygroundSoft"));
            break;
            
        case R.id.iv_google_plus:
            intent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("http://plus.google.com/113425280669457572765"));
            break;
            
        default:
            break;
        }
        if(intent != null)
            startActivity(intent);
        
    }
}
