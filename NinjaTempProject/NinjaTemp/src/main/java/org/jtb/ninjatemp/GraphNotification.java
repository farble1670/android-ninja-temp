package org.jtb.ninjatemp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.List;

import uk.ac.cam.cl.dtg.snowdon.AreaGraphView;

public class GraphNotification {
  private final Context context;
  private final Units units;

  public GraphNotification(Context context, Units units) {
    this.context = context;
    this.units = units;
  }

  Notification notify(Device device, HeartbeatResponse heartbeat, List<DataPoint> points) {
    if (points.size() < 2) {
      return null;
    }

    ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.notification_graph, null);
    AreaGraphView graph = (AreaGraphView) view.findViewById(R.id.graph);
    new GraphMaker(context, Units.FAHRENHEIT).create(graph, points);
    Bitmap bitmap = getBitmap(view);

    String title = String.format("%s: %s", device.getShortName(), GraphMaker.getTempString(units.getValue(heartbeat.getDa())));
    String summary = "";
    if (heartbeat.getTimestamp() != -1) {
      summary = String.format("Last heartbeat: %s", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(heartbeat.getTimestamp()));
    }

    float temp = Float.parseFloat(heartbeat.getDa());
    temp = Units.toFahrenheit(temp);

    String smallIconName;
    if (temp < 0) {
      smallIconName = String.format("ic_stat_minus_%2.0f", temp);
    } else {
      smallIconName = String.format("ic_stat_%2.0f", temp);
    }
    int smallIconId = context.getResources().getIdentifier(smallIconName, "drawable", context.getPackageName());
    if (smallIconId == 0) {
      smallIconId = R.drawable.undefined;
    }

    Notification n = new Notification.BigPictureStyle(
            new Notification.Builder(context)
                    .setContentIntent(PendingIntent.getBroadcast(context, 0, new Intent(context, CancelReceiver.class), 0))
                    .setContentTitle(title)
                    .setContentText(summary)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setSmallIcon(smallIconId))
            .bigPicture(bitmap)
            .setBigContentTitle(title)
            .setSummaryText(summary)
            .build();

    return n;
  }


  public static Bitmap getBitmap(View v) {
    if (v.getMeasuredHeight() <= 0) {
      v.measure(512, 256);
    }

    int w = v.getMeasuredWidth();
    int h = v.getMeasuredHeight();

    Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    v.layout(0, 0, w, h);
    v.draw(c);

    return b;
  }
}
