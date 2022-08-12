package ca.pathak.rishi.notalwaysrightreader.Activities;


import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import ca.pathak.rishi.notalwaysrightreader.R;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //start activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //load settings
        SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.PreferencesName), Context.MODE_PRIVATE);
        String defaultString;
        Boolean defaultBoolean;

        //setup back button
        Button buttonBack = (Button) findViewById(R.id.buttonSettingsBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //setup app mode setting spinner (including default position & listeners)
        Spinner appModeSpinner = (Spinner) findViewById(R.id.modeSelectorSpinner);
        defaultString = getResources().getString(R.string.default_search_mode);
        String currentAppMode = settings.getString("appMode", defaultString);
        ArrayAdapter<String> appModeSpinnerArrayAdapter = (ArrayAdapter<String>) appModeSpinner.getAdapter();
        int startingAppModeSpinnerPosition = appModeSpinnerArrayAdapter.getPosition(currentAppMode);
        appModeSpinner.setSelection(startingAppModeSpinnerPosition);
        appModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.PreferencesName), Context.MODE_PRIVATE);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putString("appMode", parent.getItemAtPosition(position).toString());
                settingsEditor.apply();
                MainActivity.mainActivity.updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, user made no change
            }
        });

        //setup article opening mode setting spinner (including default position & listeners)
        Spinner articleOpeningSpinner = (Spinner) findViewById(R.id.articleOpenSelectorSpinner);
        defaultString = getResources().getString(R.string.default_article_opening);
        String articleOpeningMode = settings.getString("openMode", defaultString);
        ArrayAdapter<String> openModeSpinnerArrayAdapter = (ArrayAdapter<String>) articleOpeningSpinner.getAdapter();
        int startingOpenModeSpinnerPosition = openModeSpinnerArrayAdapter.getPosition(articleOpeningMode);
        articleOpeningSpinner.setSelection(startingOpenModeSpinnerPosition);
        articleOpeningSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.PreferencesName), Context.MODE_PRIVATE);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putString("openMode", parent.getItemAtPosition(position).toString());
                settingsEditor.apply();
                MainActivity.mainActivity.updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, user made no change
            }
        });

        //setup only open new article switch
        SwitchCompat onlyOpenNewSwitch = (SwitchCompat) findViewById(R.id.articleOnlyOpenNewSwitch);
        defaultBoolean = false;
        boolean articleOpenNewOnly = settings.getBoolean("openNewOnly", defaultBoolean);
        onlyOpenNewSwitch.setChecked(articleOpenNewOnly);
        onlyOpenNewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.PreferencesName), Context.MODE_PRIVATE);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putBoolean("openNewOnly", isChecked);
                settingsEditor.apply();
                MainActivity.mainActivity.updateUI();
            }
        });

        //setup clear articles button
        final RadioButton clearReadArticles = (RadioButton) findViewById(R.id.radioButtonClearReadArticles);
        clearReadArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clearReadArticles.isChecked()) {
                    clearReadArticles.setChecked(false);
                    Toast.makeText(getApplicationContext(),"Read Articles Cleared.",Toast.LENGTH_LONG).show();
                    SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.StoredArticlesName), Context.MODE_PRIVATE);
                    SharedPreferences.Editor settingsEditor = settings.edit();
                    settingsEditor.clear();
                    settingsEditor.apply();
                }
            }
        });
    }
}
