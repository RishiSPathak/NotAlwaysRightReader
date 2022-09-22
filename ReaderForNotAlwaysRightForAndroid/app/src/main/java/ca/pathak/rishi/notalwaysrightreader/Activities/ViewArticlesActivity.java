package ca.pathak.rishi.notalwaysrightreader.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.threeten.bp.LocalDate;

import java.util.HashSet;
import java.util.Set;

import ca.pathak.rishi.notalwaysrightreader.ArticleListHolder;
import ca.pathak.rishi.notalwaysrightreader.MyStringFunctions;
import ca.pathak.rishi.notalwaysrightreader.R;

public class ViewArticlesActivity extends Activity {

    private ListView displayedListOfArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //start activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_articles);

        //register with MainActivity
        MainActivity.viewArticlesActivity = this;

        //setup buttons
        Button buttonBack = (Button) findViewById(R.id.buttonViewArticlesBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //setup buttons
        Button buttonClear = (Button) findViewById(R.id.buttonClearAllArticles);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArticleListHolder.get_instance().empty();
                updateUI();
            }
        });

        //setup list of articles
        displayedListOfArticles = (ListView) findViewById(R.id.listViewDisplayedListOfArticles);
        displayedListOfArticles.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                //get the URL and date for that item
                String URL = ArticleListHolder.get_instance().get_URL(position);
                LocalDate itemDate = ArticleListHolder.get_instance().get_Date(position);

                //remove that item from the list
                ArticleListHolder.get_instance().remove_item(position);

                //add the item to the list of viewed items
                SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.StoredArticlesName), Context.MODE_PRIVATE);
                Set<String> list = settings.getStringSet(MyStringFunctions.DateToStringStorageVersion(itemDate), new HashSet<String>());
                Set<String> newList = new HashSet<String>(list);
                newList.add(URL);

                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putStringSet(MyStringFunctions.DateToStringStorageVersion(itemDate), newList);
                settingsEditor.apply();

                //remove item from the list
                updateUI();

                //open the URL
                Intent intent = new Intent(MainActivity.viewArticlesActivity, WebsiteViewActivity.class);
                intent.putExtra("URL", URL);
                startActivity(intent);
            }
        });

        updateUI();
    }

    public void updateUI() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArticleListHolder.get_instance().get_titles());
        displayedListOfArticles.setAdapter(arrayAdapter);
    }
}
