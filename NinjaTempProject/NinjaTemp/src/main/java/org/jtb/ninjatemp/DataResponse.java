package org.jtb.ninjatemp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataResponse extends Response {
  DataResponse(String content) throws JSONException {
    super(content);
  }

  List<DataPoint> getDataPoints() {
    List<DataPoint> dataPoints = new ArrayList<DataPoint>();
    JSONArray points = data.optJSONArray("data");
    for (int i = 0; i < points.length(); i++) {
      JSONObject p = points.optJSONObject(i);
      if (p != null) {
        dataPoints.add(new DataPoint(p));
      }
    }
    return dataPoints;
  }
}
