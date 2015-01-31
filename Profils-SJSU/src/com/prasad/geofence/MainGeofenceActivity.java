
package com.prasad.geofence;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.prasad.geofence.GeofenceUtils.REMOVE_TYPE;
import com.prasad.geofence.GeofenceUtils.REQUEST_TYPE;
import com.prasad.helpactivities.Onmaphelp;
import com.prasad.viewpager.ViewPagerStyle1Activity;
import com.achep.header2actionbar.FadingActionBarHelper;
import com.example.headeractionbar.ListViewFragment;
import com.example.headeractionbar.ProfileData;
import com.example.headeractionbar.ProfileDataStore;
import com.prasad.profilsworld.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * UI handler for the Location Services Geofence sample app.
 * Allow input of latitude, longitude, and radius for two geofences.
 * When registering geofences, check input and then send the geofences to Location Services.
 * Also allow removing either one of or both of the geofences.
 * The menu allows you to clear the screen or delete the geofences stored in persistent memory.
 */
@SuppressLint("NewApi") public class MainGeofenceActivity extends FragmentActivity {
    /*
     * Use to set an expiration time for a geofence. After this amount
     * of time Location Services will stop tracking the geofence.
     * Remember to unregister a geofence when you're finished with it.
     * Otherwise, your app will use up battery. To continue monitoring
     * a geofence indefinitely, set the expiration time to
     * Geofence#NEVER_EXPIRE.
     */
  
    // Store the current request
    private REQUEST_TYPE mRequestType;

    // Store the current type of removal
    private REMOVE_TYPE mRemoveType;

    // Persistent storage for geofences
    private SimpleGeofenceStore mPrefs;

    // Store a list of geofences to add
    List<Geofence> mCurrentGeofences;

    // Add geofences handler
    private GeofenceRequester mGeofenceRequester;
    // Remove geofences handler
    private GeofenceRemover mGeofenceRemover;
    // Handle to geofence 1 latitude in the UI
   

    /*
     * Internal lightweight geofence objects for geofence 1 and 2
     */
    private SimpleGeofence mUIGeofence1;

    // decimal formats for latitude, longitude, and radius
    private DecimalFormat mLatLngFormat;
    private DecimalFormat mRadiusFormat;

    /*
     * An instance of an inner class that receives broadcasts from listeners and from the
     * IntentService that receives geofence transition events
     */
    private GeofenceSampleReceiver mBroadcastReceiver;

    // An intent filter for the broadcast receiver
    private IntentFilter mIntentFilter;
    
    // Store the list of geofences to remove
    private List<String> mGeofenceIdsToRemove;

  
    public static MainGeofenceActivity mymain;
	Context c=null;
	
	/****ListUI******/
	
	  private FadingActionBarHelper mFadingActionBarHelper;

	/**********/
    public MainGeofenceActivity() 
    {
    	
    }
	  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        // Attach to the main UI
        setContentView(R.layout.activity_main);
        
        mymain=this;

