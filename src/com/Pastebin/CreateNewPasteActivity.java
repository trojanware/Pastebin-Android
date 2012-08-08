package com.Pastebin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.*;
import org.apache.http.client.methods.HttpPost;
import android.text.SpannableStringBuilder;
import android.database.*;
import android.database.sqlite.*;
import android.content.*;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.view.View.OnFocusChangeListener;
import android.text.style.ForegroundColorSpan;


public class CreateNewPasteActivity extends Activity {
	public final String api_dev_key = "fb290fe1a2743da163e6553c867bea19";
	public final String dbName = "PastebinDB";
	public final String tblName = "usertoken";
	String api_user_key = "";
	List<NameValuePair> params;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.newpastelayout);		
		
		EditText txt;
		txt = (EditText)findViewById(R.id.txtPasteBody);
		txt.setGravity(Gravity.TOP);
					
		Spinner spnnrPrivacy = (Spinner)findViewById(R.id.spnnrPrivacy);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.privacy_options,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnrPrivacy.setAdapter(adapter);
		spnnrPrivacy.requestFocus();
		
		Spinner spnnrExpiry = (Spinner)findViewById(R.id.spnnrExpiry);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.expiry_options,android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnrExpiry.setAdapter(adapter1);		
		
		Spinner spnnrLanguage = (Spinner)findViewById(R.id.spnnrLanguage);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.languages_option,android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnnrLanguage.setAdapter(adapter2);
		
		final EditText txtTitle = (EditText)findViewById(R.id.txtTitle);						
		txtTitle.setText("Title(Optional)");
		txtTitle.setTextColor(Color.GRAY);
		txtTitle.setOnFocusChangeListener(new OnFocusChangeListener() {			
			
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(txtTitle.getText().toString().equals("Title(Optional)")){
						txtTitle.setText("");						
						txtTitle.setTextColor(Color.BLACK);
					}
				}
				else{
					if(txtTitle.getText().toString().equals("")){
						txtTitle.setTextColor(Color.GRAY);
						txtTitle.setText("Title(Optional)");
					}
				}
			}
		});
		
		final EditText txtBody = (EditText)findViewById(R.id.txtPasteBody);
		txtBody.clearFocus();
		txtBody.setTextColor(Color.GRAY);
		txtBody.setText("Paste Body");
		txtBody.setOnFocusChangeListener(new OnFocusChangeListener() {			
			//@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(txtBody.getText().toString().equals("Paste Body")){
						txtBody.setText("");
						txtBody.setTextColor(Color.BLACK);
					}
				}
				else{
					if(txtBody.getText().toString().equals("")){
						txtBody.setText("Paste Body");
						txtBody.setTextColor(Color.GRAY);
					}
				}
			}
		});
	}
	
	public void handleTxtClick(View v){
		EditText txt;
		String srcText;
		//SpannableStringBuilder builder = new SpannableStringBuilder();
		//builder.setSpan(new ForegroudColorSpan(Color.BLACK), 0,0);
		
		switch(v.getId()){
		case R.id.txtTitle:
			txt = (EditText)findViewById(R.id.txtTitle);
			srcText = txt.getText().toString();
			if(srcText.compareTo("Title(Optional)")==0)
				txt.selectAll();
			
			break;
		case R.id.txtPasteBody:
			txt = (EditText)findViewById(R.id.txtPasteBody);
			srcText = txt.getText().toString();
			if(srcText.equals("Paste Body"))
				txt.selectAll();
			break;
		}
	}
	
	public void createPaste(View v){
		System.out.println("In create paste");
		String privacy_flag = "1";	//private = 1
		String str_expiry="",str_lang="", url="http://pastebin.com/api/api_post.php";		
		String str_pasteBody;
		boolean loggedIn;
		Spinner spnnrPrivacy = (Spinner)findViewById(R.id.spnnrPrivacy);
		Spinner spnnrExpiry = (Spinner)findViewById(R.id.spnnrExpiry);
		Spinner spnnrLanguage = (Spinner)findViewById(R.id.spnnrLanguage);
		EditText txtPastebody = (EditText)findViewById(R.id.txtPasteBody);
		EditText txtTitle = (EditText)findViewById(R.id.txtTitle);					
				
		str_pasteBody = txtPastebody.getText().toString();
		if(str_pasteBody.length()==0||str_pasteBody.compareTo("Paste Body")==0){
			showAlertMessage("Paste contents cannot be empty!");
			return;
		}
		
		int retID = (int)spnnrPrivacy.getSelectedItemId();
		System.out.println("retID privacy = "+retID);
		if(retID==0)	//If privacy = public
			privacy_flag="0";
		
		loggedIn = userLoggedIn();
		if(!loggedIn&&privacy_flag.compareTo("2")==0){
			showAlertMessage("You are not logged in!\nPlease login to create a private post");
			spnnrPrivacy.setSelection(1);
			
			System.out.println("not logged in prompt");			
			return;
		}
			
		
		
		retID = (int)spnnrExpiry.getSelectedItemId();
		switch(retID){
		case 0:
			str_expiry="N";
			break;
		case 1:
			str_expiry="10M";
			break;
		case 2:
			str_expiry="1H";
			break;
		case 3:
			str_expiry="1D";
			break;
		case 4:
			str_expiry="1M";
			break;
		}
		
		retID = (int)spnnrLanguage.getSelectedItemId();
		switch(retID){
		case 1:
			str_lang = "4cs";
			break;
		case 2:
			str_lang = "6502acme";
			break;
		case 3:
			str_lang = "6502kickass";
			break;
		case 4:
			str_lang = "6502tasm";
			break;
		case 5:
			str_lang = "abap";
			break;
		case 6:
			str_lang = "actionscript";
			break;
		case 7:
			str_lang = "actionscript3";
			break;
		case 8:
			str_lang = "ada";
			break;
		case 9:
			str_lang = "algol68";
			break;
		case 10:
			str_lang = "apache";
			break;
		case 11:
			str_lang = "applescript";
			break;
		case 12:
			str_lang = "apt_sources";
			break;
		case 13:
			str_lang = "asm";
			break;
		case 14:
			str_lang = "asp";
			break;
		case 15:
			str_lang = "autoconf";
			break;
		case 16:
			str_lang = "autohotkey";
			break;
		case 17:
			str_lang = "autoit";
			break;
		case 18:
			str_lang = "avisynth";
			break;
		case 19:
			str_lang = "awk";
			break;
		case 20:
			str_lang = "bascomavr";
			break;
		case 21:
			str_lang = "bash";
			break;
		case 22:
			str_lang = "basic4gl";
			break;
		case 23:
			str_lang = "bibtex";
			break;
		case 24:
			str_lang = "blitzbasic";
			break;
		case 25:
			str_lang = "bnf";
			break;
		case 26:
			str_lang = "boo";
			break;
		case 27:
			str_lang = "bf";
			break;
		case 28:
			str_lang = "c";
			break;
		case 29:
			str_lang = "c_mac";
			break;
		case 30:
			str_lang = "cil";
			break;
		case 31:
			str_lang = "csharp";
			break;
		case 32:
			str_lang = "cpp";
			break;
		case 33:
			str_lang = "cpp-qt";
			break;
		case 34:
			str_lang = "c_loadrunner";
			break;
		case 35:
			str_lang = "caddcl";
			break;
		case 36:
			str_lang = "cadlisp";
			break;
		case 37:
			str_lang = "cfdg";
			break;
		case 38:			
			str_lang = "chaiscript";
			break;
		case 39:
			str_lang = "clojure";
			break;
		case 40:
			str_lang = "klonec";
			break;
		case 41:
			str_lang = "klonecpp";
			break;
		case 42:
			str_lang = "cmake";
			break;
		case 43:
			str_lang = "cobol";
			break;
		case 44:
			str_lang = "coffeescript";
			break;
		case 45:
			str_lang = "cfm";
			break;
		case 46:
			str_lang = "css";
			break;
		case 47:
			str_lang = "cuesheet";
			break;
		case 48:
			str_lang = "d";
			break;
		case 49:
			str_lang = "dcs";
			break;
		case 50:
			str_lang = "delphi";
			break;
		case 51:
			str_lang = "oxygene";
			break;
		case 52:			
			str_lang = "diff";
			break;
		case 53:
			str_lang = "div";
			break;
		case 54:
			str_lang = "dos";
			break;
		case 55:
			str_lang = "dot";
			break;
		case 56:
			str_lang = "e";
			break;
		case 57:
			str_lang = "ecmascript";
			break;
		case 58:
			str_lang = "eiffel";
			break;
		case 59:
			str_lang = "email";
			break;
		case 60:			
			str_lang = "epc";
			break;
		case 61:
			str_lang = "erlang";
			break;
		case 62:
			str_lang = "fsharp";
			break;
		case 63:
			str_lang = "falcon";
			break;
		case 64:
			str_lang = "fo";
			break;
		case 65:
			str_lang = "f1";
			break;
		case 66:			
			str_lang = "fortran";
			break;
		case 67:
			str_lang = "freebasic";
			break;
		case 68:
			str_lang = "freeswitch";
			break;
		case 69:
			str_lang = "gambas";
			break;
		case 70:
			str_lang = "gml";
			break;
		case 71:
			str_lang = "gdb";
			break;
		case 72:
			str_lang = "genero";
			break;
		case 73:
			str_lang = "genie";
			break;
		case 74:
			str_lang = "gettext";
			break;
		case 75:
			str_lang = "go";
			break;
		case 76:			
			str_lang = "groovy";
			break;
		case 77:
			str_lang = "gwbasic";
			break;
		case 78:
			str_lang = "haskell";
			break;
		case 79:
			str_lang = "hicest";
			break;
		case 80:
			str_lang = "hq9plus";
			break;
		case 81:
			str_lang = "html4strict";
			break;
		case 82:
			str_lang = "html5";
			break;
		case 83:
			str_lang = "icon";
			break;
		case 84:
			str_lang = "idl";
			break;
		case 85:
			str_lang = "ini";
			break;
		case 86:			
			str_lang = "inno";
			break;
		case 87:
			str_lang = "intercal";
			break;
		case 88:
			str_lang = "io";
			break;
		case 89:
			str_lang = "j";
			break;
		case 90:
			str_lang = "java";
			break;
		case 91:
			str_lang = "java5";
			break;
		case 92:
			str_lang = "javascript";
			break;
		case 93:
			str_lang = "jquery";
			break;
		case 94:
			str_lang = "kixtart";
			break;
		case 95:
			str_lang = "latex";
			break;
		case 96:
			str_lang = "lb";
			break;
		case 97:
			str_lang = "lsl2";
			break;
		case 98:
			str_lang = "lisp";
			break;
		case 99:
			str_lang = "llvm";
			break;
		case 100:
			str_lang = "locobasic";
			break;
		case 101:
			str_lang = "logtalk";
			break;
		case 102:
			str_lang = "lolcode";
			break;
		case 103:
			str_lang = "lotusformulas";
			break;
		case 104:
			str_lang = "lotusscript";
			break;
		case 105:
			str_lang = "lscript";
			break;
		case 106:
			str_lang = "lua";
			break;
		case 107:
			str_lang = "m68k";
			break;
		case 108:
			str_lang = "magiksf";
			break;
		case 109:
			str_lang = "make";
		case 110:
			str_lang = "mapbasic";
			break;
		case 111:
			str_lang = "matlab";
			break;
		case 112:
			str_lang = "mirc";
			break;
		case 113:
			str_lang = "mmix";
			break;
		case 114:
			str_lang = "modula2";
			break;
		case 115:
			str_lang = "modula3";
			break;
		case 116:
			str_lang = "68000devpac";
			break;
		case 117:
			str_lang = "mpasm";
			break;
		case 118:
			str_lang = "mxml";
			break;
		case 119:
			str_lang = "mysql";
			break;
		case 120:
			str_lang = "newlisp";
			break;
		case 121:
			str_lang = "nsis";
			break;
		case 122:
			str_lang = "oberon2";
			break;
		case 123:
			str_lang = "objeck";
			break;
		case 124:
			str_lang = "objc";
			break;
		case 125:
			str_lang = "ocaml-brief";
			break;
		case 126:
			str_lang = "ocaml";
			break;
		case 127:
			str_lang = "pf";
			break;
		case 128:
			str_lang = "glsl";
			break;
		case 129:
			str_lang = "oobas";
			break;
		case 130:
			str_lang = "oracle11";
			break;
		case 131:
			str_lang = "oracle8";
			break;
		case 132:
			str_lang = "oz";
			break;
		case 133:
			str_lang = "pascal";
			break;
		case 134:
			str_lang = "pawn";
			break;
		case 135:
			str_lang = "pcre";
			break;
		case 136:
			str_lang = "per";
			break;
		case 137:
			str_lang = "perl";
			break;
		case 138:
			str_lang = "perl6";
			break;
		case 139:
			str_lang = "php";
			break;
		case 140:
			str_lang = "php-brief";
			break;
		case 141:
			str_lang = "pic16";
			break;
		case 142:
			str_lang = "pike";
			break;
		case 143:
			str_lang = "pixelbender";
			break;
		case 144:
			str_lang = "plsql";
			break;
		case 145:
			str_lang = "postgresql";
			break;
		case 146:
			str_lang = "povray";
			break;
		case 147:
			str_lang = "powershell";
			break;
		case 148:
			str_lang = "powerbuilder";
			break;
		case 149:
			str_lang = "proftpd";
			break;
		case 150:
			str_lang = "progress";
			break;
		case 151:			
			str_lang = "prolog";
			break;
		case 152:
			str_lang = "properties";
			break;
		case 153:
			str_lang = "providex";
			break;
		case 154:
			str_lang = "purebasic";
			break;
		case 155:
			str_lang = "pycon";
			break;
		case 156:
			str_lang = "python";
			break;
		case 157:
			str_lang = "q";
			break;
		case 158:
			str_lang = "qbasic";
			break;
		case 159:
			str_lang = "rsplus";
			break;
		case 160:
			str_lang = "rails";
			break;
		case 161:
			str_lang = "rebol";
			break;
		case 162:
			str_lang = "reg";
			break;
		case 163:
			str_lang = "robots";
			break;
		case 164:
			str_lang = "rpmspec";
			break;
		case 165:
			str_lang = "ruby";
			break;
		case 166:
			str_lang = "gnuplot";
			break;
		case 167:
			str_lang = "sas";
			break;
		case 168:
			str_lang = "scala";
			break;
		case 169:
			str_lang = "scheme";
			break;
		case 170:
			str_lang = "scilab";
			break;
		case 171:
			str_lang = "sdlbasic";
			break;
		case 172:
			str_lang = "smalltalk";
			break;
		case 173:			
			str_lang = "smarty";
			break;
		case 174:
			str_lang = "sql";
			break;
		case 175:
			str_lang = "systemverilog";
			break;
		case 176:
			str_lang = "tsql";
			break;
		case 177:
			str_lang = "tcl";
			break;
		case 178:
			str_lang = "teraterm";
			break;
		case 179:
			str_lang = "thinbasic";
			break;
		case 180:
			str_lang = "typoscript";
			break;
		case 181:
			str_lang = "unicon";
			break;
		case 182:
			str_lang = "uscript";
			break;
		case 183:
			str_lang = "vala";
			break;
		case 184:
			str_lang = "vbnet";
			break;
		case 185:
			str_lang = "verilog";
			break;
		case 186:
			str_lang = "vhdl";
			break;
		case 187:
			str_lang = "vim";
			break;
		case 188:
			str_lang = "visualprolog";
			break;
		case 189:
			str_lang = "vb";
			break;
		case 190:
			str_lang = "visualfoxpro";
			break;
		case 191:
			str_lang = "whitespace";
			break;
		case 192:
			str_lang = "whois";
			break;
		case 193:
			str_lang = "winbatch";
			break;
		case 194:
			str_lang = "xbasic";
			break;
		case 195:
			str_lang = "xml";
			break;
		case 196:
			str_lang = "xorg_conf";
			break;
		case 197:
			str_lang = "xpp";
			break;
		case 198:
			str_lang = "yaml";
			break;
		case 199:
			str_lang = "z80";
			break;
		case 200:
			str_lang = "zxbasic";
			break;			
		}
		
		/*HttpClient client = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);*/
		params = new ArrayList<NameValuePair>(5);
		params.add(new BasicNameValuePair("api_dev_key",api_dev_key));
		params.add(new BasicNameValuePair("api_option","paste"));
		params.add(new BasicNameValuePair("api_paste_code",str_pasteBody));		
		
		if(loggedIn&&privacy_flag.compareTo("2")==0){	//Associate the paste with the user if logged in and privacy=private
			params.add(new BasicNameValuePair("api_user_key",api_user_key));
			params.add(new BasicNameValuePair("api_paste_private","2"));
			System.out.println("param privacy flag = 2");
		}
		else{
			if(loggedIn){
				params.add(new BasicNameValuePair("api_paste_private","0"));
				System.out.println("param privacy flag = 0");
			}
			else{
				
			}
		}
		
		String str_title = txtTitle.getText().toString();	//Title
		if(str_title.length()!=0||str_title.compareTo("Title(Optional")!=0){
			params.add(new BasicNameValuePair("api_paste_name",str_title));
			System.out.println("param title = "+str_title);
		}
		
		if(str_lang.length()!=0){	//Highlight mode
			params.add(new BasicNameValuePair("api_paste_format",str_lang));
			System.out.println("param highlight mode = "+str_lang);
		}
		
		params.add(new BasicNameValuePair("api_paste_expire_date",str_expiry));
		System.out.println("param expiry date = "+str_expiry);
		AsyncTask task = new ProgressTask(CreateNewPasteActivity.this,params,this).execute();		
		try{
			/*httppost.setEntity(new UrlEncodedFormEntity(params));			
			HttpResponse response = client.execute(httppost);	
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println("Response = "+responseBody);*/
			
			
			/*if(responseBody.compareTo("Bad API request, maximum paste file size exceeded")==0){
				showAlertMessage("ERROR:\nMaximum paste file size exceeded!");
				return;
			}
			else if(responseBody.compareTo("Bad API request, maximum number of 25 unlisted pastes for your free account")==0){
				showAlertMessage("ERROR:\nMaximum number of unlisted pastes exceeded for your account!");
				return;
			}
			else if(responseBody.compareTo("Bad API request, maximum number of 10 private pastes for your free account")==0){				
				showAlertMessage("ERROR:\nMaximum number of private pastes exceeded for your account!");
				return;
			}*/
			/*int ans = handleAPIError(responseBody);
			if(ans==-1)
				return;
			Intent i = new Intent(CreateNewPasteActivity.this,WebViewActivity.class);
			i.putExtra("url",responseBody);
			dialog.dismiss();
			startActivity(i);*/
			
		}catch(Exception e){
			System.err.println("Error while calling server : "+e.toString());		
		}
		
	}
	
	public boolean userLoggedIn(){
		SQLiteDatabase db;
		boolean ans = true;
		try{				
			db = openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
			String col[] = {"name,api_dev_key"};
			Cursor res = db.query(tblName, col, null, null, null, null, null);			
			if(res.getCount()==0){
				System.out.println("not logged in");
				ans = false;
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
			ans = false;
		}	
		System.out.println("userLoggedIn: return = "+ans);
		return ans;
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
	
	public int handleAPIError(String resp){		
		if(resp.compareTo("Bad API request, maximum paste file size exceeded")==0){
			showAlertMessage("ERROR:\nMaximum paste file size exceeded!");
			return -1;
		}
		else if(resp.compareTo("Bad API request, maximum number of 25 unlisted pastes for your free account")==0){
			showAlertMessage("ERROR:\nMaximum number of unlisted pastes exceeded for your account!");
			return -1;
		}
		else if(resp.compareTo("Bad API request, maximum number of 10 private pastes for your free account")==0){				
			showAlertMessage("ERROR:\nMaximum number of private pastes exceeded for your account!");
			return -1;
		}
		else
			return 0;
	}
	
	private class ProgressTask extends AsyncTask<String,Void,Boolean>{
		private ProgressDialog dialog;
		private Activity activity; 
		private List<NameValuePair> params;
		String responseBody="";
		private CreateNewPasteActivity ActivityClass;
		public ProgressTask(Activity activity, List<NameValuePair> p, CreateNewPasteActivity c){			
			this.activity = activity;			
			params = p;
			ActivityClass = c;
		}
		
		@Override
		protected void onPreExecute(){			
			dialog = ProgressDialog.show(CreateNewPasteActivity.this, "", "Creating Paste.Please Wait..");						
		}
		
		@Override
		protected void onPostExecute(final Boolean success){			
			
			dialog.cancel();
			int ans = handleAPIError(responseBody);
			if(ans==-1)
				return;
			Intent i = new Intent(CreateNewPasteActivity.this,WebViewActivity.class);
			i.putExtra("url",responseBody);
			dialog.dismiss();
			startActivity(i);
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
}
