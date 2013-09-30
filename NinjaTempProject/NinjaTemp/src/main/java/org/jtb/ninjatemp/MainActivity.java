package org.jtb.ninjatemp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
  private ListView list;
  private SensorAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    list = (ListView) findViewById(R.id.list);
  }

  @Override
  protected void onResume() {
    super.onResume();
    refresh();
  }

  private void refresh() {
    new RequestTask("/v0/devices", DevicesResponse.class) {
      @Override
      protected void onPostExecute(Response response) {
        if (response == null || response.getResult() != 1) {
          return;
        }
        DevicesResponse dr = (DevicesResponse) response;
        List<Device> tempDevices = new ArrayList<Device>();
        for (Device device: dr.getDevices()) {
          if (device.getDeviceType().equals("temperature")) {
            tempDevices.add(device);
          }
        }
        adapter = new SensorAdapter(MainActivity.this, tempDevices);
        list.setAdapter(adapter);
      }
    }.execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_refresh:
        refresh();
        return true;
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
