
package com.prasad.viewpager;

import com.prasad.profilsworld.R;
import com.prasad.geofence.AppDataStore;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class LayoutThree extends Fragment {


	public static Fragment newInstance(Context context) {
		LayoutThree f = new LayoutThree();	
		
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		 ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_three, null);	
		  final CheckBox c=(CheckBox)root.findViewById(R.id.checkBox1);

		Button b=(Button)root.findViewById(R.id.buttonGot);
		  b.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            Toast.makeText(getActivity(), "You are ready to start", Toast.LENGTH_SHORT).show();
		            if(c.isChecked())
		            	new AppDataStore(getActivity()).setFirstUsed(true);
		            getActivity().finish();
		        }
		    });
		return root;
	}
	
}
