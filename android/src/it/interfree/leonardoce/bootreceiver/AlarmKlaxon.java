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

import android.app.NotificationManager;
import android.app.Service;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.support.v4.app.NotificationCompat;

/**
 * Manages alarms and vibe. Runs as a service so that it can continue to play
 * if another activity overrides the AlarmAlert dialog.
 */
public class AlarmKlaxon extends Service {
    private static final int NOTIFICATION_ID=11221;
    
    private static final boolean LOGV = true;
    private static final String LTAG = AlarmKlaxon.class.getName();

    /** Play alarm up to 10 minutes before silencing */
    private static final int ALARM_TIMEOUT_SECONDS = 1 * 60;

    private static final long[] sVibratePattern = new long[] { 500, 500 };

    private boolean mPlaying = false;
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;
    private long mStartTime;
    private int mInitialCallState;

    private static final int KILLER = 1000;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case KILLER:
                    Log.v(LTAG, "*********** Alarm killer triggered ***********");
                    
                    // Here we create a notification to let the user know, when he will
                    // take the phone, that he must take the pill

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

                    PendingIntent pendingIntent = PendingIntent.getActivity(AlarmKlaxon.this, 0, intentApplicazione,
									    PendingIntent.FLAG_UPDATE_CURRENT);
                    
                    NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(AlarmKlaxon.this)
                        .setContentTitle("MemoFarma")
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                        .setContentText("Alarm!")
                        .setContentIntent(pendingIntent)
			.setAutoCancel(true);
                    
                    NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    
                    stopSelf();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        // Listen for incoming calls to kill the alarm.
        AlarmAlertWakeLock.acquireCpuWakeLock(this);
    }

    @Override
    public void onDestroy() {
        stop();
        // Stop listening for incoming calls.
        AlarmAlertWakeLock.releaseCpuLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // No intent, tell the system not to restart us.
        if (intent == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        play();

        return START_STICKY;
    }

    private void play() {
        // stop() checks to see if we are already playing.
        stop();

        if (LOGV) {
            Log.v(LTAG, "AlarmKlaxon.play() ");
        }

        Uri alert = RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_ALARM);
        if (LOGV) {
            Log.v(LTAG, "Using default alarm: " + alert.toString());
        }

        // TODO: Reuse mMediaPlayer instead of creating a new one and/or use
        // RingtoneManager.
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(LTAG, "Error occurred while playing audio.");
                mp.stop();
                mp.release();
                mMediaPlayer = null;
                return true;
            }
        });

        try {
            // Check if we are in a call. If we are, use the in-call alarm
            // resource at a low volume to not disrupt the call.
            mMediaPlayer.setDataSource(this, alert);
            startAlarm(mMediaPlayer);
        } catch (Exception ex) {
            Log.v(LTAG, "Non trovo la suoneria dell'allarme???");
        }

        /* Start the vibrator after everything is ok with the media player */
        mVibrator.vibrate(sVibratePattern, 0);
        mPlaying = true;
        mStartTime = System.currentTimeMillis();
        enableKiller();
    }

    // Do the common stuff when starting the alarm.
    private void startAlarm(MediaPlayer player)
            throws java.io.IOException, IllegalArgumentException,
                   IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        // do not play alarms if stream volume is 0
        // (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0
            && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            player.start();
        }
    }

    private void setDataSourceFromResource(Resources resources,
            MediaPlayer player, int res) throws java.io.IOException {
        AssetFileDescriptor afd = resources.openRawResourceFd(res);
        if (afd != null) {
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
            afd.close();
        }
    }

    /**
     * Stops alarm audio and disables alarm if it not snoozed and not
     * repeating
     */
    public void stop() {
        if (LOGV) Log.v(LTAG, "AlarmKlaxon.stop()");
        if (mPlaying) {
            mPlaying = false;

            // Stop audio playing
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            // Stop vibrator
            mVibrator.cancel();
        }
        disableKiller();
    }

    /**
     * Kills alarm audio after ALARM_TIMEOUT_SECONDS, so the alarm
     * won't run all day.
     *
     * This just cancels the audio, but leaves the notification
     * popped, so the user will know that the alarm tripped.
     */
    private void enableKiller() {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(KILLER),
                1000 * ALARM_TIMEOUT_SECONDS);
    }

    private void disableKiller() {
        mHandler.removeMessages(KILLER);
    }
}
