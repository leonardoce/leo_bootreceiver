package it.interfree.leonardoce.bootreceiver;

import org.appcelerator.kroll.common.Log;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Questo serve per lanciare direttamente un servizio
 * al boot del cellulare
 * @author leonardo
 *
 */
public class BootReceiver extends BroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent) {
		final int interval_sec;
		final String class_name;
		
    	try {
			ActivityInfo ai = context.getPackageManager().getReceiverInfo(new ComponentName(context, BootReceiver.class.getName()), PackageManager.GET_META_DATA);
			interval_sec = ai.metaData.getInt("interval_sec");
			class_name = ai.metaData.getString("class_name");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
    	
    	Log.i(BootReceiver.class.getName(), "Boot receiver bootstrap");
    	
    	if (interval_sec==0)
    	{
    		Log.e(BootReceiver.class.getName(), "Missing interval_sec metadata");
    		return;
    	}
    	
    	if (class_name==null || class_name.length()==0)
    	{
    		Log.e(BootReceiver.class.getName(), "Missing class_name metadata");
    	}
    	
        try {
            Class aClass = Class.forName(class_name);
            Intent serviceIntent = new Intent(context, aClass);
            serviceIntent.putExtra("interval", interval_sec * 1000L);//every second
            context.startService(serviceIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
