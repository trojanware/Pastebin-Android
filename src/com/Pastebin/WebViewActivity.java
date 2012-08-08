package com.Pastebin;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.*;

public class WebViewActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		WebView browser = new WebView(this);
		setContentView(browser);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null)
			browser.loadUrl(extras.getString("url"));	
	}
}
