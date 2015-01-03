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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmListener  extends BroadcastReceiver {
	private static final String LTAG = "LeoAlarmListener";
	@Override 
	public void onReceive(Context context, Intent intent) {
		Log.d(LTAG, "Ricevo l'allarme");

		// Questo deve accendere il cellulare
        AlarmAlertWakeLock.acquireCpuWakeLock(context);

        // Play the alarm alert and vibrate the device.
        Log.d(LTAG, "Attivo il servizio...");
        Intent playAlarm = new Intent(context, AlarmKlaxon.class);
        context.startService(playAlarm);

        // Apri l'applicazione
        Log.d(LTAG, "Attivo l'applicazione...");
        Intent intentApplicazione = new Intent();
        intentApplicazione.setComponent(new ComponentName(TiApplication.getInstance().getApplicationContext().getPackageName(), 
        	"it.interfree.leonardoce.memofarma.MemofarmaActivity"));
        intentApplicazione.addFlags(
        	Intent.FLAG_ACTIVITY_NEW_TASK | 
        	Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | 
        	Intent.FLAG_ACTIVITY_SINGLE_TOP |
        	Intent.FLAG_ACTIVITY_NO_USER_ACTION);
    	intentApplicazione.addCategory(Intent.CATEGORY_LAUNCHER);
    	intentApplicazione.putExtra("tipologia", "terapie_non_somministrate");
    	context.startActivity(intentApplicazione);
	}
}
