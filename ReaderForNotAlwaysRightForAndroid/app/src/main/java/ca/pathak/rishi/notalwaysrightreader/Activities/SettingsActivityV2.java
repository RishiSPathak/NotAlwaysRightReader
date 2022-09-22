package ca.pathak.rishi.notalwaysrightreader.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ca.pathak.rishi.notalwaysrightreader.R;

public class SettingsActivityV2 extends AppCompatActivity {

    static SettingsActivityV2 thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_v2);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        thisActivity = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //setup the reset read items button
            Preference reset_read_items_button = findPreference("resetReadArticlesButton");
            reset_read_items_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //confirm they want to reset their read items
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setTitle(R.string.reset_read_articles_confirm_question);
                    builder.setMessage(thisActivity.getString(R.string.reset_read_articles_confirm_comment));
                    //builder.setIcon(R.drawable.ic_launcher);
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.mainActivity,"Read Articles Cleared.",Toast.LENGTH_LONG).show();
                            SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.StoredArticlesName), Context.MODE_PRIVATE);
                            SharedPreferences.Editor settingsEditor = settings.edit();
                            settingsEditor.clear();
                            settingsEditor.apply();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });
        }
    }
}