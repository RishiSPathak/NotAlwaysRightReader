package ca.pathak.rishi.notalwaysrightreader.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import ca.pathak.rishi.notalwaysrightreader.ArticleListHolder;
import ca.pathak.rishi.notalwaysrightreader.DatePickers.DatePickingAddDateDialog;
import ca.pathak.rishi.notalwaysrightreader.DatePickers.DatePickingEndDialog;
import ca.pathak.rishi.notalwaysrightreader.DatePickers.DatePickingStartDialog;
import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionDateError1;
import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionDateError2;
import ca.pathak.rishi.notalwaysrightreader.FetchResults;
import ca.pathak.rishi.notalwaysrightreader.MyStringFunctions;
import ca.pathak.rishi.notalwaysrightreader.R;

public class MainActivity extends AppCompatActivity {

    //public activity access to this function
    public static MainActivity mainActivity;

    //public activity access for in app article browser
    public static ViewArticlesActivity viewArticlesActivity = null;

    //date variables
    public static LocalDate StartDate, EndDate;
    public static List<LocalDate> listOfDates;

    //string constants
    public static final String firstallURL = "https://notalwaysright.com/all/";
    public static final String firstrightURL = "https://notalwaysright.com/";
    public static final String firstworkingURL = "https://notalwaysright.com/working/";
    public static final String firstromanticURL = "https://notalwaysright.com/romantic/";
    public static final String firstrelatedURL = "https://notalwaysright.com/related/";
    public static final String firstlearningURL = "https://notalwaysright.com/learning/";
    public static final String firstfriendlyURL = "https://notalwaysright.com/friendly/";
    public static final String firsthopelessURL = "https://notalwaysright.com/hopeless/";
    public static final String firsthealthyURL = "https://notalwaysright.com/healthy/";
    public static final String firstlegalURL = "https://notalwaysright.com/legal/";
    public static final String firstunfilteredURL = "https://notalwaysright.com/unfiltered/";
    public static final String baseallURL = "https://notalwaysright.com/all/page/";
    public static final String baserightURL = "https://notalwaysright.com/page/";
    public static final String baseworkingURL = "https://notalwaysright.com/working/page/";
    public static final String baseromanticURL = "https://notalwaysright.com/romantic/page/";
    public static final String baserelatedURL = "https://notalwaysright.com/related/page/";
    public static final String baselearningURL = "https://notalwaysright.com/learning/page/";
    public static final String basefriendlyURL = "https://notalwaysright.com/friendly/page/";
    public static final String basehopelessURL = "https://notalwaysright.com/hopeless/page/";
    public static final String basehealthyURL = "https://notalwaysright.com/healthy/page/";
    public static final String baselegalURL = "https://notalwaysright.com/legal/page/";
    public static final String baseunfilteredURL = "https://notalwaysright.com/unfiltered/page/";
    public static final String URLsuffix = "/";

    //Display elements
    TextView textViewDateRangeSelectedDates, textViewSingleDateSelectedDates, textViewDateListSelectedDates, textViewCurrentTask;
    Button buttonDateMatch, buttonStartDate, buttonEndDate, buttonSetDate, ButtonAddDate, ButtonRemoveFirstDate, ButtonResetDates, buttonViewArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //start activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;

        //initialize the date-time library
        AndroidThreeTen.init(this);

        //setup default date values
        StartDate = LocalDate.now();
        EndDate = LocalDate.now();
        listOfDates = new ArrayList<LocalDate>();
        listOfDates.add(StartDate);
        String StartDayOfWeek = StartDate.getDayOfWeek().toString();
        Log.d("Initial Date is: ", MyStringFunctions.DateToStringDisplayVersion(StartDate));

