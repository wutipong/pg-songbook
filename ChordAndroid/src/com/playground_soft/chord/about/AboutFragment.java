package com.playground_soft.chord.about;

import com.actionbarsherlock.app.SherlockFragment;
import com.playground_soft.chord.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment 
extends SherlockFragment
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
            ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/PlaygroundSoft"));
            break;
            
        case R.id.iv_google_plus:
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/b/113425280669457572765/"));
            break;
            
        default:
            break;
        }
        if(intent != null)
            startActivity(intent);
        
    }
}
