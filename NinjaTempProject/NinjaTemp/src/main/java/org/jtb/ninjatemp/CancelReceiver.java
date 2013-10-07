package org.jtb.ninjatemp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Intent svcIntent = new Intent(context, NotificationService.class);
    svcIntent.setAction(NotificationService.ACTION_STOP);
    context.startService(svcIntent);
  }
}
