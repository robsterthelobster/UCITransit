package com.robsterthelobster.ucitransit.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.robsterthelobster.ucitransit.R;

/**
 * Created by robin on 10/17/2016.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

}
