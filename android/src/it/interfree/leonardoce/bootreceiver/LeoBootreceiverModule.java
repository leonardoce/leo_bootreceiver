package it.interfree.leonardoce.bootreceiver;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Kroll.module(name="LeoBootreceiver", id="it.interfree.leonardoce.bootreceiver")
public class LeoBootreceiverModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "LeoBootreceiverModule";
	private static final boolean DBG = TiConfig.LOGD;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public LeoBootreceiverModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	// Methods
	@Kroll.method
	public String example()
	{
		Log.d(LCAT, "example called");
		return "hello world";
	}

	// Properties
	@Kroll.getProperty
	public String getExampleProp()
	{
		Log.d(LCAT, "get example property");
		return "hello world";
	}


	@Kroll.setProperty
	public void setExampleProp(String value) {
		Log.d(LCAT, "set example property: " + value);
	}

	@Kroll.method
	public void clearAllAlarms() {
		Log.d(LCAT, "Cancellazione di tutti gli allarmi");

		AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.cancel (pendingIntent);
	}

	@Kroll.method
	public void addAlarm(int ore, int minuti, int idTerapia) {
		Log.d(LCAT, "Aggiungo allarme alle [" + ore + ":" + minuti +"] per [" + idTerapia + "]");

		AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar defaultDay = Calendar.getInstance();
		int day = defaultDay.get(Calendar.DAY_OF_MONTH);
		int month = defaultDay.get(Calendar.MONTH);
		int year = defaultDay.get(Calendar.YEAR);
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, ore);
		cal.add(Calendar.MINUTE, minuti);
		cal.add(Calendar.SECOND, 0);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000*60*60*24, pendingIntent);
	}

	/**
	 * Prende il contesto dell'applicazione
	 */
	private Context getContext() {
		return TiApplication.getInstance().getApplicationContext();
	}
}