        //setup title bar
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.menu_bar_text_section);

        //setup progress bar, activity indicator, the handler
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setMax(100);
        final ProgressBar activityIndicator = (ProgressBar) findViewById(R.id.activityIndicator);
        activityIndicator.setVisibility(View.GONE);
        final Handler progressBarHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (bundle.getBoolean("visible")) {
                    progressBar.setVisibility(View.VISIBLE);
                    activityIndicator.setVisibility(View.VISIBLE);
                    textViewCurrentTask.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    activityIndicator.setVisibility(View.INVISIBLE);
                    textViewCurrentTask.setVisibility(View.INVISIBLE);
                }
                progressBar.setProgress(bundle.getInt("progress"));
                textViewCurrentTask.setText(bundle.getString("task"));
                super.handleMessage(msg);
            }
        };

        final Handler noResultsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(),"No results found.",Toast.LENGTH_LONG).show();
                super.handleMessage(msg);
            }
        };

        final Handler URLReadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(),"Error: no reponce from notalwaysright.com.",Toast.LENGTH_LONG).show();
                super.handleMessage(msg);
            }
        };

        final Handler updateUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                updateUI();
            }
        };

        //setup buttons
        Button buttonAll = (Button) findViewById(R.id.buttonAll);
        buttonAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("all", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstallURL, baseallURL, URLsuffix);
            }
        });
        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("right", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstrightURL, baserightURL, URLsuffix);
            }
        });
        Button buttonWorking = (Button) findViewById(R.id.buttonWorking);
        buttonWorking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("working", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstworkingURL, baseworkingURL, URLsuffix);
            }
        });
        Button buttonRomantic = (Button) findViewById(R.id.buttonRomantic);
        buttonRomantic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("romantic", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstromanticURL, baseromanticURL, URLsuffix);
            }
        });
        Button buttonRelated = (Button) findViewById(R.id.buttonRelated);
        buttonRelated.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("related", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstrelatedURL, baserelatedURL, URLsuffix);
            }
        });
        Button buttonLearning = (Button) findViewById(R.id.buttonLearning);
        buttonLearning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("learning", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstlearningURL, baselearningURL, URLsuffix);
            }
        });
        Button buttonFriendly = (Button) findViewById(R.id.buttonFriendly);
        buttonFriendly.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("friendly", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstfriendlyURL, basefriendlyURL, URLsuffix);
            }
        });
        Button buttonHopeless = (Button) findViewById(R.id.buttonHopeless);
        buttonHopeless.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("hopeless", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firsthopelessURL, basehopelessURL, URLsuffix);
            }
        });
        Button buttonHealthy = (Button) findViewById(R.id.buttonHealthy);
        buttonHealthy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("healthy", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firsthealthyURL, basehealthyURL, URLsuffix);
            }
        });
        Button buttonLegal = (Button) findViewById(R.id.buttonLegal);
        buttonLegal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("legal", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstlegalURL, baselegalURL, URLsuffix);
            }
        });
        Button buttonUnfiltered = (Button) findViewById(R.id.buttonUnfiltered);
        buttonUnfiltered.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("unfiltered", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstunfilteredURL, baseunfilteredURL, URLsuffix);
            }
        });
        Button buttonAllAndUnfiltered = (Button) findViewById(R.id.buttonAllAndUnfiltered);
        buttonAllAndUnfiltered.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_search ("all", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstallURL, baseallURL, URLsuffix);
                start_search ("unfiltered", progressBarHandler, noResultsHandler, updateUIHandler, URLReadHandler, firstunfilteredURL, baseunfilteredURL, URLsuffix);
            }
        });
        buttonDateMatch = (Button) findViewById(R.id.buttonMatchDates);
        buttonDateMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EndDate = StartDate;
                updateUI();
            }
        });
        buttonSetDate = (Button) findViewById(R.id.buttonSetDate);
        buttonSetDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickingStartDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        buttonStartDate = (Button) findViewById(R.id.buttonStartDate);
        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickingStartDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        buttonEndDate = (Button) findViewById(R.id.buttonEndDate);
        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickingEndDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        ButtonAddDate = (Button) findViewById(R.id.buttonAddDate);
        ButtonAddDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickingAddDateDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });
        ButtonRemoveFirstDate = (Button) findViewById(R.id.buttonRemoveFirstDate);
        ButtonRemoveFirstDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (listOfDates.size() < 2) {
                    Toast.makeText(getApplicationContext(),"Error: Cannot have no selected date.",Toast.LENGTH_LONG).show();
                } else {
                    listOfDates.remove(0);
                    updateUI();
                }
            }
        });
        ButtonResetDates = (Button) findViewById(R.id.buttonResetDates);
        ButtonResetDates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartDate = LocalDate.now();
                EndDate = StartDate;
                listOfDates = new ArrayList<LocalDate>();
                listOfDates.add(StartDate);
                updateUI();
            }
        });
        buttonViewArticles = (Button) findViewById(R.id.buttonViewArticles);
        buttonViewArticles.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.mainActivity, ViewArticlesActivity.class);
                startActivity(intent);
            }
        });

        //setup the UI
        textViewDateRangeSelectedDates = (TextView) findViewById(R.id.textViewDateRangeSelectedDates);
        textViewSingleDateSelectedDates = (TextView) findViewById(R.id.textViewSingleDateSelectedDates);
        textViewDateListSelectedDates = (TextView) findViewById(R.id.textViewDateListSelectedDates);
        textViewCurrentTask = (TextView) findViewById(R.id.textViewCurrentTask);
        textViewCurrentTask.setVisibility(View.GONE);
        updateUI();
    }

    //add menu icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_icon_section, menu);
        return true;
    }

    //handle menu icons being used
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                //load settings menu
                Log.d("MENU", "settings activated from menu");
                Intent intent = new Intent(this, SettingsActivityV2.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume () {
        super.onResume();
        updateUI();
    }

    private void start_search (String category, Handler progressBarHandler, Handler noResultsHandler, Handler updateUIHandler, Handler URLReadHandler, String firstURL, String baseURL, String URLsuffix) {
        String task = "Fetching " + category + " from " + MyStringFunctions.DateToStringDisplayVersionShort(StartDate) + " to " + MyStringFunctions.DateToStringDisplayVersionShort(EndDate);
        FetchResults execute = new FetchResults(firstURL, baseURL, URLsuffix, StartDate, EndDate, listOfDates, progressBarHandler, noResultsHandler, URLReadHandler, updateUIHandler, task);
        execute.execute();
    }

    private List<LocalDate> getDates (LocalDate startDate, LocalDate endDate) throws ExceptionDateError1, ExceptionDateError2 {

        //check for the enddate coming before the startdate
        if (startDate.isAfter(endDate)) {
            throw new ExceptionDateError1();
        }

        //check to see if enddate is in the future
        LocalDate thisDate = LocalDate.now();
        if (endDate.isAfter(thisDate)) {
            throw new ExceptionDateError2();
        }

        //make a list and fill it with days
        List<LocalDate> days = new ArrayList<LocalDate>();
        if (startDate.compareTo(endDate) == 0) {
            //if gates are the same then there is only one date to add
            days.add(startDate);
            return days;
        } else {
            for (LocalDate currentDate=startDate; currentDate.isAfter(endDate) == false; currentDate=currentDate.plusDays(1)) {
                days.add(currentDate);
            }
            return days;
        }
    }

    public void updateUI() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.mainActivity);
        String defaultValue = getResources().getString(R.string.default_search_mode);
        String searchMode = settings.getString("appMode", defaultValue);
        defaultValue = MainActivity.mainActivity.getResources().getString(R.string.default_article_opening);
        String openMode = settings.getString("openMode", defaultValue);

        if (searchMode.equals("Single Date")) {
            buttonDateMatch.setVisibility(View.INVISIBLE);
            buttonStartDate.setVisibility(View.INVISIBLE);
            buttonEndDate.setVisibility(View.INVISIBLE);
            textViewDateRangeSelectedDates.setVisibility(View.INVISIBLE);
            buttonSetDate.setVisibility(View.VISIBLE);
            textViewSingleDateSelectedDates.setVisibility(View.VISIBLE);
            ButtonAddDate.setVisibility(View.INVISIBLE);
            ButtonRemoveFirstDate.setVisibility(View.INVISIBLE);
            ButtonResetDates.setVisibility(View.INVISIBLE);
            textViewDateListSelectedDates.setVisibility(View.INVISIBLE);

            EndDate = StartDate;

            try {
                listOfDates = getDates(StartDate, EndDate);
            } catch (ExceptionDateError1 exceptionDateError) {
                Toast.makeText(getApplicationContext(),"Error: End date must be after the start date.",Toast.LENGTH_LONG).show();
            } catch (ExceptionDateError2 exceptionDateError2) {
                Toast.makeText(getApplicationContext(),"Error: End date cannot be in the future.",Toast.LENGTH_LONG).show();
            }

            textViewSingleDateSelectedDates.setText("Selected Date: " + MyStringFunctions.DateToStringDisplayVersion(StartDate));
        } else if (searchMode.equals("Range of Dates")) {
            buttonDateMatch.setVisibility(View.VISIBLE);
            buttonStartDate.setVisibility(View.VISIBLE);
            buttonEndDate.setVisibility(View.VISIBLE);
            textViewDateRangeSelectedDates.setVisibility(View.VISIBLE);
            buttonSetDate.setVisibility(View.INVISIBLE);
            textViewSingleDateSelectedDates.setVisibility(View.INVISIBLE);
            ButtonAddDate.setVisibility(View.INVISIBLE);
            ButtonRemoveFirstDate.setVisibility(View.INVISIBLE);
            ButtonResetDates.setVisibility(View.INVISIBLE);
            textViewDateListSelectedDates.setVisibility(View.INVISIBLE);

            try {
                listOfDates = getDates(StartDate, EndDate);
            } catch (ExceptionDateError1 exceptionDateError) {
                Toast.makeText(getApplicationContext(),"Error: End date must be after the start date.",Toast.LENGTH_LONG).show();
            } catch (ExceptionDateError2 exceptionDateError2) {
                Toast.makeText(getApplicationContext(),"Error: End date cannot be in the future.",Toast.LENGTH_LONG).show();
            }

            textViewDateRangeSelectedDates.setText("Start Date: " + MyStringFunctions.DateToStringDisplayVersion(StartDate) + "\nEnd Date: " + MyStringFunctions.DateToStringDisplayVersion(EndDate));
        } else if (searchMode.equals("List of Dates")) {
            buttonDateMatch.setVisibility(View.INVISIBLE);
            buttonStartDate.setVisibility(View.INVISIBLE);
            buttonEndDate.setVisibility(View.INVISIBLE);
            textViewDateRangeSelectedDates.setVisibility(View.INVISIBLE);
            buttonSetDate.setVisibility(View.INVISIBLE);
            textViewSingleDateSelectedDates.setVisibility(View.INVISIBLE);
            ButtonAddDate.setVisibility(View.VISIBLE);
            ButtonRemoveFirstDate.setVisibility(View.VISIBLE);
            ButtonResetDates.setVisibility(View.VISIBLE);
            textViewDateListSelectedDates.setVisibility(View.VISIBLE);

            String textBoxText = "Current Dates:\n";
            for (LocalDate date : listOfDates) {
                textBoxText = textBoxText + MyStringFunctions.DateToStringDisplayVersion(date) + "\n";
            }
            textBoxText = textBoxText.substring(0, textBoxText.length()-1);
            textViewDateListSelectedDates.setText(textBoxText);
        }

        if (openMode.equals("In Browser")) {
            buttonViewArticles.setVisibility(View.INVISIBLE);
            ArticleListHolder.get_instance().empty();
        } else if (ArticleListHolder.get_instance().get_number_of_URLs() > 0) {
            buttonViewArticles.setVisibility(View.VISIBLE);
        } else {
            buttonViewArticles.setVisibility(View.INVISIBLE);
        }

        if (viewArticlesActivity != null) {
            viewArticlesActivity.updateUI();
        }
    }



}
