package org.jtb.ninjatemp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DataPoint extends Response {
  private static final DateFormat ISO_8601_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  private static class DataPointComparator implements Comparator<DataPoint> {

    @Override
    public int compare(DataPoint dataPoint1, DataPoint dataPoint2) {
      long t1 = dataPoint1.getTimeMillis();
      long t2 = dataPoint2.getTimeMillis();

      if (t1 < t2) {
        return -1;
      }
      if (t1 > t2) {
        return 1;
      }
      return 0;
    }
  }

  private static final Comparator<DataPoint> DATA_POINT_COMPARATOR = new DataPointComparator();

  DataPoint(JSONObject data) {
    super(data);
  }

  String getTimeString() {
    return data.optString("t");
  }

  long getTimeMillis() {
    try {
      long t = ISO_8601_FORMAT.parse(getTimeString()).getTime();
      t += TimeZone.getDefault().getRawOffset();
      return t;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  String getValue() {
    return data.optString("v");
  }

  static void sort(List<DataPoint> points) {
    Collections.sort(points, DATA_POINT_COMPARATOR);
  }
}
