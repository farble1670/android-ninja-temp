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
  private final List<SensorElement> elements;
  private final Units units;

  SensorAdapter(Context context) {
    this.context = context;
    this.elements = new ArrayList<SensorElement>();
    this.units = Units.getUnits(context);

    load();
  }

  void load() {
    new Fetcher(context){
      @Override
      protected void onDevice(Device device) {
        SensorElement element = new SensorElement();
        element.device = device;
        elements.add(element);
        notifyDataSetChanged();
      }

      @Override
      protected void onHeartbeat(Device device, HeartbeatResponse heartbeat) {
        for (SensorElement element: elements) {
          if (!element.device.equals(device)) {
            continue;
          }
          element.heartbeat = heartbeat;
          notifyDataSetChanged();
        }
      }

      @Override
      protected void onComplete(Device device, HeartbeatResponse heartbeat, DataResponse data) {
        for (SensorElement element: elements) {
          if (!element.device.equals(device)) {
            continue;
          }
          element.data = data;
          notifyDataSetChanged();
        }
      }
    }.fetch();
  }
  @Override
  public int getCount() {
    return elements.size();
  }

  @Override
  public SensorElement getItem(int i) {
    return elements.get(i);
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

    ViewGroup dataLayout = (ViewGroup) view.findViewById(R.id.layout_data);

    SensorElement element = getItem(i);

    TextView nameText = (TextView) view.findViewById(R.id.text_name);
    nameText.setText(element.device.getShortName());

    dataLayout.setVisibility(View.VISIBLE);

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

    return view;
  }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }
}
