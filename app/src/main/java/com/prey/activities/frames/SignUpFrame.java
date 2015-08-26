package com.prey.activities.frames;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.R;
import com.prey.activities.WelcomeActivity;

public class SignUpFrame extends Fragment {
	
	
	private WelcomeActivity welcome;
	
	
	
 


	public void setActivity(WelcomeActivity welcome) {
		this.welcome = welcome;
	}


	@Override
	  public void onResume() {
	     PreyLogger.i("onResume of SignUpFrame");
	     super.onResume();
	  }

	  @Override
	  public void onPause() {
		  PreyLogger.i("OnPause of SignUpFrame");
	    super.onPause();
	  }
	  

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Defines the xml file for the fragment
      View view = inflater.inflate(R.layout.signup, container, false);
      
      
      Button button =(Button)view.findViewById(R.id.buttonSignup);
      button.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			PreyConfig.getPreyConfig(getActivity()).setProtectAccount(true);
			welcome.menu();
		}
      });
      
      
      TextView linkSignup =(TextView)view.findViewById(R.id.linkSignup);
      linkSignup.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			welcome.signIn();
		}
      });
      return view;
	}

}