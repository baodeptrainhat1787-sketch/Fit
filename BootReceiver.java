package com.fitplan.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context, Intent intent) {
  if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
  SharedPreferences p = context.getSharedPreferences(ReminderReceiver.PREFS, Context.MODE_PRIVATE);
  for (String kind : new String[]{"workout", "sleep"}) if (p.getBoolean(kind + "_enabled", false)) ReminderReceiver.schedule(context, kind, p.getInt(kind + "_hour", kind.equals("sleep") ? 22 : 17), p.getInt(kind + "_minute", 0));
 }
}
