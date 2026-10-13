package com.fitplan.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;


import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String PREFS = "fitplan_reminders";
    private static final String CHANNEL = "fitplan_reminders_channel";
    @Override public void onReceive(Context context, Intent intent) {
        String kind = intent.getStringExtra("kind");
        if (kind == null) return;
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!p.getBoolean(kind + "_enabled", false)) return;
        createChannel(context);
        String title = kind.equals("sleep") ? "Đến giờ chuẩn bị ngủ" : "Đến giờ vận động";
        String body = kind.equals("sleep") ? "Hãy thư giãn và hướng tới 8–10 tiếng ngủ." : "Đến giờ tập theo lịch FitPlan. Khởi động trước nhé!";
        PendingIntent content = PendingIntent.getActivity(context, 700, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, CHANNEL) : new Notification.Builder(context);
        b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).setContentIntent(content);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(kind.equals("sleep") ? 702 : 701, b.build());
        scheduleNext(context, kind, p.getInt(kind + "_hour", kind.equals("sleep") ? 22 : 17), p.getInt(kind + "_minute", 0));
    }
    public static void createChannel(Context c) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "Nhắc nhở FitPlan", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Nhắc giờ tập luyện và chuẩn bị ngủ");
            NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }
    public static void schedule(Context c, String kind, int hour, int minute) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit().putBoolean(kind + "_enabled", true).putInt(kind + "_hour", hour).putInt(kind + "_minute", minute).apply();
        scheduleNext(c, kind, hour, minute);
    }
    private static void scheduleNext(Context c, String kind, int hour, int minute) {
        Calendar cal = Calendar.getInstance(); cal.set(Calendar.HOUR_OF_DAY, hour); cal.set(Calendar.MINUTE, minute); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        if (!cal.after(Calendar.getInstance())) cal.add(Calendar.DAY_OF_YEAR, 1);
        Intent i = new Intent(c, ReminderReceiver.class).putExtra("kind", kind);
        int request = kind.equals("sleep") ? 702 : 701;
        PendingIntent pi = PendingIntent.getBroadcast(c, request, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        else am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }
    public static void cancel(Context c, String kind) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(kind + "_enabled", false).apply();
        int request = kind.equals("sleep") ? 702 : 701;
        PendingIntent pi = PendingIntent.getBroadcast(c, request, new Intent(c, ReminderReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE); if (am != null) am.cancel(pi); pi.cancel();
    }
}
