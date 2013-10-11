package org.jtb.ninjatemp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationService extends Service {
  static final String ACTION_START = NotificationService.class.getName() + ".ACTION_START";
  static final String ACTION_STOP = NotificationService.class.getName() + ".ACTION_STOP";

  private final Set<String> notifications = new HashSet<String>();

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      return START_NOT_STICKY;
    }

    String action = intent.getAction();
    if (TextUtils.isEmpty(action)) {
      action = ACTION_START;
    }

    if (ACTION_START.equals(action)) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      boolean show = prefs.getBoolean("notification", true);
      if (show) {
        notifications.clear();
        new Fetcher(this) {
          @Override
          protected void onComplete(Device device, HeartbeatResponse heartbeat, DataResponse data) {
            doNotification(device, heartbeat, data);
          }
        }.fetch();
        schedule();
      }
    } else {
      cancel();
      stopForeground(true);
      NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      for (String guid: notifications) {
        nm.cancel(guid.hashCode());
      }
      notifications.clear();
      stopSelf();
    }

    return Service.START_STICKY;
  }

  private void cancel() {
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(this, StartReceiver.class), 0);
    alarmManager.cancel(pi);
  }

  private void schedule() {
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(this, StartReceiver.class), 0);
    alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
  }

  private void doNotification(Device device, HeartbeatResponse heartbeatResponse, DataResponse dr) {
    Units units = Units.getUnits(this);

    GraphNotification notification = new GraphNotification(this, units);
    Notification n = notification.notify(device, heartbeatResponse, dr.getDataPoints());
    if (n == null) {
      return;
    }

    if (notifications.isEmpty()) {
      startForeground(device.getGuid().hashCode(), n);
      notifications.add(device.getGuid());
    } else {
      NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      nm.notify(device.getGuid().hashCode(), n);
      notifications.add(device.getGuid());
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
