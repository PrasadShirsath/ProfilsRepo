package com.prasad.maps;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.prasad.profilsworld.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditMapActivity extends FragmentActivity 
{
	
	GoogleMap mGoogleMap=null;

	private TextView radiusText=null;
	
	private double log=1000;
	private double lat=1000;
	private int radius=0;
	private String profilename;
	int progress = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		radiusText=(TextView)findViewById(R.id.textRadius);
		radiusText.setText(0+"mtrs");
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setVisibility(View.GONE);
		mGoogleMap = fragment.getMap();

		 // Enabling MyLocation Layer of Google Map
		mGoogleMap.setMyLocationEnabled(true);

		
		//handleIntent(getIntent());	
				
		Intent data = getIntent();
		 
		profilename=data.getStringExtra("profilename");
		 lat=data.getDoubleExtra("lat",0);
		 log=data.getDoubleExtra("log",0);
		 radius=(int) data.getFloatExtra("radius",0);
		 
		
		         	
		mGoogleMap.setPadding(0, 0, 0, 50);

          LatLng location = new LatLng(lat,log);
          
          if (location != null)
          {
        	  mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

              CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(location)      // Sets the center of the map to location user
              .zoom(17)                   // Sets the zoom
              .bearing(90)                // Sets the orientation of the camera to east
              .tilt(40)                   // Sets the tilt of the camera to 30 degrees
              .build();                   // Creates a CameraPosition from the builder
              mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
              
              MarkerOptions markerOptions = new MarkerOptions();

              markerOptions.position(location);
  			// Setting the title for the marker. 
  			// This will be displayed on taping the marker
  			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

  			 markerOptions.title(""+profilename);
  			
  			// Clears the previously touched position
  			mGoogleMap.clear();
  			
  			// Placing a marker on the touched position
  			mGoogleMap.addMarker(markerOptions);
  			
  			
  			seekBar.setProgress(radius);
  			radiusText.setText(radius+"mts");
  			int strokeColor = 0xff6666ff; //red outline
  		    int shadeColor = 0x186666ff; //opaque red fill
  		    CircleOptions circleOptions = new CircleOptions().center(location).radius(radius).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
  		    mGoogleMap.addCircle(circleOptions);
  			//listener.setCircle(mCircle);
          }
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		return true;
	}
	  
}