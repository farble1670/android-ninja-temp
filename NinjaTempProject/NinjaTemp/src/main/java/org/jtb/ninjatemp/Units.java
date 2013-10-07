package org.jtb.ninjatemp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

enum Units {
  FAHRENHEIT, CELSIUS;

  static float toFahrenheit(float celsius) {
    return celsius * 1.8f + 32.0f;
  }

  float getValue(String cs) {
    return getValue(Float.valueOf(cs));
  }

  float getValue(float celsius) {
    if (this == Units.CELSIUS) {
      return celsius;
    }

    return toFahrenheit(celsius);
  }

  static Units getUnits(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Units units = Units.valueOf(prefs.getString("units", Units.FAHRENHEIT.name()));
    return units;
  }
}
