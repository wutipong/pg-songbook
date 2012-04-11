package com.playground_soft.chord.pref;

import com.playground_soft.chord.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class FontSizePreference 
extends DialogPreference
implements OnSeekBarChangeListener{

    private TextView mTextViewValue;

    public FontSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.font_size_dialog);
    }
    
    @Override
    protected void onBindDialogView (View view){
        SharedPreferences preferences = this.getSharedPreferences();
        String key = this.getKey();
        String defValue = "24";
        
        String value = preferences.getString(key, defValue);
        int iValue = Integer.parseInt(value);
        mTextViewValue = (TextView)view.findViewById(R.id.text_font_size_value);
        mTextViewValue.setText(value);
        
        SeekBar mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar_font_size);
        mSeekBar.setProgress(iValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        
        setNegativeButtonText(android.R.string.cancel);
        setPositiveButtonText(android.R.string.ok);
        
    }
    
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult) {
        
            SharedPreferences preferences = this.getSharedPreferences();
    
            SharedPreferences.Editor editor = preferences.edit();
            String value =  mTextViewValue.getText().toString();
            editor.putString(getKey(), value);
    
            editor.commit();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        mTextViewValue.setText(Integer.toString(progress));   
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
