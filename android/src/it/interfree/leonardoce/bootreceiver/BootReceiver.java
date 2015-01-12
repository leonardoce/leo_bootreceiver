// This file is part of MemoFarma.
//
// MemoFarma is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// MemoFarma is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with MemoFarma.  If not, see <http://www.gnu.org/licenses/>.

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
