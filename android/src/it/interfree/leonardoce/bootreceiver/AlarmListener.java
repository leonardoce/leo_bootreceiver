/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package it.interfree.leonardoce.bootreceiver;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmListener  extends BroadcastReceiver {
	private static final String LTAG = "LeoAlarmListener";
	@Override 
	public void onReceive(Context context, Intent intent) {
		Log.d(LTAG, "Ricevo l'allarme");

        // Play the alarm alert and vibrate the device.
        Log.d(LTAG, "Attivo il servizio...");
        Intent playAlarm = new Intent(context, AlarmKlaxon.class);
        context.startService(playAlarm);
	}
}
