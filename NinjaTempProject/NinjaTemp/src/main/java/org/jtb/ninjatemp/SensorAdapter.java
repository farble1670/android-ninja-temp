package org.jtb.ninjatemp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cl.dtg.snowdon.AreaGraphView;

class SensorAdapter extends BaseAdapter {
  private final Context context;
  private final List<Device> devices;
  private final Units units;

  private SensorElement[] elements;

  SensorAdapter(Context context, List<Device> devices) {
    this.context = context;
    this.devices = new ArrayList<Device>(devices);

    this.elements = new SensorElement[devices.size()];

    this.units = Units.getUnits(context);
  }

  @Override
  public int getCount() {
    return devices.size();
  }

  @Override
  public SensorElement getItem(int i) {
    return elements[i];
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.item_sensor, null);
    }

    ViewGroup statusLayout = (ViewGroup) view.findViewById(R.id.layout_status);
    TextView statusText = (TextView) view.findViewById(R.id.text_status);
    ViewGroup dataLayout = (ViewGroup) view.findViewById(R.id.layout_data);

    TextView nameText = (TextView) view.findViewById(R.id.text_name);
    nameText.setText(devices.get(i).getShortName());

    SensorElement element = getItem(i);
    if (element == null) {
      elements[i] = new SensorElement();

      dataLayout.setVisibility(View.GONE);
      statusLayout.setVisibility(View.VISIBLE);
      statusText.setText("Requesting sensor data ...");

      getData(i, devices.get(i));
    } else {
      dataLayout.setVisibility(View.VISIBLE);
      statusLayout.setVisibility(View.GONE);

      ViewGroup heartbeatLayout = (ViewGroup) view.findViewById(R.id.layout_heartbeat);
      if (element.heartbeat == null || element.heartbeat.getResult() != 1) {
        heartbeatLayout.setVisibility(View.GONE);
      } else {
        heartbeatLayout.setVisibility(View.VISIBLE);

        TextView tempText = (TextView) view.findViewById(R.id.text_temp);
        float tempValue = units.getValue(element.heartbeat.getDa());
        tempText.setText(GraphMaker.getTempString(tempValue));

        TextView lastText = (TextView) view.findViewById(R.id.text_last);
        lastText.setText(String.format("Last heartbeat: %s", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(element.heartbeat.getTimestamp())));
      }

      ViewGroup historyLayout = (ViewGroup) view.findViewById(R.id.layout_history);
      if (element.data == null || element.data.getResult() != 1) {
        historyLayout.setVisibility(View.GONE);
      } else {
        historyLayout.setVisibility(View.VISIBLE);

        AreaGraphView graph = (AreaGraphView) view.findViewById(R.id.graph_temp);
        new GraphMaker(context, units).create(graph, element.data.getDataPoints());
      }
    }

    return view;
  }

  private void getData(final int i, Device device) {

    new RequestTask(String.format("/v0/device/%s/heartbeat", device.getGuid()), HeartbeatResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        HeartbeatResponse hbr = (HeartbeatResponse) response;
        SensorElement element = elements[i];
        element.heartbeat = hbr;
        notifyDataSetChanged();
      }
    }.execute();

    Map<String, String> query = new HashMap<String, String>();
    long now = System.currentTimeMillis();
    Period period = Period.getPeriod(context);
    query.put("interval", period.interval);
    query.put("from", Long.toString(now - period.timeMillis));
    query.put("to", Long.toString(now));
    query.put("fn", "mean");

    new RequestTask(String.format("/v0/device/%s/data", device.getGuid()), DataResponse.class, query) {
      @Override
      protected void onPostExecute(Response response) {
        if (response.getResult() != 1) {
          return;
        }
        DataResponse dr = (DataResponse) response;
        SensorElement element = elements[i];
        element.data = dr;
        notifyDataSetChanged();
      }
    }.execute();
  }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }
}
