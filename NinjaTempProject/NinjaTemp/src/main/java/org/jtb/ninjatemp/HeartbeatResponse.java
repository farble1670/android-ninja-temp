package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

public class HeartbeatResponse extends Response {
  HeartbeatResponse(String content) throws JSONException {
    super(content);
  }

  public String getGuid() {
    JSONObject deviceData = data.optJSONObject("data");
    if (deviceData == null) {
      return null;
    }
    return deviceData.optString("GUID");
  }

  public String getDa() {
    JSONObject deviceData = data.optJSONObject("data");
    if (deviceData == null) {
      return null;
    }
    return deviceData.optString("DA");
  }
}
