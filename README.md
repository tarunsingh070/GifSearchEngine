# GifSearchEngine

A simple Gif search engine to search various gifs based on the keywords you type and rate them.

## What's New

* Scroll to load more items.
* User's rating information stored in local database, user can edit rating at any time.
* Added many cool animations.
* Changed app theme to dark.
* Squashed some bugs.
* Wrote some unit tests.

## Features
* See all the trending gifs when app is launched.
* Search gifs based on keywords typed into the search bar.
* Sort the search results based on Ranking or Relevance.
* The ranking persists globally as the information about ranked gifs is stored in a Firebase database.
* Click on any gif to see the details of the gif such as Title, uploader, upload date, size, average rating etc.

## Currently unavailable stuff.

* Unit tests for views.

### Architecture

This Project follows the Model-View-Presenter architecture.

### Libraries
* OkHttp3 for REST api communication
* Glide for image loading
* Gson for converting Json data to Java objects.
* Firebase Database for reading/writing data in Firebase Database.
* Google Paging library for pagination support.
* Room persistance storage library for local data storage.
* Mockito, Robolectric libraries for unit tests.
