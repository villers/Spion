package com.ghota.spi0n;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        findPreference("about").setOnPreferenceClickListener(
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                    adb.setTitle("About me");
                    adb.setMessage("Un message a propro de moi!!!!");
                    adb.setPositiveButton("Ok", null);
                    adb.show();
                    return true;
                }
            }
        );

        findPreference("cache").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        ImageLoader.getInstance().clearMemoryCache();
                        ImageLoader.getInstance().clearDiscCache();
                        Toast.makeText(SettingsActivity.this, "Le cache a été vidé", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );
    }
}
