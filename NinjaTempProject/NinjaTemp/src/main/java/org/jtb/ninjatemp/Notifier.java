package org.jtb.ninjatemp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
import java.util.List;

import uk.ac.cam.cl.dtg.snowdon.AreaGraphView;

public class Notifier {
  private final Context context;
  private final Units units;

  public Notifier(Context context, Units units) {
    this.context = context;
    this.units = units;
  }

  void notify(Device device, HeartbeatResponse heartbeat, List<DataPoint> points) {
    NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification.Builder builder = new Notification.Builder(context);

    //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification);

    ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.notification_graph, null);
    AreaGraphView graph = (AreaGraphView) view.findViewById(R.id.graph);
    new GraphMaker(context, Units.FAHRENHEIT).create(graph, points);
    Bitmap bitmap = getBitmap(view);
    //views.setImageViewBitmap(R.id.image, bitmap);

    String text = String.format("%s: %s", device.getShortName(), GraphMaker.getTempString(units.getValue(heartbeat.getDa())));

    Notification n = new Notification.BigPictureStyle(
            new Notification.Builder(context)
                    .setContentTitle(text)
                    .setContentText(text)
                    .setSmallIcon(android.R.drawable.stat_sys_warning))
                    .bigPicture(bitmap).build();



    notifyManager.notify(device.getGuid().hashCode(), n);
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
//
//    try {
//      FileOutputStream out = new FileOutputStream(new File("/sdcard/test.png"));
//      b.compress(Bitmap.CompressFormat.PNG, 90, out);
//      out.close();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
    return b;
  }
}
