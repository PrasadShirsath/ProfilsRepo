package com.example.headeractionbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class ProfileDataStore
{
	 private final SharedPreferences mPrefs;

	    // The name of the resulting SharedPreferences
	    private static final String SHARED_PREFERENCE_NAME =
	    		ProfileDataStore.class.getSimpleName();

	    // Create the SharedPreferences storage with private access only
	    public ProfileDataStore(Context context) {
	        mPrefs =context.getSharedPreferences(
	                        SHARED_PREFERENCE_NAME,
	                        Context.MODE_PRIVATE);
	    }
	    
	    public void setProfileData(ProfileData p)
	    {
	    	Gson gson = new Gson();
	        String profils_json = gson.toJson(p);
	        Editor editor = mPrefs.edit();	        
	        // store in SharedPreferences
	        String id =  p.getGeofenceId(); // get storage key
	        editor.putString(id, profils_json);
	        editor.commit();
	    }
	    
	   public ProfileData getProfileDataByID(String geofenceId)
	   {
		   Gson gson = new Gson();
		   String profile_gson = mPrefs.getString(geofenceId, "");
		   return gson.fromJson(profile_gson, ProfileData.class);
	    }
	   
	   public String getProfileRingermodeByID(String geofenceId)
	   {
		   Gson gson = new Gson();
		   String profile_gson = mPrefs.getString(geofenceId, "");
		    ProfileData p= gson.fromJson(profile_gson, ProfileData.class);
		    if(p!=null)
		    	return p.getRingerMode();
		    else
		    	return "vibrate";
		    
	    }
	   public List<ProfileData> getAllProfiles()
	   {
		   ArrayList<ProfileData> lp = new ArrayList();
		   
		   Map<String, ?> allEntries = mPrefs.getAll();
		   for (Map.Entry<String, ?> entry : allEntries.entrySet()) 
		   {
		       String profile_gson = entry.getValue().toString().trim();
			   Gson gson = new Gson();
		      lp.add(gson.fromJson(profile_gson, ProfileData.class));
		   } 
		   
		   return lp;
	   }
	   
	  
	   public void clearProfileByID(String geofenceId) {
	        // Remove a flattened geofence object from storage by removing all of its keys
	        Editor editor = mPrefs.edit();
	        editor.remove(geofenceId);
	        editor.commit();	        
		   
	    }
	    public void clearAllProfiles() {
	        Editor editor = mPrefs.edit();
	        editor.clear().commit();
	    }
	   
	    
	    boolean isProfilePresent(String geofenceId)
	    {
	    	return mPrefs.contains(geofenceId);
	    }
	   
}
