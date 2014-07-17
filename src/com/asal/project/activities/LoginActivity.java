package com.asal.project.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



import com.asal.project.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Build;

@SuppressLint("NewApi")
public class LoginActivity extends ABSActivity {

	private EditText userNameEt,passwordEt;
	private Button loginBtn,registerBtn;
    private String username,password;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.login_activity);
		
		userNameEt = (EditText) findViewById(R.id.username_edit);
		passwordEt = (EditText) findViewById(R.id.password_edit);
		
		loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username= userNameEt.getText().toString();
			    password = passwordEt.getText().toString();	
			      
			    if(emailValidation(username, password)){			    	
			    	writeSession(username, password);
			        // to send the login request
				    new POSTRequestTask("login").execute("http://10.0.2.2:8081/login" , username , password);} //we can add params as much as we need in execute, use 10.0.2.2 for mob emulator
           	 }  
		 });
		
		registerBtn= (Button) findViewById(R.id.Registration_btn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username= userNameEt.getText().toString();
			    password = passwordEt.getText().toString();
				
			    if(emailValidation(username, password)){
			      //to send the registration request
				  new POSTRequestTask("regist").execute("http://10.0.2.2:8081/registration" , username , password);} //we can add params as much as we need in execute, use 10.0.2.2 for mob emulator	
			      Toast.makeText(getApplicationContext(), "You have registered successfully!", Toast.LENGTH_SHORT).show();
			}
		});
	   }//end of onCreate
	
	  //will be executing while opening the app again 
	  @Override
	   protected void onResume() {
	      checkSession();
	      super.onResume();
	   }

	//to check if the entered username and password are valid or not 
     public Boolean emailValidation(String userName, String password){
    	 
    	 Boolean isValid= true;
    	 
			if (userName.length() == 0){
			     Toast.makeText(getApplicationContext(),"username is required!", Toast.LENGTH_SHORT).show();
			     isValid=false;}
			if (password.length() == 0){
			     Toast.makeText(getApplicationContext(),"password is required!", Toast.LENGTH_SHORT).show();
			     isValid=false;}
			if (password.length() < 6){
			     Toast.makeText(getApplicationContext(),"The password is too short!", Toast.LENGTH_SHORT).show();
			     isValid=false;}
			
			String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Boolean b = userName.matches(emailreg);
			if (b == false){
		    	Toast.makeText(getApplicationContext(),"The email is not valid!", Toast.LENGTH_SHORT).show();
		    	isValid=false;}
			
			return isValid;
     }
	
	 class POSTRequestTask extends AsyncTask<String, String, String> {     //the asyncTask is used when sth would take too much time as threads
	    Boolean responseStatus;
		String reqType;
		
	    public POSTRequestTask(String reqType) {
		super();
		this.reqType=reqType;
	    }

		@Override
	    protected String doInBackground(String... uri) {
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        
	        try {
	        	HttpPost post = new HttpPost(uri[0]);   	                              

	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  	  // Add your data. This list is especially used for the post 
	            nameValuePairs.add(new BasicNameValuePair("username", uri[1]));
	            nameValuePairs.add(new BasicNameValuePair("password", uri[2]));
	            
	            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        	
	            response = httpclient.execute(post);                            
	            
	            StatusLine statusLine = response.getStatusLine();  	            //to check the status of the operation 

	            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {            // the status means if the request is executed correctly and the response is sent back to the app from the server without any problem
	              
	            	ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);	                        //to get out the response data    
	                responseString = out.toString();
	                
	                out.close();
                 } 
	             else {
	                response.getEntity().getContent().close();                  //closes the connection.
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	            
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        	e.printStackTrace();
	        } catch (IOException e) {
	            //TODO Handle problems..
	        	e.printStackTrace();
	        }
	        	        
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if(reqType=="login"){
	        	
	        	JSONObject json = null;
	        
	        	try {	//to parse the returned JSON object 
	        		json = new JSONObject(result);
	        	} catch (JSONException e) {
	        		e.printStackTrace();
	        	} 
			
	        	try {   //the returned JSON Obj: {"status": ture/false}
	        		responseStatus = json.getBoolean("status");
	        	} catch (JSONException e) {
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	        	}
            
	        	if(responseStatus == false){
	        		Toast.makeText(getApplicationContext(), "The user is invalid!", Toast.LENGTH_SHORT).show();
	        	} else {
	        		Intent intent = new Intent(getApplicationContext() , NoteListActivity.class); 
  		       		startActivity(intent);
	        	}      
	        }//End of if stat.
	  } 
	} // End of RequestTask
} 




