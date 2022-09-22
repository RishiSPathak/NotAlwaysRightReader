package ca.pathak.rishi.notalwaysrightreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.pathak.rishi.notalwaysrightreader.Activities.MainActivity;
import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionDateNotFound;
import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionNoInternetError;

public class FetchResults extends AsyncTask<Void, Void, String> {

    private String FirstUrl, BaseUrl, URLSuffix, Task;
    private List<LocalDate> Dates;
    private LocalDate StartDate, EndDate;
    private Handler ProgressBarHandler;
    private Handler NoResultsHandler;
    private Handler UpdateUIHandler;
    private Handler URLReadHandler;

    private static final int minProgress = 2;
    private static final int maxProgress = 98;

    public FetchResults (String firstUrl, String baseURL, String URLsuffix, LocalDate startDate, LocalDate endDate, List<LocalDate> dates, Handler progressBarHandler, Handler noResultsHandler, Handler urlReadHandler, Handler updateUIHandler, String task) {
        FirstUrl = firstUrl;
        BaseUrl = baseURL;
        URLSuffix = URLsuffix;
        StartDate = startDate;
        EndDate = endDate;
        Dates = dates;
        ProgressBarHandler = progressBarHandler;
        NoResultsHandler = noResultsHandler;
        UpdateUIHandler = updateUIHandler;
        URLReadHandler = urlReadHandler;
        Task = task;
    }

