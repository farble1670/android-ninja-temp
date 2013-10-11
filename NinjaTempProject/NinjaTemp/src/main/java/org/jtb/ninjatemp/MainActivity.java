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
  private static final int REQUEST_SETTINGS = 1;

  private ListView list;
  private SensorAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    list = (ListView) findViewById(R.id.list);

    refresh();
  }

  private void refresh() {
    adapter = new SensorAdapter(MainActivity.this);
    list.setAdapter(adapter);

    startService(new Intent(this, NotificationService.class));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
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
        startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_SETTINGS);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_SETTINGS) {
      if (resultCode == RESULT_OK) {
        refresh();
      }
    }
  }
}
