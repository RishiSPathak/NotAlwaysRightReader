# Reader For Not Always Right

## Description

This is an android app that loads posts from the website notalwaysright.com. I enjoy their humorous, entertaining articles. 

As this website only shows 5 articles per page and accessing the comments section of an article required the extra step of opening that article’s webpage, I found it convenient to create an app that automatically opened each article’s webpage in bulk. By doing this I had easy access to articles from a specified category and date.

This app will, by default, open each post made on the selected date(s) from the selected categories in a separate tab of your browser. Alternatively, the posts can be opened inside the app one at a time. 

Made in Java using [Android Studio](https://developer.android.com/studio), [jsoup](https://jsoup.org/), and [ThreeTen Backport](https://www.threeten.org/threetenbp/). I selected jsoup for parsing the html from the Not Always Right website. ThreeTen Backport was utilized so that I could use the JSR-310 API (Java 8 date and time library) on older devices that did not support it (such as the Samsung Galaxy S5).

## Installation

You can download NotAlwaysRightReaderForAndroid.apk and open it on your android device to install it. You will need to enable sideloading to do this if you have not done so prior. Alternatively, you can download or clone the repository and compile it yourself in android studio to generate your own apk file.

## Instructions

![image](https://user-images.githubusercontent.com/111155048/191289905-66ef47b2-b3b9-46d1-a867-fea326ab9be1.png)

The first time you use the app you should use the gear in the upper right-hand corner to select the settings you want to use. You can choose from 3 methods of selecting dates (one date, consecutive range of dates, or list of dates), whether you want posts opened in the app or your browser, and if the app should only open articles that you have not opened before. An article is marked as read when the app sends it to your browser to open or is opened in the app. This menu also includes the option to reset the list of read articles.

![image](https://user-images.githubusercontent.com/111155048/191288181-5e0cadbe-f94c-4710-8b3a-9c4bffaa65ba.png)

On the main page of the app simply choose the date(s) you wish to view posts on and then choose the category you want view posts from. The bottom row of buttons scrolls horizontally to reveal more categories. After a short wait (the length of the wait is greater when looking for older posts) the posts will open in your browser, or a button will appear to view them in the app (depending on your chosen settings). 

## About the Author

### Rishi Pathak

LinkedIn: [here](https://www.linkedin.com/in/rishispathak)
