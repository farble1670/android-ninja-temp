package org.jtb.ninjatemp;

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

}
