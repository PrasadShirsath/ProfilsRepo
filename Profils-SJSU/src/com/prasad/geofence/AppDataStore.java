package com.prasad.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppDataStore 
{
	  // The SharedPreferences object in which geofences are stored
    private final SharedPreferences mPrefs;

    // The name of the resulting SharedPreferences
    private static final String SHARED_PREFERENCE_NAME =
    		AppDataStore.class.getSimpleName();

    // Create the SharedPreferences storage with private access only
    public AppDataStore(Context context) 
    {
        mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
    }
    
    public void setFirstUsed(boolean b)
	  {
		  Editor editor = mPrefs.edit();
		  editor.putBoolean("firstUse",b);
		  editor.commit();
	  }
  public boolean getFirstUsed()
	  {
		  return mPrefs.getBoolean("firstUse",false);
	  }
  
  public void setMapHelpEnable(boolean b)
  {
	  Editor editor = mPrefs.edit();
	  editor.putBoolean("MapHelpEnable",b);
	  editor.commit();
  }
  public boolean getMapHelpEnable()
  {
	  return mPrefs.getBoolean("MapHelpEnable",false);
  }
  

}
