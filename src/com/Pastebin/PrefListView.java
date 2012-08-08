package com.Pastebin;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.util.ArrayList;
import android.app.Activity;
import android.database.*;
import android.database.sqlite.*;
import android.content.*;

public class PrefListView extends Activity {
	ArrayList options;
	ArrayAdapter<String> adapter;
	public final String dbName = "PastebinDB";
	public final String tblName = "usertoken";
	public final String qCreate = "create table "+tblName+"( id integer, name varchar,api_dev_key varchar );";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);			
		
		SQLiteDatabase myDatabase;
		try{
			myDatabase = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
			//myDatabase.close();
			myDatabase.execSQL(qCreate);
			setContentView(R.layout.prefviewlogin);
			myDatabase.close();
		}catch(SQLiteException e){			
			myDatabase = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
			//myDatabase.execSQL("insert into usertoken values (1,\"trojanware\",\"123456\");");
			String query = "select * from usertoken;";
			String col[] = {"name,api_dev_key"};
			Cursor res = myDatabase.query(tblName, col, null, null, null, null, null);
			if(res.getCount()==0)
				setContentView(R.layout.prefviewlogin);
			else{
				setContentView(R.layout.preflistview);
				System.out.println("Count = "+res.getCount());
				TextView txtUname = (TextView)findViewById(R.id.txtUname);				
				res.moveToNext();				
				txtUname.setText("\tLogged in as "+res.getString(0));
				//myDatabase.endTransaction();
				myDatabase.close();
				//addDefaultControls();
			}
		}
		
		/*options = new ArrayList();
		//options.add(getResources().getStringArray(R.array.pref_list).toString());
		options.add("Login");		
		//adapter = new ArrayAdapter<String>(this,R.layout.preflistview,options);
		//adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options);
		//setListAdapter(adapter);
		
		ListView lv = getListView();
		//lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				if(position==0){
					options.remove(0);
					options.add("Logout trojanware");
					options.add("Logout");
					adapter.notifyDataSetChanged();
				}
			}
		});*/
		
	}
	
	public void logout(View v){
		SQLiteDatabase myDatabase = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
		myDatabase.delete(tblName,null,null);
		setContentView(R.layout.prefviewlogin);			
		
		myDatabase.close();
	}
	
	public void login(View v){
		//startActivity(new Intent(PrefListView.this,Login.class));
		startActivityForResult(new Intent(PrefListView.this,Login.class),1);						
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode==RESULT_OK)
			startActivity(getIntent());finish(); //Reload					
	}
	
	/*public void addDefaultControls(){
		//SQLiteDatabase db;
		Spinner spnnrPrivacy = (Spinner)findViewById(R.id.spnnrDefaultsExpiry);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.defaults_expiry,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnrPrivacy.setAdapter(adapter);
		
		Spinner spnnrExpiry = (Spinner)findViewById(R.id.spnnrDefaultsPrivacy);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.defaults_privacy,android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnrExpiry.setAdapter(adapter1);		
	}*/
	
	public void savePrivacySettings(View v){
		//Spinner spnnr = (Spinner)findViewById(R.id.spnnrDefaults_Privacy);
		/*Spinner spnnr = (Spinner)v;
		long a = spnnr.getSelectedItemId();
		//AdapterView<SpinnerAdapter> t;		
		System.out.println("spnnr val = "+spnnr.getItemAtPosition((int)a));*/
	}

}
