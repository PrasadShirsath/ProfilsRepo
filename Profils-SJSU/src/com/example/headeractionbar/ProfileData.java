package com.example.headeractionbar;

public class ProfileData 
{
	
	 private String geofenceId;
	 private double mLatitude;
	 private double mLongitude;
	 private float mRadius;
	 private boolean enabled;
	 private String ringermode;
	 
	 public ProfileData()
	 {
		 this.geofenceId="";
	        // Center of the geofence
	        this.mLatitude = -999;
	        this.mLongitude = -999;
	        // Radius of the geofence, in meters
	        this.mRadius = 0;
	        this.enabled=true;
	        this.ringermode="";
	 }
	
	 public ProfileData(
	        String geofenceId,
			double latitude,
            double longitude,
            float radius,
            String ringermode,
            boolean enabled)
		{
			 // An identifier for the geofence
	       
	        this.geofenceId=geofenceId;
	        // Center of the geofence
	        this.mLatitude = latitude;
	        this.mLongitude = longitude;
	        // Radius of the geofence, in meters
	        this.mRadius = radius;
	        this.enabled=enabled;
	        this.ringermode=ringermode;

		}
	 /**
	     * Get the  ID
	     * @return  ID
	     */
	
	 
	  public String getGeofenceId() 
	  {
		  
	        return geofenceId;
	    }
	  /**
	     * Get the  latitude
	     * @return A latitude value
	     */
	    public double getLatitude() {
	        return mLatitude;
	    }

	    /**
	     * Get the  longitude
	     * @return A longitude value
	     */
	    public double getLongitude() {
	        return mLongitude;
	    }

	    /**
	     * Get the  radius
	     * @return A radius value
	     */
	    public float getRadius() {
	        return mRadius;
	    }
	    
	    public boolean isEnabled() {
	        return enabled;
	    }
	    
	    public String getRingerMode() {
	        return ringermode;
	    }
	    
	    
	    
	    /*****Setters*****/
	    public void setGeofenceId(String geofenceId) 
		  {
			  
		         this.geofenceId=geofenceId;
		    }
		 
		    public void setLatitude(double mLatitude) {
		         this.mLatitude=mLatitude;
		    }

		  
		    public void setLongitude(double mLongitude) {
		         this.mLongitude=mLongitude;
		    }

		  
		    public void setRadius(float mRadius) {
		         this.mRadius=mRadius;
		    }
		    
		    public void setEnabled(boolean enable) 
		    {
		         this.enabled=enable;
		    }
		    
		    public void setRingerMode(String ringermode) {
		         this.ringermode= ringermode;
		    }
}
