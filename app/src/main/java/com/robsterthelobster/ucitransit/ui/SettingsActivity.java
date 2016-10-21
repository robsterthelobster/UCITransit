package com.robsterthelobster.ucitransit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ftinc.scoop.Scoop;
import com.ftinc.scoop.ui.ScoopSettingsActivity;
import com.robsterthelobster.ucitransit.R;
import com.robsterthelobster.ucitransit.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by robin on 10/17/2016.
 */
public class SettingsActivity extends AppCompatActivity{

    @BindView(R.id.settings_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scoop.getInstance().apply(this);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_settings, new SettingsFragment())
                .commit();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.RC_CHANGE_THEME){
            Log.d("settings", "recreate");
            recreate();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            Preference pref = getPreferenceManager().findPreference(getString(R.string.key_theme_pref));
            pref.setOnPreferenceClickListener(preference -> {
                startActivityForResult(ScoopSettingsActivity.createIntent(getActivity()),
                        Constants.RC_CHANGE_THEME);
                return true;
            });
        }
    }
}
