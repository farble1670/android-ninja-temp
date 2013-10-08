package org.jtb.ninjatemp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

enum Period {
  ONE_HOUR(60 * 60 * 1000, "1min"), SIX_HOURS(6 * 60 * 60 * 1000, "1min"), TWELVE_HOURS(12 * 60 * 60 * 1000, "1min"), ONE_DAY(1 * 24 * 60 * 60 * 1000, "2min"), TWO_DAYS(2 * 24 * 60 * 60 * 1000, "8min"), ONE_WEEK(7 * 24 * 60 * 60 * 1000, "30min");

  final long timeMillis;
  final String interval;

  private Period(long timeMillis, String interval) {
    this.timeMillis = timeMillis;
    this.interval = interval;
  }

  static Period getPeriod(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Period period = Period.valueOf(prefs.getString("period", Period.ONE_DAY.name()));
    return period;
  }
}