    @Override
    protected String doInBackground(Void... params) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.mainActivity);
        String defaultValue = MainActivity.mainActivity.getResources().getString(R.string.default_article_opening);
        String openMode = settings.getString("openMode", defaultValue);
        boolean onlyOpenNew = settings.getBoolean("openNewOnly", false);

        List<String> results = new ArrayList<String>();
        List<String> resultNames = new ArrayList<String>();
        List<LocalDate> resultDates = new ArrayList<LocalDate>();
        try {
            extract_date_entries(FirstUrl, BaseUrl, URLSuffix, StartDate, EndDate, Dates, ProgressBarHandler, Task, results, resultNames, resultDates, openMode, onlyOpenNew);

            if (results.isEmpty()) {
                Message message = NoResultsHandler.obtainMessage();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                NoResultsHandler.sendMessage(message);
            } else if (openMode.equals("In Browser")) {
                for (String result : results) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                    MainActivity.mainActivity.startActivity(browserIntent);
                    Log.d("Result: ", result);
                }
            } else {
                for (int i = 0; i < results.size(); i++) {
                    ArticleListHolder.get_instance().add_URL(resultNames.get(i), results.get(i), resultDates.get(i));
                }
                Message message = UpdateUIHandler.obtainMessage();
                Bundle bundle = new Bundle();
                message.setData(bundle);
                UpdateUIHandler.sendMessage(message);
            }

        } catch (ExceptionNoInternetError exceptionNoInternetError) {
            Log.e("URL Read Error: ", "fetchresults aborted due to internet failure");
            //UI Cleanup
            Message message = ProgressBarHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("visible", false);
            message.setData(bundle);
            ProgressBarHandler.sendMessage(message);

            message = URLReadHandler.obtainMessage();
            bundle = new Bundle();
            message.setData(bundle);
            URLReadHandler.sendMessage(message);
        }
        return "";
    }

    private static void extract_date_entries(String firstURL, String baseURL, String URLsuffix, LocalDate startDate, LocalDate endDate, List<LocalDate> dates, Handler progressBarHandler, String task, List<String> results, List<String> resultNames, List<LocalDate> resultDates, String openMode, boolean onlyOpenNew) throws ExceptionNoInternetError {
        // variables to track progress
        int counter = 1;
        boolean unfinished = true;

        //start progress bar
        Message message = progressBarHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean("visible", true);
        bundle.putInt("progress", minProgress);
        bundle.putString("task", task);
        message.setData(bundle);
        progressBarHandler.sendMessage(message);

        while (unfinished) {
            // Generate URL to read from
            String URL;
            if (counter == 1) {
                URL = firstURL;
            } else {
                URL = baseURL + counter + URLsuffix;
            }

            // load the webpage
            URLExaminer webpage = new URLExaminer(URL);

            //check if there are results on this page or if the last result has been found
            boolean URLsOnPage = false;
            LocalDate currentDate = LocalDate.now();
            while (true) {

                boolean wantedDate = false;
                for (LocalDate date : dates) {
                    if (currentDate.isEqual(date)) {
                        wantedDate = true;
                    }
                }

                if (wantedDate && webpage.check_for_text(MyStringFunctions.DateToStringWebsiteVersion(currentDate))) {
                    URLsOnPage = true;
                    break;
                } else if (webpage.check_for_text(MyStringFunctions.DateToStringWebsiteVersion(currentDate))) {
                    break;
                } else if (currentDate.isBefore(startDate)) {
                    unfinished = false;
                    break;
                } else {
                    currentDate = currentDate.minusDays(1);
                }
            }

            if (URLsOnPage) {
                //find the URLs on this Page
                Elements links = webpage.get_links("a[rel=bookmark]");

                for (Element link : links) {
                    boolean relevantDateFound = false;
                    boolean URLNotYetIncluded = false;
                    boolean notPreviouslyRead = false;

                    //load the link's data
                    URLExaminer result = new URLExaminer(link.attr("abs:href"));
                    LocalDate resultDate = LocalDate.now();

                    //check if the link contains one of the dates being searched for
                    for (LocalDate date : dates) {
                        if (result.check_for_text(MyStringFunctions.DateToStringWebsiteVersion(date))) {
                            relevantDateFound = true;
                        }
                    }

                    //check if the link is already included
                    if (results.contains(link.attr("abs:href")) == false) {
                        URLNotYetIncluded = true;
                    }

                    //check if result should be excluded due to being previously read
                    if (onlyOpenNew) {
                        try {
                            resultDate = result.getDate();

                            SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.StoredArticlesName), Context.MODE_PRIVATE);
                            Set<String> list = settings.getStringSet(MyStringFunctions.DateToStringStorageVersion(resultDate), new HashSet<String>());

                            if (list.contains(link.attr("abs:href")) == false) {
                                notPreviouslyRead = true;
                            }
                        } catch (ExceptionDateNotFound exceptionDateNotFound) {
                            //too far in past just let it pass
                            Log.d("Date Error", "Date is too far in past to find");
                            notPreviouslyRead = true;
                            resultDate = LocalDate.now().minusYears(5);
                        }

                    } else {
                        notPreviouslyRead = true;
                    }

                    //if both requirements are met then add the URL and update the UI
                    if (relevantDateFound && URLNotYetIncluded && notPreviouslyRead) {
                        results.add(link.attr("abs:href"));
                        resultNames.add(link.text());
                        resultDates.add(resultDate);

                        //store item as read if opened in browser, otherwise wait untill opened in app to sore as read
                        if (openMode.equals(MainActivity.mainActivity.getResources().getString(R.string.default_article_opening))) {
                            try {
                                LocalDate newResultDate = result.getDate();

                                SharedPreferences settings = MainActivity.mainActivity.getSharedPreferences(MainActivity.mainActivity.getString(R.string.StoredArticlesName), Context.MODE_PRIVATE);
                                Set<String> list = settings.getStringSet(MyStringFunctions.DateToStringStorageVersion(newResultDate), new HashSet<String>());
                                Set<String> newList = new HashSet<String>(list);
                                newList.add(link.attr("abs:href"));

                                SharedPreferences.Editor settingsEditor = settings.edit();
                                settingsEditor.putStringSet(MyStringFunctions.DateToStringStorageVersion(newResultDate), newList);
                                settingsEditor.apply();
                            } catch (ExceptionDateNotFound exceptionDateNotFound) {
                                //too far in past do nothing
                                Log.d("Date Error", "Date is too far in past to find");
                            }
                        }

                        //update progress bar
                        message = progressBarHandler.obtainMessage();
                        bundle = new Bundle();
                        bundle.putBoolean("visible", true);
                        int factor = dates.size();
                        if (firstURL.equals(MainActivity.firstallURL)) {
                            factor = dates.size() * 18;
                        } else if (firstURL.equals(MainActivity.firstrightURL)) {
                            factor = dates.size() * 8;
                        } else if (firstURL.equals(MainActivity.firstworkingURL)) {
                            factor = dates.size() * 8;
                        } else if (firstURL.equals(MainActivity.firstunfilteredURL)) {
                            factor = dates.size() * 5;
                        }
                        bundle.putInt("progress", Math.min(maxProgress, Math.max(minProgress, 100 * results.size() / factor)));
                        bundle.putString("task", task);
                        message.setData(bundle);
                        progressBarHandler.sendMessage(message);
                    }
                }
            }

            // increment counter
            counter++;
        }

        //end progress bar
        message = progressBarHandler.obtainMessage();
        bundle = new Bundle();
        bundle.putBoolean("visible", false);
        bundle.putInt("progress", 0);
        message.setData(bundle);
        progressBarHandler.sendMessage(message);
    }
}
