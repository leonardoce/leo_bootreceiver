package it.interfree.leonardoce.bootreceiver;

import org.appcelerator.kroll.common.Log;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;

/**
 * Questo serve per lanciare direttamente un servizio
 * al boot del cellulare
 * @author leonardo
 *
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String LCAT = BootReceiver.class.getName();

	@Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LCAT, "Attivazione del listener al boot...");

        // Qua lancio il controllo al boot dell'applicazione.
        // Vediamo cosa succede!
        Intent intentApplicazione = new Intent();
        intentApplicazione.setComponent(new ComponentName(TiApplication.getInstance().getApplicationContext().getPackageName(), 
            "it.interfree.leonardoce.memofarma.MemofarmaActivity"));
        intentApplicazione.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK | 
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | 
            Intent.FLAG_ACTIVITY_SINGLE_TOP |
            Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        intentApplicazione.addCategory(Intent.CATEGORY_LAUNCHER);
        intentApplicazione.putExtra("tipologia", "controllo_al_boot");
        context.startActivity(intentApplicazione);
    }
}
