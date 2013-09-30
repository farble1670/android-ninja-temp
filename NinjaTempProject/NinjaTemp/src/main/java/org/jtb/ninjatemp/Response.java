package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

abstract class Response {
  protected final JSONObject data;

  int getResult() {
    return data.optInt("result", -1);
  }

  Response(String content) throws JSONException {
    data = new JSONObject(content);
  }

  Response(JSONObject data) {
    this.data = data;
  }
}
