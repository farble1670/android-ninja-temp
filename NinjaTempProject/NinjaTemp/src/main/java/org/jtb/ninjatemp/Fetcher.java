package org.jtb.ninjatemp;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Fetcher {
  private final Context context;

  Fetcher(Context context) {
    this.context = context;
  }

  void fetch() {
    doGetDevices();
  }

  void fetch(Device device) {
    doGetHeartbeat(device);
  }

  protected void onDevice(Device device) {
  }

  protected void onHeartbeat(Device device, HeartbeatResponse heartbeat) {
  }

  protected void onComplete(Device device, HeartbeatResponse heartbeat, DataResponse data) {
  }

  private void doGetDevices() {
    new RequestTask("/v0/devices", DevicesResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response == null || response.getResult() != 1) {
          return;
        }
        DevicesResponse dr = (DevicesResponse) response;
        for (Device device : dr.getDevices()) {
          if (device.getDeviceType().equals("temperature")) {
            onDevice(device);
            doGetHeartbeat(device);
          }
        }
      }
    }.execute();
  }

  private void doGetHeartbeat(final Device device) {
    new RequestTask(String.format("/v0/device/%s/heartbeat", device.getGuid()), HeartbeatResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        HeartbeatResponse hbr = (HeartbeatResponse) response;
        onHeartbeat(device, hbr);
        doGetData(device, hbr);
      }
    }.execute();
  }

  private void doGetData(final Device device, final HeartbeatResponse heartbeatResponse) {
    Map<String, String> query = new HashMap<String, String>();
    Period period = Period.getPeriod(context);
    Interval interval = Interval.obtain(period);
    query.put("interval", period.interval);

    query.put("from", Long.toString(interval.start));
    query.put("to", Long.toString(interval.end));
    query.put("fn", "mean");

    new RequestTask(String.format("/v0/device/%s/data", device.getGuid()), DataResponse.class, query) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        DataResponse dr = (DataResponse) response;
        onComplete(device, heartbeatResponse, dr);
      }
    }.execute();
  }

}
