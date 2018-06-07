# GifSearchEngine

A simple Gif search engine to search various gifs based on the keywords you type.

## Features

* See all the trending gifs when app is launched.
* Search gifs based on keywords typed into the search bar.
* Sort the search results based on Ranking or Relevance.
* The ranking persists globally as the information about ranked gifs is stored in a Firebase database.
* Click on any gif to see the details of the gif such as Title, uploader, upload date, size, average rating etc.

## Currently unavailable stuff.

* Pagination support.
* Unit tests.

### Architecture

This Project follows the Model-View-Presenter architecture.

### Libraries
* OkHttp3 for REST api communication
* Glide for image loading
* Gson for converting Json data to Java objects.
* Firebase Database for reading/writing data in Firebase Database.
