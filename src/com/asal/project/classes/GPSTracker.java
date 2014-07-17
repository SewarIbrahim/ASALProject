package com.asal.project.classes;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker implements LocationListener {

	private final Context context;
	private Location location;             // location
	boolean isGPSEnabled = false; 	       // flag for GPS status
	boolean isNetworkEnabled = false; 	   // flag for network status
	boolean canGetLocation = false;    	   // flag for ability to get the location
	double latitude;                       // latitude
	double longitude;                      // longitude
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters, the minimum distance to change Updates in meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute, the minimum time between updates in milliseconds
	protected LocationManager locationManager; 	// Declaring a Location Manager


	public GPSTracker(Context context) {
		this.context = context;
		getLocation();
	}

	// to get the user location using either the network provider or the GPS services
	public Location getLocation() {
		try {

			locationManager = (LocationManager) context.getSystemService(this.context.LOCATION_SERVICE);

			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // getting GPS status

			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);	// getting network status

			if (!isGPSEnabled && !isNetworkEnabled) {
				Toast.makeText(context, "GPS or Network is disabled!", Toast.LENGTH_SHORT).show();
			} else {
				
				this.canGetLocation = true;
				
				// First get location from Network Provider
				if (isNetworkEnabled) {

					locationManager.requestLocationUpdates(	LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {

				     if (location == null) {
						  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							latitude = location.getLatitude();
							longitude = location.getLongitude();

							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	  return location;
	}

	
	// to get latitude
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	// to get longitude
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	
	//to check if GPS/wifi are enabled
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	// to show settings alert dialog. On pressing Settings button will launch Settings Options
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(	Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
	                    dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location arg0) {		
	}

	@Override
	public void onProviderDisabled(String arg0) {		
	}

	@Override
	public void onProviderEnabled(String arg0) {		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {		
	}
}