package com.asal.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.ActionBarActivity;

public class ABSActivity extends ActionBarActivity {

	private static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;

	// to save the user info(username and password) in shared preferences in order not to login when using the app again. 
	protected void writeSession(String username, String password) {
		Editor editor = sharedpreferences.edit();
		editor.putString("nameKey", username);
		editor.putString("passwordKey", password);
		editor.commit();
	}

	//to check if the user has logged in before or not, if yes he will skip the login screen.
	protected void checkSession() {
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		if (sharedpreferences.contains("nameKey")
				&& sharedpreferences.contains("passwordKey")) {
			Intent intent = new Intent(getApplicationContext(),
					NoteListActivity.class);
			startActivity(intent);
		}
	}

	//to remove the user info from the shared preferences when logging out.  
	protected void endSession() {
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.clear();
		editor.commit();
		moveTaskToBack(true);
		this.finish();
	}
}