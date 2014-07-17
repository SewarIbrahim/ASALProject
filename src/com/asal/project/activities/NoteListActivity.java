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

import com.asal.project.R;
import com.asal.project.classes.GPSTracker;
import com.asal.project.classes.NoteItem;
import com.asal.project.classes.NoteListAdpater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NoteListActivity extends ABSActivity{
	
	 private ArrayList<NoteItem> noteList = new ArrayList<NoteItem>();
	 private Button addNoteBtn,logoutBtn;
	 private GPSTracker gps;
	
	 public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.notes_list_activity);
	       
	       //To send a get request in order to view the notes in a list
			new GETRequestTask().execute("http://10.0.2.2:8081/list" ); 
		     
	        ListView lv = (ListView) findViewById(R.id.notesListView) ; 
	        NoteListAdpater myAdapter =  new NoteListAdpater(this, 0, noteList);
	        lv.setAdapter(myAdapter); 
	       
		    //to open a dialog to: 1- show the user location 2- add a new note
	        addNoteBtn = (Button) findViewById(R.id.addBtn);
	        addNoteBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					//to show the user location
				    ProgressBar locationProgBar=(ProgressBar) findViewById(R.id.locationProgessBar);
			        locationProgBar.setVisibility(View.VISIBLE); 
					
					gps = new GPSTracker(NoteListActivity.this);
					
					// check if GPS enabled
					if (gps.canGetLocation()) {
						double latitude  = gps.getLatitude();
						double longitude = gps.getLongitude();
						Toast.makeText( getApplicationContext(), "Your Location is - \nLat: " + latitude+ "\nLong: " + longitude, Toast.LENGTH_LONG).show();
					} else {
						// can't get location, GPS or Network is not enabled 
						gps.showSettingsAlert();}
					
		  	         locationProgBar.setVisibility(View.INVISIBLE); 
					
		  	         //to add a new note
			          showAddNoteDialog();
			     	}
			});	  
	        
	        logoutBtn = (Button) findViewById(R.id.logoutBtn);
	        logoutBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					   endSession();
			     	}
			});	  
		}
	 
	 /////////////////////////////////////////////////////////////////////////////////////////
	 
	 //this dialog appears while pressing on "add note" button to add a new note
	 public void showAddNoteDialog(){
        		 
	    	final View v = getLayoutInflater().inflate(R.layout.add_note_dialog, null) ; 
	    	AlertDialog.Builder b = new AlertDialog.Builder(this); 
	    	b.setView(v)  ; 
	    	
	    	//when pressing close button
			b.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
			
			//when pressing save button
	        b.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
				public void onClick(DialogInterface arg0, int arg1) {
					EditText title = (EditText) v.findViewById(R.id.titleEditText);
					EditText desc = (EditText) v.findViewById(R.id.descEditText);
					
				    new POSTRequestTask().execute("http://10.0.2.2:8081/saveNote",title.getText().toString(), desc.getText().toString());  
				}
			});
	        //to show the dialog
	    	b.show() ;
	 }
				
	 //////////////////////////////////////////////////////////////////////////////////
	 
	 class GETRequestTask extends AsyncTask<String, String, String> {     //the asyncTask is used when sth would take too much time as threads
			
		    @Override
		    protected String doInBackground(String... uri) {
		    	
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpResponse response;
		        String responseString = null;
		       
		        try {
		        	HttpGet get =new HttpGet(uri[0]) ;	                              
		            		        	
		            response = httpclient.execute(get);                           
		            
		            StatusLine statusLine = response.getStatusLine();  	           

		            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {          
		              
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
		        		        
		 	    JSONArray jsonArr = null; 
		 	    String noteTitle;
		 	    String noteDesc;
		 	    
				try {
					//To parse the JSON Array returned from the server and save the objects in the noteList
			        JSONObject jsonResponse = new JSONObject(result);
					jsonArr = jsonResponse.getJSONArray("array");
		            JSONObject jsonObj = null;

		            for (int i = 0; i < jsonArr.length(); i++) { 
		        	
						jsonObj = jsonArr.getJSONObject(i);
					    noteTitle = jsonObj.getString("Title");
				        noteDesc = jsonObj.getString("Desc");
			            NoteItem note = new NoteItem(noteTitle,noteDesc);
			 	    	noteList.add(note); 		           
		             }
		           } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} // End of onPostExecute
     }//End of AsyncTask
	 
	 ///////////////////////////////////////////////////////////////////////////////
	 class POSTRequestTask extends AsyncTask<String, String, String> {    
			
		    @Override
		    protected String doInBackground(String... uri) {
		    	
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpResponse response;
		        String responseString = null;
		        
		        try {
		        	HttpPost post = new HttpPost(uri[0]);   	                              

		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  	   
		            nameValuePairs.add(new BasicNameValuePair("Title", uri[1]));
		            nameValuePairs.add(new BasicNameValuePair("Description", uri[2]));
		            
		            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        	
		            response = httpclient.execute(post);                            //the post will be executed  // This sentence does the connection instead of the old way to do so.
		            
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
		        	e.printStackTrace();
		        } catch (IOException e) {
		        	e.printStackTrace(); }
		     	        
		        return responseString;
		    }

		    @Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);		        				
		    }
		} // End of RequestTask
}
	 
	
