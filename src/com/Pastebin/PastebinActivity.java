package com.Pastebin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;

public class PastebinActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnCreateNewPaste = (Button)findViewById(R.id.btnCreate);
        btnCreateNewPaste.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v){
        		startActivity(new Intent(PastebinActivity.this, CreateNewPasteActivity.class));
        	}
        });
        
        Button btnPref = (Button)findViewById(R.id.btnPref);
        btnPref.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v){
        		startActivity(new Intent(PastebinActivity.this, PrefListView.class));
        	}
        });
        
        Button btnShowPastes = (Button)findViewById(R.id.btnShowPastes);
        btnShowPastes.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v){
        		startActivity(new Intent(PastebinActivity.this, ShowPastesActivity.class));
        	}
        });
    }    
    
}
