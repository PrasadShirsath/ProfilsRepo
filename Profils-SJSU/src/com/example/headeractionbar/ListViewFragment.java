
package com.example.headeractionbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.achep.header2actionbar.HeaderFragment;
import com.example.headeractionbar.CustomList.ListItemHolder;
import com.prasad.geofence.MainGeofenceActivity;
import com.prasad.maps.EditMapActivity;
import com.prasad.maps.MapActivity;
import com.prasad.profilsworld.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Artem on 06.12.13.
 */
public class ListViewFragment extends HeaderFragment 
{

    private static ListView mListView;
    private boolean mLoaded;

    private AsyncLoadSomething mAsyncLoadSomething;
    private FrameLayout mContentOverlay;
    public CustomList adapter;
    private static List<ProfileData> mItems;  
    public ProfileDataStore ProfilsPref;
	protected String ringerMode;
    static int i=0;

   public static ListViewFragment mainContext;

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ProfilsPref = new ProfileDataStore(getActivity());
        mainContext=this;
        
        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                height -= getActivity().getActionBar().getHeight();

                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;

                // *
                // `*
                // ```*
                // ``````*
                // ````````*
                // `````````*
                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;

                ((MainGeofenceActivity) getActivity())
                        .getFadingActionBarHelper()
                        .setActionBarAlpha((int) (255 * progress));
                
            }
        });
        
        //setListViewTitles();
        cancelAsyncTask(mAsyncLoadSomething);
        mAsyncLoadSomething = new AsyncLoadSomething(this);
        mAsyncLoadSomething.execute();
    }

    @Override
    public void onDetach() {
        cancelAsyncTask(mAsyncLoadSomething);
        super.onDetach();
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) 
    {
        return inflater.inflate(R.layout.fragment_header, container, false);
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        mListView = (ListView) inflater.inflate(R.layout.fragment_listview, container, false);
        if (mLoaded) setListViewTitles();
        
        
        
        return mListView;
    }
   
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
    	inflater.inflate(R.menu.homepagemenu, menu);
    
    	
	}

	@Override
    public View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container) {
        ProgressBar progressBar = new ProgressBar(getActivity());
        mContentOverlay = new FrameLayout(getActivity());
        mContentOverlay.addView(progressBar, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        if (mLoaded) mContentOverlay.setVisibility(View.GONE);
        return mContentOverlay;
    }

    private void setListViewTitles() 
    {
        mLoaded = true;
       // mListViewTitles = titles;
        if (mListView == null) return;
//        /*****************/
       
        mItems = ProfilsPref.getAllProfiles();
//        mItems.add("asd");
        //ProfilsPref.clearAllProfiles();
       
        
//        ProfileData geo = new ProfileData("San Jose States",123,321,3,"will be put on vibrate",true); 
//        mItems.add(geo);

       // ProfileData pd=ProfilsPref.getProfileDataByID("home");

        
        adapter = new CustomList(getActivity(),this,mItems);
        mListView.setAdapter(adapter);
 
        mListView.setVisibility(View.VISIBLE);
//        setListViewAdapter(mListView, new ArrayAdapter<String>(
//                getActivity(), android.R.layout.simple_list_item_1,
//                mListViewTitles));
        
        setListViewAdapter(mListView, adapter);
        /*************Prasad***************/
      
        
        ImageButton addNew=(ImageButton) getView().findViewById(R.id.addnew);
        
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
            	showProfileNameAlert();                
            }
        });
        
        
	/**********************************************/
        }

    private void cancelAsyncTask(AsyncTask task) {
        if (task != null) task.cancel(false);
    }

    // //////////////////////////////////////////
    // ///////////// -- LOADER -- ///////////////
    // //////////////////////////////////////////

    private static class AsyncLoadSomething extends AsyncTask<Void, Void, String[]> {

        private static final String TAG = "AsyncLoadSomething";

        final WeakReference<ListViewFragment> weakFragment;

        public AsyncLoadSomething(ListViewFragment fragment) {
            this.weakFragment = new WeakReference<ListViewFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            final ListViewFragment audioFragment = weakFragment.get();
            if (audioFragment.mListView != null) audioFragment.mListView.setVisibility(View.INVISIBLE);
            if (audioFragment.mContentOverlay != null) audioFragment.mContentOverlay.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Void... voids) {


            return new String[]{"Test"};
        }

        @Override
        protected void onPostExecute(String[] titles) {
            super.onPostExecute(titles);
            final ListViewFragment audioFragment = weakFragment.get();
            if (audioFragment == null) {
                return;
            }

            if (audioFragment.mContentOverlay != null) audioFragment.mContentOverlay.setVisibility(View.GONE);
            audioFragment.setListViewTitles();
        }
    }
    
    
    /**********************/
    
    public void addItem(ProfileData geo)
    {
    //    Log.d("change", "additem");

    	 ProfilsPref.setProfileData(geo);
    	 Toast.makeText(getActivity().getApplicationContext(), "Added: "+geo.getGeofenceId(), Toast.LENGTH_SHORT).show();
         mItems.add(geo);
         adapter.notifyDataSetChanged();
         ((MainGeofenceActivity) getActivity()).registerGeofence(geo.getGeofenceId(), geo.getLatitude(), geo.getLongitude(), geo.getRadius());
    }
    public void deleteItem(ProfileData p)
    {
    	//ListItemHolder l=(ListItemHolder)v.getTag();
        ((MainGeofenceActivity) getActivity()).unregisterGeofence(p.getGeofenceId());
    	mItems.remove(p);
    	ProfilsPref.clearProfileByID(p.getGeofenceId());
    	adapter.notifyDataSetChanged();

    }
    public void setItemEnabled(ListItemHolder l,boolean b)
    {
    	if(b)
    	{
    	//ListItemHolder l=(ListItemHolder)v.getTag();
    		Toast.makeText(getActivity(),"Enabled "+l.profiledata.getGeofenceId(), Toast.LENGTH_SHORT).show();
        ((MainGeofenceActivity) getActivity()).registerGeofence(l.profiledata.getGeofenceId(),l.profiledata.getLatitude(),l.profiledata.getLongitude(), l.profiledata.getRadius());
        ProfilsPref.setProfileData(l.profiledata);
    	}
    	else
    	{
    		Toast.makeText(getActivity(),"Disabled "+l.profiledata.getGeofenceId(), Toast.LENGTH_SHORT).show();

            ((MainGeofenceActivity) getActivity()).unregisterGeofence(l.profiledata.getGeofenceId());
            ProfilsPref.setProfileData(l.profiledata);
    	}

    }
    public void editChange(ProfileData profiledata)
    {
        
        ((MainGeofenceActivity) getActivity()).registerGeofence(profiledata.getGeofenceId(),profiledata.getLatitude(),profiledata.getLongitude(), profiledata.getRadius());
	
    }
    void showProfileNameAlert()
    {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

    	alert.setTitle("New Profile");
    	alert.setIcon(R.drawable.ic_launcher);
    	alert.setCancelable(false);
    	// Set an EditText view to get user input 
    	final EditText input = new EditText(getActivity());
    	input.setHint("Set profile name");
    	
    	
    	alert.setView(input);
    	
    	alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) 
    	{
    		
    		if(ProfilsPref.isProfilePresent(input.getText().toString().trim()))
    		{
    			input.setText("");
    			Toast.makeText(getActivity(), "You already have a profile with this name", Toast.LENGTH_SHORT).show();
    		}
    		else
    		{
	    		Intent intent = new Intent(getActivity(),MapActivity.class);
	    		intent.putExtra("profilename",input.getText().toString().trim());
	    		startActivityForResult(intent,127);
    		}
    	}
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) 
    	  {
                dialog.dismiss();
    	  }
    	});
    	
    	
    	AlertDialog a=alert.create();
    	a.show();
    	final Button theButton = a.getButton(DialogInterface.BUTTON_POSITIVE);
    	theButton.setEnabled(false);
    	
    	input.addTextChangedListener(new TextWatcher() {
    		
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
    			// TODO Auto-generated method stub
    		}
    		
    		@Override
    		public void beforeTextChanged(CharSequence s, int start, int count,
    				int after) {
    			// TODO Auto-generated method stub
    			
    		}
    		
    		@Override
    		public void afterTextChanged(Editable s) {
    			if(!s.toString().trim().equals(""))
    			{
    				theButton.setEnabled(true);
    			}
    			else
    			{
    				theButton.setEnabled(false);
    			}
    			
    		}
    	});
    }
    
    
    @Override
   	public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
   		// TODO Auto-generated method stub
   		super.onActivityResult(requestCode, resultCode, data);
   				
   		if(resultCode== Activity.RESULT_OK)
		{
   			

			if(requestCode==127)
			{				
				if(data.getExtras().containsKey("profilename")&&data.getExtras().containsKey("ringermode")
						&&data.getExtras().containsKey("lat")&&data.getExtras().containsKey("log")
						&&data.getExtras().containsKey("radius"))
				{
					 ProfileData geo = new ProfileData(); 
					 geo.setGeofenceId(data.getStringExtra("profilename"));
					 geo.setLatitude(data.getDoubleExtra("lat",0));
					 geo.setLongitude(data.getDoubleExtra("log",0));
					 geo.setRadius(data.getIntExtra("radius",0));
					 geo.setRingerMode(data.getStringExtra("ringermode"));
					 geo.setEnabled(true);
					
		             addItem(geo);
		             					
				}
				
			}
		}
   	}

	public void showProfileOnMap(ProfileData profiledata) 
	{
	
		Intent intent = new Intent(getActivity(),EditMapActivity.class);
		intent.putExtra("profilename",profiledata.getGeofenceId());
		intent.putExtra("lat",profiledata.getLatitude());
		intent.putExtra("log",profiledata.getLongitude());
		intent.putExtra("radius",profiledata.getRadius());

		startActivityForResult(intent,128);	}
    
    /**********************/
    


}
