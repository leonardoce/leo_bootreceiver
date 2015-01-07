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
	private static final long MILLIS_IN_DAY = 1000*60*60*24;

	// Questo ID di terapia (fittizio) serve per distinguere l'allarme
	// derivato da una ripetizione di un allarme gia' dato.
	private static final int ID_PER_RIPETIZIONE = 54321;

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
	public void clearAlarm(int idTerapia) {
		Log.d(LCAT, "Cancellazione dell'allarme per la terapia ["+idTerapia+"]");

		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), idTerapia, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		getAlarmManager().cancel (pendingIntent);
	}

	@Kroll.method
	public void addAlarmOggi(int ore, int minuti, int idTerapia) {
		Log.d(LCAT, "Aggiungo allarme OGGI alle [" + ore + ":" + minuti +"] per [" + idTerapia + "]");
		addAlarm(ore, minuti, idTerapia, 0);
	}

	@Kroll.method
	public void addAlarmDomani(int ore, int minuti, int idTerapia) {
		Log.d(LCAT, "Aggiungo allarme DOMANI alle [" + ore + ":" + minuti +"] per [" + idTerapia + "]");
		addAlarm(ore, minuti, idTerapia, MILLIS_IN_DAY);
	}

	private void addAlarm(int ore, int minuti, int idTerapia, long delta) {
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), idTerapia, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		long tempoBase = generaTimestamp(ore, minuti) + delta;
		if(tempoBase<System.currentTimeMillis()) {
			Log.d(LCAT, "Questo allarme e' gia' passato. Schedulo a partire da domani.");
			tempoBase += MILLIS_IN_DAY;
		}

		getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, tempoBase, MILLIS_IN_DAY, pendingIntent);
	}

	@Kroll.method
	public void interrompiSuoneria() 
	{
		Log.d(LCAT, "Interrompo la suoneria...");
        Intent playAlarm = new Intent(getContext(), AlarmKlaxon.class);
        getContext().stopService(playAlarm);
	}

	@Kroll.method
	public void ripetiAllarmeFraMinuti(int minuti){
		Log.d(LCAT, "Aggiungi un allarme per ripetizione in ["+minuti+"] minuti");

		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), ID_PER_RIPETIZIONE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar cal = new GregorianCalendar();
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + minuti * 60 * 1000, pendingIntent);
	}

	@Kroll.method
	public void cancellaAllarmePerRipetizione() {
		clearAlarm(ID_PER_RIPETIZIONE);
	}

	/**
	 * Prende il contesto dell'applicazione
	 */
	private Context getContext() {
		return TiApplication.getInstance().getApplicationContext();
	}

	/**
 	 * Prende il gestore degli allarmi di questa applicazione
 	 */
	private AlarmManager getAlarmManager() {
		return (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * Genera il timestamp corrispondente alla giornata di oggi alle ore e minuti specificati
	 */
	private long generaTimestamp(int ore, int minuti) {
		Calendar defaultDay = Calendar.getInstance();
		int day = defaultDay.get(Calendar.DAY_OF_MONTH);
		int month = defaultDay.get(Calendar.MONTH);
		int year = defaultDay.get(Calendar.YEAR);
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, ore);
		cal.add(Calendar.MINUTE, minuti);
		cal.add(Calendar.SECOND, 0);

		return cal.getTimeInMillis();
	}
}

