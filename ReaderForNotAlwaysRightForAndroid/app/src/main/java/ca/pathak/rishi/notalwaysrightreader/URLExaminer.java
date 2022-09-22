package ca.pathak.rishi.notalwaysrightreader;

import android.util.Log;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.threeten.bp.LocalDate;

import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionDateNotFound;
import ca.pathak.rishi.notalwaysrightreader.Exceptions.ExceptionNoInternetError;

public class URLExaminer {
    private String URL;
    private Document document;
    private String html;

    public URLExaminer(String url) throws ExceptionNoInternetError {
        super();
        URL = url;
        document = null;
        try {
            document = Jsoup.connect(URL).get();
            html = document.html();
        } catch (IOException e) {
            Log.e("Error cannot read: ", URL);
            throw new ExceptionNoInternetError();
        }

    }

    public boolean check_for_text (String text) {
        return html.toLowerCase().contains(text.toLowerCase());
    }

    public int find_number_of_occurrences(String text) {
        String webSiteText = html.toLowerCase();
        text = text.toLowerCase();
        int count = 0;
        int index = webSiteText.indexOf(text);
        while (index != -1) {
            count++;
            webSiteText =  webSiteText.substring(index + 1);
            index = webSiteText.indexOf(text);
        }
        return count;
    }

    public Elements get_links (String restrictions) {
        return document.select(restrictions);
    }

    public LocalDate getDate() throws ExceptionDateNotFound {
        LocalDate today = LocalDate.now();
        LocalDate date = LocalDate.now();
        while (true) {
            if (check_for_text(MyStringFunctions.DateToStringWebsiteVersion(date))) {
                return date;
            } else if (date.equals(today.minusYears(5))) {
                throw new ExceptionDateNotFound();
            } else {
                date = date.minusDays(1);
            }
        }
    }
}

