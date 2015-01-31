package com.example.headeractionbar;

import java.util.List;

import com.prasad.profilsworld.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
public class CustomList extends ArrayAdapter<ProfileData>
{
private  Activity context;
private List<ProfileData> list;
private ListViewFragment lstFragment;


public CustomList(Activity context,ListViewFragment listViewFragment,List<ProfileData> list) 
{
	super(context, R.layout.list_single, list);
	this.context = context;
	this.list=list;
	lstFragment=listViewFragment;
//this.imageId = imageId;
}
@Override
public View getView(int position, View view, ViewGroup parent)
{
	View rowView=view;
	ListItemHolder itemholder;
	itemholder= new ListItemHolder();
	
		LayoutInflater inflater = context.getLayoutInflater();
		rowView= inflater.inflate(R.layout.list_single, null, true);
		
		itemholder.position=position;
		itemholder.profiledata=list.get(position);
		itemholder.txtTitle = (TextView) rowView.findViewById(R.id.list_title);
		itemholder.txtSubTitle = (TextView) rowView.findViewById(R.id.list_subtitile);
		itemholder.imgIcon = (ImageButton) rowView.findViewById(R.id.list_icon);
		itemholder.deleteBtn = (ImageButton) rowView.findViewById(R.id.list_delete);
		itemholder.switchEnable = (Switch) rowView.findViewById(R.id.list_enable_switch);
		itemholder.editBtn = (ImageButton) rowView.findViewById(R.id.list_edit);

		rowView.setTag(itemholder);
		itemholder.deleteBtn.setTag(itemholder);
		itemholder.editBtn.setTag(itemholder);
		itemholder.switchEnable.setTag(itemholder);
		itemholder.imgIcon.setTag(itemholder);
		
		itemholder.switchEnable.setChecked(itemholder.profiledata.isEnabled());
		itemholder.switchEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				ListItemHolder l=(ListItemHolder)buttonView.getTag();
		    	l.profiledata.setEnabled(isChecked);
				lstFragment.setItemEnabled(l, isChecked);
					
			}
		});

		itemholder.imgIcon.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) 
		    {
		    	
		    	ListItemHolder l=(ListItemHolder)v.getTag();
		    	lstFragment.showProfileOnMap(l.profiledata);
		    }
		});
		itemholder.deleteBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) 
		    {
		    	
		    	ListItemHolder l=(ListItemHolder)v.getTag();
		    	lstFragment.deleteItem(l.profiledata);
		    }
		});
		itemholder.editBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) 
		    {
		    	
		    	ListItemHolder l=(ListItemHolder)v.getTag();		    	
		    	showRingerAlert(l.profiledata);
			  

		    }
		    public void showRingerAlert(final ProfileData p)
		    {
		    	int mode=2;
		    	
		    	if(p.getRingerMode().equals("normal"))
		    	{
		    		mode=0;
		    	}else if(p.getRingerMode().equals("vibrate"))
		    	{
		    		mode=1;
		    	}else if(p.getRingerMode().equals("silent"))
		    	{
		    		mode=2;
		    	}
		    	 	
		    	final CharSequence[] items={"Normal","Vibrate","Silent"};
		    	AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
		    	builder.setTitle("Choose Ringer mode");
		    	builder.setPositiveButton("Done", new DialogInterface.OnClickListener() 
		    	{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						new ProfileDataStore(getContext()).setProfileData(p);
					  	lstFragment.adapter.notifyDataSetChanged();


					  	if(p.isEnabled())
					  	{
					  		lstFragment.editChange(p);

					  	}
					  	
					}
				});
		    	
		     	builder.setSingleChoiceItems(items,mode, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						
						if("Normal".equals(items[which]))
						{
							p.setRingerMode("normal");
							
						}
						else if("Vibrate".equals(items[which]))
						{
							p.setRingerMode("vibrate");

						}
						else if("Silent".equals(items[which]))
						{
							p.setRingerMode("silent");
						}
						
					}
				});
		    	builder.show();
		    }
		});

	
	itemholder.position=position;
	setupItem(itemholder);

return rowView;
}

private void setupItem(ListItemHolder itemholder) 
{
	  	itemholder.imgIcon.setImageResource(R.drawable.itemicon);
	  	itemholder.txtTitle.setText(itemholder.profiledata.getGeofenceId());
	  	itemholder.txtSubTitle.setText(itemholder.profiledata.getRingerMode());
	  	itemholder.switchEnable.setChecked(itemholder.profiledata.isEnabled());
	
}
@Override
public int getCount() {
	// TODO Auto-generated method stub
	return list.size();  
}

@Override
public long getItemId(int position) {
	// TODO Auto-generated method stub
	return position;
}


static class ListItemHolder
{
	ProfileData profiledata;
	int position;
    ImageButton imgIcon;
	ImageButton deleteBtn,editBtn;
    TextView txtTitle;
    TextView txtSubTitle;
    Switch switchEnable;
}
}