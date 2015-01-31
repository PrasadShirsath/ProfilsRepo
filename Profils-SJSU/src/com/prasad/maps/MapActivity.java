package com.prasad.maps;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.prasad.profilsworld.R;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prasad.geofence.AppDataStore;
import com.prasad.helpactivities.Onmaphelp;

public class MapActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{
	
	GoogleMap mGoogleMap=null;
	private SeekBar seekBar=null;
	private MyBarChangeListener barListener=null;

	private TextView radiusText=null;
	
	private double log=1000;
	private double lat=1000;
	private int radius=0;
	private String profilename;
	int progress = 0;
	private String ringerMode = "vibrate"; 
	float acc=0;
	
	
	//Listener for seek bar
	class MyBarChangeListener implements OnSeekBarChangeListener{
		private Circle circle;
		
		public Circle getCircle() {
			return circle;
		}

		public void setCircle(Circle circle) {
			this.circle = circle;
		}

		
		@Override
		public void onProgressChanged(SeekBar seekBar, 
				int progresValue, boolean fromUser) {
			progress = progresValue;
			if(circle!=null){
				circle.setRadius(progress);
				radiusText.setText(progress+"mtrs");
				radius=progress;

			}
		
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) 
		{
			// Do something here, 
			//if you want to do anything at the start of
			// touching the seekbar
			if(lat==1000 || log==1000)
			{
				Toast.makeText(getApplicationContext(), "First tap on map to select location then set radius.",Toast.LENGTH_SHORT).show(); 
			}
		
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// Display the value in textview
			//textView.setText(progress + "/" + seekBar.getMax());
		}
	}
	
	//listener for map click
	class MapClickListener implements OnMapClickListener{

		private GoogleMap map;
		private MyBarChangeListener listener;


		MapClickListener(GoogleMap map)
		{
			this.map = map;
		}

		public MyBarChangeListener getListener() {
			return listener;
		}


		public void setListener(MyBarChangeListener listener) {
			this.listener = listener;
		}
		

		@Override
		public void onMapClick(LatLng arg0) 
		{
			
			// Creating a marker
			MarkerOptions markerOptions = new MarkerOptions();
			
			seekBar.setProgress(0);
			// Setting the position for the marker
			markerOptions.position(arg0);
			// Setting the title for the marker. 
			// This will be displayed on taping the marker
			markerOptions.title("Location Accuracy: "+acc+" mts");
			markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
			// Clears the previously touched position
			map.clear();
			
			// Animating to the touched position                				
			map.animateCamera(CameraUpdateFactory.newLatLng(arg0));
			
			// Placing a marker on the touched position
			map.addMarker(markerOptions);
			
			lat=arg0.latitude;
			log=arg0.longitude;
			radius=100;
			
			seekBar.setProgress(100);
			radiusText.setText("100mts");
			Circle mCircle;
			int strokeColor = 0xff6666ff; //red outline
		    int shadeColor = 0x186666ff; //opaque red fill
		    CircleOptions circleOptions = new CircleOptions().center(arg0).radius(100).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
		    mCircle = map.addCircle(circleOptions);
			listener.setCircle(mCircle);			
			
			
		}


	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		radiusText=(TextView)findViewById(R.id.textRadius);
		radiusText.setText(0+"mtrs");
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		
		profilename=getIntent().getStringExtra("profilename");
        	
		mGoogleMap = fragment.getMap();
		mGoogleMap.setPadding(0, 0, 0, 50);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		  
		  // Enabling MyLocation Layer of Google Map
			mGoogleMap.setMyLocationEnabled(true);
          Criteria criteria = new Criteria();

          Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
         
          if (location != null)
          {
        	  
//        	  mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                      new LatLng(location.getLatitude(), location.getLongitude()), 13));

              CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
              .zoom(17)                   // Sets the zoom
              .bearing(90)                // Sets the orientation of the camera to east
              .tilt(40)                   // Sets the tilt of the camera to 30 degrees
              .build();                   // Creates a CameraPosition from the builder
              mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
              acc=location.getAccuracy();



          }

		
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setMax(500);
		barListener = new MyBarChangeListener();
		seekBar.setOnSeekBarChangeListener(barListener);

		
		//handleIntent(getIntent());	
				
		MapClickListener lst = new MapClickListener(mGoogleMap);
		lst.setListener(barListener);
		mGoogleMap.setOnMapClickListener(lst);
		
		if(!new AppDataStore(this).getMapHelpEnable())
        {
        	new AppDataStore(this).setMapHelpEnable(true);
        	startActivity(new Intent(this, Onmaphelp.class));
        	
        }
	
		
		 
	}
	
	private void handleIntent(Intent intent){
		if(intent.getAction().equals(Intent.ACTION_SEARCH)){
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		}else if(intent.getAction().equals(Intent.ACTION_VIEW)){
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {		
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void doSearch(String query){		
		Bundle data = new Bundle();
		data.putString("query", query);		
		getSupportLoaderManager().restartLoader(0, data, this);
	}
	
	private void getPlace(String query){		
		Bundle data = new Bundle();
		data.putString("query", query);		
		getSupportLoaderManager().restartLoader(1, data, this);
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_search:	
				onSearchRequested();
				break;
			case R.id.action_addloc:
				addNewLoc();
		}	
		return super.onMenuItemSelected(featureId, item);
	}

	private void addNewLoc()
	{
		if(lat==1000 || log==1000)
		{
			Toast.makeText(getApplicationContext(), "First tap on map to select location and it's radius.",Toast.LENGTH_SHORT).show(); 
		}
		else
		{
			showRingerAlert();
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
		if(arg0==0)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
		else if(arg0==1)
			cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
		return cLoader;
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {	
		showLocations(c);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub		
	}
	
	private void showLocations(Cursor c){
		MarkerOptions markerOptions = null;
		Circle mCircle;
		int strokeColor = 0xff0000ff; //red outline
	    int shadeColor = 0x44ff0000; //opaque red fill
		LatLng position = null;
		mGoogleMap.clear();
		while(c.moveToNext()){
			markerOptions = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
			markerOptions.position(position);
//			markerOptions.title(c.getString(0));
//		    CircleOptions circleOptions = new CircleOptions().center(position).radius(100).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
//		    mCircle = mGoogleMap.addCircle(circleOptions);

			mGoogleMap.addMarker(markerOptions);
		}
		if(position!=null){
			CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
			mGoogleMap.animateCamera(cameraPosition);			
		}
	}		
	   public void showRingerAlert()
	    {
	    	final CharSequence[] items={"Normal","Vibrate","Silent"};
	    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
	    	builder.setTitle("Choose Ringer mode");
	    	builder.setPositiveButton("Done", new DialogInterface.OnClickListener() 
	    	{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
				
					if(lat!=1000 || log!=1000)
					{
						
						/*******return values to home activity****/
						Intent j = getIntent();
						j.putExtra("lat", lat);
						j.putExtra("log", log);
						j.putExtra("radius", radius);
						j.putExtra("ringermode", ringerMode);
						j.putExtra("profilename",profilename);
			             
					
						setResult(RESULT_OK, j);
						finish();
						
						/**************/
					}
					
				}
			});
	    	
	     	builder.setSingleChoiceItems(items,1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					
					if("Normal".equals(items[which]))
					{
						ringerMode="normal";
					}
					else if("Vibrate".equals(items[which]))
					{
						ringerMode="vibrate";

					}
					else if("Silent".equals(items[which]))
					{
						ringerMode="silent";
					}
					
				}
			});
	    	builder.show();
	    }
		
	
	
}