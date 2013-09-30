package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class DevicesResponse extends Response {

  DevicesResponse(String content) throws JSONException {
    super(content);
  }

  List<Device> getDevices() {
    List<Device> devices = new ArrayList<Device>();

    JSONObject root = data.optJSONObject("data");
    if (root == null) {
      return devices;
    }

    for (Iterator<String> i = root.keys(); i.hasNext();) {
      String guid = i.next();
      JSONObject deviceJo = root.optJSONObject(guid);
      if (deviceJo == null) {
        continue;
      }
      try {
        Device device = new Device(deviceJo, guid);
        devices.add(device);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    return devices;
  }
}
