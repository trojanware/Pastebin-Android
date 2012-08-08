package com.Pastebin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.*;
import android.os.AsyncTask;
import android.os.Bundle;
import org.apache.http.client.entity.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.*;
import org.apache.http.client.methods.HttpPost;
import java.util.*;

import android.widget.*;
import android.database.sqlite.*;
import android.content.*;
import android.app.AlertDialog;

public class Login extends Activity {
	
	public final String api_dev_key = "fb290fe1a2743da163e6553c867bea19";
	public final String dbName = "PastebinDB";
	public final String tblName = "usertoken";
	List<NameValuePair> params;
	String uname, password;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginview);					
	}
	
	public void LogMeIn(View v){
		
		
		EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
		EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
		
		uname = txtUsername.getText().toString();
		password = txtPassword.getText().toString();
		
		/*HttpClient client = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.pastebin.com/api/api_login.php");*/
		params = new ArrayList<NameValuePair>(5);
		params.add(new BasicNameValuePair("api_dev_key",api_dev_key));
		params.add(new BasicNameValuePair("api_user_name",uname));
		params.add(new BasicNameValuePair("api_user_password",password));
		AsyncTask task = new ProgressTask(Login.this,params,this).execute();
		//try{
			/*ProgressDialog dialog = ProgressDialog.show(Login.this, "", "Logging in.Please Wait...");
			httppost.setEntity(new UrlEncodedFormEntity(params));			
			HttpResponse response = client.execute(httppost);
			dialog.cancel();
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println("Response = "+responseBody);*/
			
			/*int ans = handleAPIError(responseBody);
			if(ans==-1)
				return;
			
			else{
				SQLiteDatabase myDatabase;
				try{
					myDatabase = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
					myDatabase.execSQL("insert into usertoken values (1,\""+uname+"\",\""+responseBody+"\");");					
					myDatabase.close();
					setResult(RESULT_OK);
					finish();
				}catch(Exception e){
					System.err.println(e.toString());
				}
			}
		}catch(Exception e){
			System.err.println("Error : "+e.toString());
			txtUsername.setText(e.toString());
		}*/
			
	}
	
	public int handleAPIError(String resp){
		if(resp.compareTo("Bad API request, invalid login")==0){
			showAlertMessage("Error logging in!\nPlease check username and password");
			return -1;
		}
		
		else if(resp.compareTo("Bad API request, account not active")==0){
			showAlertMessage("Error logging in!\nYour Pastebin account is not active!");
			return -1;
		}
		
		else
			return 0;
	}
	
	public void showAlertMessage(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {						
				dialog.cancel();						
			}
		});		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private class ProgressTask extends AsyncTask<String,Void,Boolean>{
		private ProgressDialog dialog;
		private Activity activity; 
		private List<NameValuePair> params;
		String responseBody="";
		private Login ActivityClass;
		public ProgressTask(Activity activity, List<NameValuePair> p, Login c){			
			this.activity = activity;			
			params = p;
			ActivityClass = c;
		}
		
		@Override
		protected void onPreExecute(){			
			dialog = ProgressDialog.show(Login.this, "", "Logging In.Please Wait...");						
		}
		
		@Override
		protected void onPostExecute(final Boolean success){			
			
			dialog.cancel();
			int ans = ActivityClass.handleAPIError(responseBody);
			if(ans==-1)
				return;
			
			else{
				SQLiteDatabase myDatabase;
				try{
					myDatabase = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
					myDatabase.execSQL("insert into usertoken values (1,\""+ActivityClass.uname+"\",\""+responseBody+"\");");					
					myDatabase.close();
					setResult(RESULT_OK);
					finish();
				}catch(Exception e){
					System.err.println(e.toString());
				}
			}
		}
		
		
		@Override
		protected Boolean doInBackground(final String... args){
			System.out.println("in doInBackgroud!");
			try{				
				HttpClient client = new DefaultHttpClient();			
				HttpPost httppost = new HttpPost("http://www.pastebin.com/api/api_login.php");			
				httppost.setEntity(new UrlEncodedFormEntity(ActivityClass.params));			
				HttpResponse response = client.execute(httppost);			
				responseBody = EntityUtils.toString(response.getEntity());			
			}catch(Exception e){
				System.out.println("Error while fetching pastes: ");				
			}			
			return true;
		}
	}

}
