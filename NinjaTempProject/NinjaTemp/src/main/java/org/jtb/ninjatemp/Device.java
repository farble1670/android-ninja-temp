package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

public class Device extends Response {
  private final String guid;

  Device(JSONObject data, String guid) throws JSONException {
    super(data);
    this.guid = guid;
  }

  String getGuid() {
    return guid;
  }

  public String getDeviceType() {
    String deviceType = data.optString("device_type");
    return deviceType;
  }

  public String getShortName() {
    String shortName = data.optString("shortName");
    return shortName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Device device = (Device) o;

    if (!guid.equals(device.guid)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return guid.hashCode();
  }
}
