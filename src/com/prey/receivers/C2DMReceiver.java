/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.receivers;

import java.util.Iterator;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.prey.FileConfigReader;
import com.prey.PreyConfig;
import com.prey.PreyController;
import com.prey.PreyLogger;
import com.prey.PushMessage;
import com.prey.activities.FeedbackActivity;
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;

public class C2DMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			handleMessage(context, intent);
		}
	}

	private void handleMessage(Context context, Intent intent) {
 
		String pushedMessage = intent.getExtras().getString(PreyConfig.getPreyConfig(context).getc2dmAction());
		
		Set<String> set=intent.getExtras().keySet();
		Iterator<String>  ite=set.iterator();
		while(ite.hasNext()){
			String key=ite.next();
			PreyLogger.i("key_:"+key+" value:"+intent.getExtras().getString(key));
		}
		String version=intent.getExtras().getString("version");
		if ("beta".equals(version)){
			handleMessageBeta(context, pushedMessage);
		}else{
			handleMessageMaster(context, pushedMessage);
		}
	}
	
	
	private void handleMessageBeta(Context context, String pushedMessage) {
	    try {
			PreyLogger.i("Push notification received, waking up Prey right now!");
			PreyLogger.i("Push message received " + pushedMessage);
			PreyController.startPrey(context);
		} catch (Exception e) {
			PreyLogger.e("Push execution failed to run", e);
		}
	}
	private void handleMessageMaster(Context context, String pushedMessage) {	
		 
		
 
		PreyLogger.i("Push notification received, waking up Prey right now!");
		PreyLogger.i("Push message received " + pushedMessage);
		if (pushedMessage != null) {
		 
			try {
				PushMessage pMessage = new PushMessage(pushedMessage);
				
				boolean feedback = pushedMessage.indexOf("feedback") >= 0;
				feedback=false;
				if (feedback) {
					PreyConfig.getPreyConfig(context).setFlagFeedback(FeedbackActivity.FLAG_FEEDBACK_C2DM);
				} else {
					boolean shouldPerform = pushedMessage.indexOf("run") >= 0;
					boolean shouldStop = pushedMessage.indexOf("stop") >= 0;
					shouldPerform=true;
					if (shouldPerform) {
						PreyLogger.i("Push notification received, waking up Prey right now!");
						PreyController.startPrey(context);
					} else {
						if (shouldStop) {
							PreyLogger.i("Push notification received, stopping Prey!");
							PreyController.stopPrey(context);
						}
					}
					PreyConfig.getPreyConfig(context).setRunOnce(pMessage.getBody().indexOf("run_once") >= 0);
				}
			} catch (PreyException e) {
				PreyLogger.e("Push execution failed to run", e);
			}
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		String registration = intent.getStringExtra("registration_id");
		if (intent.getStringExtra("error") != null) {
			PreyLogger.d("Couldn't register to c2dm: " + intent.getStringExtra("error"));

		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistration done, new messages from the authorized sender will
			// be rejected
			PreyLogger.d("Unregistered from c2dm: " + intent.getStringExtra("unregistered"));
		} else if (registration != null) {
			PreyLogger.i("Registration id: " + registration);
			new UpdateCD2MId().execute(registration, context);
			// Send the registration ID to the 3rd party site that is sending
			// the messages.
			// This should be done in a separate thread.
			// When done, remember that all registration is done.
		}
	}

	private class UpdateCD2MId extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... data) {
			try {
				String registration = FileConfigReader.getInstance((Context) data[1]).getGcmIdPrefix() + (String) data[0];
				PreyLogger.i("Registration id: " + registration);
				PreyWebServices.getInstance().setPushRegistrationId((Context) data[1], registration);

			} catch (Exception e) {

				PreyLogger.e("Failed registering to CD2M: " + e.getLocalizedMessage(), e);
			}
			return null;
		}

	}

}
