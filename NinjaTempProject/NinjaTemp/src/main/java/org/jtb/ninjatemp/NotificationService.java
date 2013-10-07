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
        doGetDevices();
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

  private void doGetDevices() {
    new RequestTask("/v0/devices", DevicesResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response == null || response.getResult() != 1) {
          return;
        }
        DevicesResponse dr = (DevicesResponse) response;
        List<Device> tempDevices = new ArrayList<Device>();
        for (Device device : dr.getDevices()) {
          if (device.getDeviceType().equals("temperature")) {
            doGetHeartbeat(device);
          }
        }
      }
    }.execute();
  }

  private void doGetHeartbeat(final Device device) {
    new RequestTask(String.format("/v0/device/%s/heartbeat", device.getGuid()), HeartbeatResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        HeartbeatResponse hbr = (HeartbeatResponse) response;
        doGetData(device, hbr);
      }
    }.execute();
  }

  private void doGetData(final Device device, final HeartbeatResponse heartbeatResponse) {
    Map<String, String> query = new HashMap<String, String>();
    Period period = Period.getPeriod(this);
    query.put("interval", period.interval);

    long now = System.currentTimeMillis();

    query.put("from", Long.toString(now - period.timeMillis));
    query.put("to", Long.toString(now));
    query.put("fn", "mean");

    new RequestTask(String.format("/v0/device/%s/data", device.getGuid()), DataResponse.class, query) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        DataResponse dr = (DataResponse) response;
        doNotification(device, heartbeatResponse, dr);
      }
    }.execute();
  }

  private void doNotification(Device device, HeartbeatResponse heartbeatResponse, DataResponse dr) {
    Units units = Units.getUnits(this);

    GraphNotification notification = new GraphNotification(this, units);
    Notification n = notification.notify(device, heartbeatResponse, dr.getDataPoints());

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
