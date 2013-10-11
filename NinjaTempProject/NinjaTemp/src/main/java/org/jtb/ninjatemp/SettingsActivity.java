package org.jtb.ninjatemp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setResult(RESULT_CANCELED);

    addPreferencesFromResource(R.xml.settings);
  }


  @Override
  protected void onPause() {
    PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    super.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    setResult(RESULT_OK);
  }
}