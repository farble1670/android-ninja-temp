package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPoint extends Response {
  DataPoint(JSONObject data) {
    super(data);
  }

  String getTime() {
    return data.optString("t");
  }

  String getValue() {
    return data.optString("v");
  }
}
