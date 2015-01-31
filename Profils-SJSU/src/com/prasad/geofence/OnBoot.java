package com.prasad.geofence;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * 
 * 
 * This class starts the service after phone is rebooted
 * 
 * 
 */
public class OnBoot extends BroadcastReceiver
{
	 @Override
	 public void onReceive(Context aContext, Intent aIntent) 
	 {
		 //start our service
		 try
		 {
		       Intent i = new Intent(aContext, MainGeofenceActivity.class);
				//	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("boot","fromboot");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					aContext.startActivity(i);
		 }
		 catch(Exception e)
		{
		}
	}

	
}
