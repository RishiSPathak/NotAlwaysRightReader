package ca.pathak.rishi.notalwaysrightreader;

import org.threeten.bp.LocalDate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticleListHolder {

    private  static ArticleListHolder thisClass = null;
    
    private List<ListEntry> listOfURLs;

    private ArticleListHolder() {
        listOfURLs = new ArrayList<ListEntry>();
    }

    static public ArticleListHolder get_instance () {
        if (thisClass == null) {
            thisClass = new ArticleListHolder();
        }
        return thisClass;
    }
    
    public void add_URL (String toAddName, String toAddURL, LocalDate toAddDate) {
        add_URL(new ListEntry(toAddURL, toAddName, toAddDate));
    }

    public void add_URL (ListEntry toAdd) {
        if (!check_if_URL_contained(toAdd.getURL())) {
            listOfURLs.add(toAdd);
        }
    }

    public int get_number_of_URLs () {
        return listOfURLs.size();
    }

    public boolean check_if_URL_contained (String URL) {
        boolean contained = false;
        for (ListEntry element : listOfURLs) {
            if (element.getURL().equals(URL)) {
                contained = true;
                break;
            }
        }
        return  contained;
    }

    public void empty () {
        listOfURLs = new ArrayList<ListEntry>();
    }

    public void remove_item(int position) {
        listOfURLs.remove(position);
    }

    public String get_URL(int position) {
        return listOfURLs.get(position).getURL();
    }

    public String[] get_titles() {
        String[] titles = new String[listOfURLs.size()];
        for(int i = 0; i < listOfURLs.size(); i++) {
            titles[i] = listOfURLs.get(i).getTitle();
        }
        return titles;
    }

    public LocalDate get_Date(int position) {
        return listOfURLs.get(position).getDate();
    }

    private class ListEntry {
        private String URL, title;
        private LocalDate date;

        public ListEntry (String URL, String title, LocalDate date) {
            this.URL = URL;
            this.title = title;
            this.date = date;
        }

        public String getURL() {
            return URL;
        }

        public String getTitle() {
            return title;
        }

        public LocalDate getDate() {
            return date;
        }
    }
}