        mFadingActionBarHelper = new FadingActionBarHelper(getActionBar(),
                getResources().getDrawable(R.drawable.actionbar_bg));

       
      
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ListViewFragment())
                    .commit();
        }
        /*********************/
      
        // Instantiate a new geofence storage area
        mPrefs = new SimpleGeofenceStore(this);
        
        
        c=this;
        if(!new AppDataStore(this).getFirstUsed())
        {
        	
        	startActivity(new Intent(this,ViewPagerStyle1Activity.class));
        	
        }
      
        

        // Set the pattern for the latitude and longitude format
        String latLngPattern = getString(R.string.lat_lng_pattern);

        // Set the format for latitude and longitude
        mLatLngFormat = new DecimalFormat(latLngPattern);

        // Localize the format
        mLatLngFormat.applyLocalizedPattern(mLatLngFormat.toLocalizedPattern());

        // Set the pattern for the radius format
        String radiusPattern = getString(R.string.radius_pattern);

        // Set the format for the radius
        mRadiusFormat = new DecimalFormat(radiusPattern);

        // Localize the pattern
        mRadiusFormat.applyLocalizedPattern(mRadiusFormat.toLocalizedPattern());

        // Create a new broadcast receiver to receive updates from the listeners and service
        mBroadcastReceiver = new GeofenceSampleReceiver();

        // Create an intent filter for the broadcast receiver
        mIntentFilter = new IntentFilter();

        // Action for broadcast Intents that report successful addition of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_ADDED);

        // Action for broadcast Intents that report successful removal of geofences
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCES_REMOVED);

        // Action for broadcast Intents containing various types of geofencing errors
        mIntentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_ERROR);

        // All Location Services sample apps use this category
        mIntentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);


        // Instantiate the current List of geofences
        mCurrentGeofences = new ArrayList<Geofence>();

        // Instantiate a Geofence requester
        mGeofenceRequester = new GeofenceRequester(this);

        // Instantiate a Geofence remover
        mGeofenceRemover = new GeofenceRemover(this);
        
        
        if(this.getIntent().hasExtra("boot") &&this.getIntent().getStringExtra("boot").equals("fromboot"))
        {
        		   ProfileDataStore ProfilsPref = new ProfileDataStore(this);
		           List<ProfileData> mItems = ProfilsPref.getAllProfiles();
			       for (ProfileData p : mItems)
			        {
					       registerGeofence(p.getGeofenceId(),p.getLatitude(), p.getLongitude(), p.getRadius());
					       //Toast.makeText(aContext, "OnBoot", Toast.LENGTH_SHORT).show();
			        } 
			       
        }
        
       
        	
		       
   
        
    }

    @Override
	protected boolean onPrepareOptionsPanel(View view, Menu menu) {
		// TODO Auto-generated method stub
    	menu.findItem(R.id.action_share).setVisible(true);
    	menu.findItem(R.id.action_rate).setVisible(true);

		return super.onPrepareOptionsPanel(view, menu);
	}

	/*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     * calls
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) 
        {

            // If the request code matches the code sent in onConnectionFailed
            case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // If the request was to add geofences
                        if (GeofenceUtils.REQUEST_TYPE.ADD == mRequestType) {

                            // Toggle the request flag and send a new request
                            mGeofenceRequester.setInProgressFlag(false);

                            // Restart the process of adding the current geofences
                            mGeofenceRequester.addGeofences(mCurrentGeofences);

                        // If the request was to remove geofences
                        } else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mRequestType ){

                            // Toggle the removal flag and send a new removal request
                            mGeofenceRemover.setInProgressFlag(false);

                            // If the removal was by Intent
                            if (GeofenceUtils.REMOVE_TYPE.INTENT == mRemoveType) {

                                // Restart the removal of all geofences for the PendingIntent
                                mGeofenceRemover.removeGeofencesByIntent(
                                    mGeofenceRequester.getRequestPendingIntent());

                            // If the removal was by a List of geofence IDs
                            } else {

                                // Restart the removal of the geofence list
                                mGeofenceRemover.removeGeofencesById(mGeofenceIdsToRemove);
                            }
                        }
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Report that Google Play services was unable to resolve the problem.
//                        Log.d(GeofenceUtils.APPTAG, getString(R.string.no_resolution));
                }
            

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
//               Log.d(GeofenceUtils.APPTAG,
//                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    /*
     * Whenever the Activity resumes, reconnect the client to Location
     * Services and reload the last geofences that were set
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver to receive status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
//    	Log.d("geofence", "onResume!");
    	 
    	//check for location service is enabled or not 
        if(!isLocationEnabled(this))
        {
        	 AlertDialog.Builder dialog = new AlertDialog.Builder(this);
             dialog.setMessage(R.string.open_location_settings);
             dialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                     // TODO Auto-generated method stub
                 	Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                     startActivity(myIntent);
                     //get gps
                 }
             });
             dialog.setNegativeButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {

                 @Override
                 public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                     // TODO Auto-generated method stub

                 }
             });
             dialog.show();
  
        }
    }

    /*
     * Inflate the app menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	 getMenuInflater().inflate(R.menu.homepagemenu, menu);
        return true;

    }
    /*
     * Respond to menu item selections
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
       switch (item.getItemId()) {

            // Request to clear the geofence1 settings in the UI
            case R.id.action_share:
            	  Intent intent = new Intent(Intent.ACTION_SEND);
                  intent.setType("text/plain");
                  intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.prasad.profilsworld");
                  intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this cool App!");
                  startActivity(Intent.createChooser(intent, "Share"));

                return true;
                
            case R.id.action_rate:
            	Intent intentrate = new Intent(Intent.ACTION_VIEW);
            	intentrate.setData(Uri.parse("market://details?id=com.prasad.profilsworld"));
            	startActivity(intentrate);

              return true;

            // Pass through any other request
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Save the current geofence settings in SharedPreferences.
     */
    @Override
    protected void onPause() {
        super.onPause();

       // mPrefs.setGeofence("1", mUIGeofence1);
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
//            Log.d(GeofenceUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), GeofenceUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Called when the user clicks the "Remove geofences" button
     * @param id 
     *
     * @param view The view that triggered this callback
     */
    public void unregisterGeofence(String id) {
        /*
         * Remove all geofences set by this app. To do this, get the
         * PendingIntent that was added when the geofences were added
         * and use it as an argument to removeGeofences(). The removal
         * happens asynchronously; Location Services calls
         * onRemoveGeofencesByPendingIntentResult() (implemented in
         * the current Activity) when the removal is done
         */

        /*
         * Record the removal as remove by Intent. If a connection error occurs,
         * the app can automatically restart the removal if Google Play services
         * can fix the error
         */
        // Record the type of removal
        mRemoveType = GeofenceUtils.REMOVE_TYPE.INTENT;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }

        // Try to make a removal request
        try {
        /*
         * Remove the geofences represented by the currently-active PendingIntent. If the
         * PendingIntent was removed for some reason, re-create it; since it's always
         * created with FLAG_UPDATE_CURRENT, an identical PendingIntent is always created.
         */
        	ArrayList<String> l= new ArrayList<String>();
        	l.add(id);
         mGeofenceRemover.removeGeofencesById(l);

        //mGeofenceRemover.removeGeofencesByIntent(mGeofenceRequester.getRequestPendingIntent());
    	mPrefs.clearGeofenceByID(id);


        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
//            Toast.makeText(this, R.string.remove_geofences_already_requested_error,
//                        Toast.LENGTH_LONG).show();
        }

    }

      /**
     * Called when the user clicks the "Register geofences" button.
     * Get the geofence parameters for each geofence and add them to
     * a List. Create the PendingIntent containing an Intent that
     * Location Services sends to this app's broadcast receiver when
     * Location Services detects a geofence transition. Send the List
     * and the PendingIntent to Location Services.
     * @param id 
     */
    public void registerGeofence(String id, double Latitude,double Longitude,float Radius) {

        /*
         * Record the request as an ADD. If a connection error occurs,
         * the app can automatically restart the add request if Google Play services
         * can fix the error
         */
        mRequestType = GeofenceUtils.REQUEST_TYPE.ADD;

        /*
         * Check for Google Play services. Do this after
         * setting the request type. If connecting to Google Play services
         * fails, onActivityResult is eventually called, and it needs to
         * know what type of request was in progress.
         */
        if (!servicesConnected()) {

            return;
        }


        /*
         * Create a version of geofence 1 that is "flattened" into individual fields. This
         * allows it to be stored in SharedPreferences.
         */
        mUIGeofence1 = new SimpleGeofence(
            id,
            // Get latitude, longitude, and radius
            Latitude,
            Longitude,
            Radius,
            // Set the expiration time
            Geofence.NEVER_EXPIRE,
            // detect entry transitions and exit
            Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        // Store this flat version in SharedPreferences
        mPrefs.setGeofence(id, mUIGeofence1);
        /*
         * Add Geofence objects to a List. toGeofence()
         * creates a Location Services Geofence object from a
         * flat object
         */
        mCurrentGeofences.add(mUIGeofence1.toGeofence());

        // Start the request. Fail if there's already a request in progress
        try {
            // Try to add geofences
            mGeofenceRequester.addGeofences(mCurrentGeofences);
            
        } catch (UnsupportedOperationException e) {
            // Notify user that previous request hasn't finished.
//            Toast.makeText(this, R.string.add_geofences_already_requested_error,
//                        Toast.LENGTH_LONG).show();
        }
    }
  
       /**
     * Define a Broadcast receiver that receives updates from connection listeners and
     * the geofence transition service.
     */
    public class GeofenceSampleReceiver extends BroadcastReceiver {
        /*
         * Define the required method for broadcast receivers
         * This method is invoked when a broadcast Intent triggers the receiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // Check the action code and determine what to do
            String action = intent.getAction();

            // Intent contains information about errors in adding or removing geofences
            if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)) {

//            	Log.d("geofence", "Geofence error!");

                handleGeofenceError(context, intent);

            // Intent contains information about successful addition or removal of geofences
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED))
            		{
            		//	Toast.makeText(context,"Service enabled",Toast.LENGTH_SHORT).show();

            		}
            else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED))
            {

//            	Log.d("geofence", "Geofence added/removed!");
                handleGeofenceStatus(context, intent);
            // Intent contains information about a geofence transition
            } else if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)) {

                handleGeofenceTransition(context, intent);
                

            // The Intent contained an invalid action
            } else {
               Toast.makeText(context, R.string.invalid_action, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * If you want to display a UI message about adding or removing geofences, put it here.
         *
         * @param context A Context for this component
         * @param intent The received broadcast Intent
         */
        private void handleGeofenceStatus(Context context, Intent intent) 
        {
//        	Log.d("geofence", "Geofence added/removed!");

        }

        /**
         * Report geofence transitions to the UI
         *
         * @param context A Context for this component
         * @param intent The Intent containing the transition
         */
        private void handleGeofenceTransition(Context context, Intent intent) 
        {
            /*
             * If you want to change the UI when a transition occurs, put the code
             * here. The current design of the app uses a notification to inform the
             * user that a transition has occurred.
             */
//        	Log.d("geofence", "Inside hagleGeofenceTransition!");
        	

        }

        /**
         * Report addition or removal errors to the UI, using a Toast
         *
         * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
         */
        private void handleGeofenceError(Context context, Intent intent) 
        {
        	
          //  String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
//            Log.e(GeofenceUtils.APPTAG, msg);
            //Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
       
           
          
            
            
        }
    }
    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /*
     * 
     * To check loction service is enable or not
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    } 
    /***ListUI****/
    public FadingActionBarHelper getFadingActionBarHelper() {
        return mFadingActionBarHelper;
    }
    /*********/

}
