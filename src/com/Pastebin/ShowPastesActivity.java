package com.Pastebin;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import org.apache.http.client.entity.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.*;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.*;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
//import android.app.ProgressDialog;
import org.w3c.dom.*;
import android.widget.ArrayAdapter;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.os.AsyncTask;
import android.text.ClipboardManager;
import android.widget.Toast;



public class ShowPastesActivity extends ListActivity{
	
	static String paste_title[], paste_key[];	
	public final String api_dev_key = "fb290fe1a2743da163e6553c867bea19";
	int noPastes;
	String api_user_key;
	ArrayList pastes;
	ArrayAdapter<String> adapter;
	List<NameValuePair> params;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.showpasteslayout);
		
		paste_title = new String[50];
		paste_key = new String[50];
		noPastes = 0;
		api_user_key = userLoggedIn();			
		
		HttpClient client = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.pastebin.com/api/api_post.php");
		params = new ArrayList<NameValuePair>(3);
		params.add(new BasicNameValuePair("api_dev_key",api_dev_key));			
		params.add(new BasicNameValuePair("api_user_key",api_user_key));		
		params.add(new BasicNameValuePair("api_option","list"));
		params.add(new BasicNameValuePair("api_results_limit","50"));
		//ProgressDialog dialog = ProgressDialog.show(ShowPastesActivity.this, "", "Loading.Please Wait...");
		//try{
			
			//httppost.setEntity(new UrlEncodedFormEntity(params));			
			//HttpResponse response = client.execute(httppost);			
			//String responseBody = EntityUtils.toString(response.getEntity());			
			//dialog.cancel();
			//System.out.println("Response = "+responseBody);
			//responseBody = "<?xml version=\"1.0\"?><Pastes>\n"+responseBody+"\n</Pastes>";			
			//parseResponse(responseBody);
			//new ProgressTask(ShowPastesActivity.this,params,this);
			AsyncTask task = new ProgressTask(ShowPastesActivity.this,params,this).execute();
			System.out.println("Out of ProgrssTask!");
			/*if(noPastes!=0){
				pastes = new ArrayList();
				adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,pastes);
				setListAdapter(adapter);
				for(int i = 0;i<noPastes;i++)
					pastes.add(paste_title[i]);
				adapter.notifyDataSetChanged();
			}
			ListView lv = getListView();
			lv.setOnItemClickListener(new OnItemClickListener(){				
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					System.out.println("Clicked position = "+position);
					
					HttpPost httppost = new HttpPost("http://pastebin.com/raw.php?i="+paste_key[position]);	
					HttpClient client = new DefaultHttpClient();
					try{
						Toast.makeText(ShowPastesActivity.this, "Paste copied to clipboard", Toast.LENGTH_LONG).show();
						HttpResponse response = client.execute(httppost);
						String responseBody = EntityUtils.toString(response.getEntity());
						ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
						manager.setText(responseBody);						
						//ClipData clip = ClipData.newPlainText(responseBody);
					}catch(Exception e){
						System.out.println("Error while opening response: "+e.toString());
					}
					Intent i = new Intent(ShowPastesActivity.this,WebViewActivity.class);
					i.putExtra("url","http://pastebin.com/raw.php?i="+paste_key[position]);					
					startActivity(i);
				}
			});
		}catch(Exception e){
			System.err.println("Error while fetching pastes:"+e.toString());
		}*/
	}
	
	public String userLoggedIn(){
		SQLiteDatabase db;
		String api_user_key="", tblName = "usertoken", dbName = "PastebinDB";
		try{				
			db = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
			String col[] = {"name,api_dev_key"};
			Cursor res = db.query(tblName, col, null, null, null, null, null);			
			if(res.getCount()==0){
				System.out.println("not logged in");
				//api_user_key = null;
			}
			else{
				res.moveToNext();
				api_user_key = res.getString(1);
				System.out.println("logged in");
				System.out.println("userkey = "+api_user_key);				
			}
			db.close();
		}catch(Exception e){
			System.err.println("Error in boolean userLoggedIn : "+e.toString());			
		}			
		return api_user_key;
	}
	
	public void parseResponse(String resp){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(resp)));					
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("paste");
			noPastes = nList.getLength();
			
			for(int i = 0;i < noPastes;i++){
				Node node = nList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element)node;
					paste_title[i] = getTagValue("paste_title",eElement);
					paste_key[i] = getTagValue("paste_key",eElement);
					System.out.println("Title:"+paste_title[i]+"Key:"+paste_key[i]);
				}
			}
			
		}catch(Exception e){
			System.err.println("Error while parsing response: "+e.toString());
		}
	}
	
	public String getTagValue(String tagName, Element e){
		NodeList nList = e.getElementsByTagName(tagName).item(0).getChildNodes();
		Node n = (Node)nList.item(0);
		return n.getNodeValue();
	}
	
	private class ProgressTask extends AsyncTask<String,Void,Boolean>{
		private ProgressDialog dialog;
		private ListActivity activity; 
		private List<NameValuePair> params;
		String responseBody="";
		private ShowPastesActivity ActivityClass;
		public ProgressTask(ListActivity activity, List<NameValuePair> p, ShowPastesActivity c){			
			this.activity = activity;
			//dialog = new ProgressDialog(activity.getApplicationContext());
			params = p;
			ActivityClass = c;
		}
		
		@Override
		protected void onPreExecute(){			
			dialog = ProgressDialog.show(ShowPastesActivity.this, "", "Loading.Please Wait...");			
			System.out.println("out preexecute");
		}
		
		@Override
		protected void onPostExecute(final Boolean success){			
			responseBody = "<?xml version=\"1.0\"?><Pastes>\n"+responseBody+"\n</Pastes>";
			ActivityClass.parseResponse(responseBody);
			dialog.cancel();
			ActivityClass.conrt();
		}
		
		@Override
		protected Boolean doInBackground(final String... args){
			System.out.println("in doInBackgroud!");
			try{				
				HttpClient client = new DefaultHttpClient();			
				HttpPost httppost = new HttpPost("http://www.pastebin.com/api/api_post.php");			
				httppost.setEntity(new UrlEncodedFormEntity(ActivityClass.params));			
				HttpResponse response = client.execute(httppost);			
				responseBody = EntityUtils.toString(response.getEntity());			
			}catch(Exception e){
				System.out.println("Error while fetching pastes: ");				
			}			
			return true;
		}
	}
	
	
	public void conrt(){
		if(noPastes!=0){
			pastes = new ArrayList();
			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,pastes);
			setListAdapter(adapter);
			for(int i = 0;i<noPastes;i++)
				pastes.add(paste_title[i]);
			adapter.notifyDataSetChanged();
		}
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener(){				
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				System.out.println("Clicked position = "+position);
				
				HttpPost httppost = new HttpPost("http://pastebin.com/raw.php?i="+paste_key[position]);	
				HttpClient client = new DefaultHttpClient();
				try{
					Toast.makeText(ShowPastesActivity.this, "Paste copied to clipboard", Toast.LENGTH_LONG).show();
					HttpResponse response = client.execute(httppost);
					String responseBody = EntityUtils.toString(response.getEntity());
					ClipboardManager manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
					manager.setText(responseBody);						
					//ClipData clip = ClipData.newPlainText(responseBody);
				}catch(Exception e){
					System.out.println("Error while opening response: "+e.toString());
				}
				Intent i = new Intent(ShowPastesActivity.this,WebViewActivity.class);
				i.putExtra("url","http://pastebin.com/raw.php?i="+paste_key[position]);					
				startActivity(i);
			}
		});
	//}catch(Exception e){
		//System.err.println("Error while fetching pastes:"+e.toString());
	}
	
		
}
