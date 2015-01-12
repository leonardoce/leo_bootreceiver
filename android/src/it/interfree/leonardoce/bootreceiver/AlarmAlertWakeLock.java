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

import android.content.Context;
import android.os.PowerManager;
import org.appcelerator.kroll.common.Log;

/**
 * Hold a wakelock that can be acquired in the AlarmReceiver and
 * released in the AlarmAlert activity
 */
class AlarmAlertWakeLock {
    private final static String LCAT = AlarmAlertWakeLock.class.getName();
    private static PowerManager.WakeLock sCpuWakeLock;

    static void acquireCpuWakeLock(Context context) {
        Log.v(LCAT, "Acquiring cpu wake lock");
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, LCAT);
        sCpuWakeLock.acquire();
    }

    static void releaseCpuLock() {
        Log.v(LCAT, "Releasing cpu wake lock");
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
