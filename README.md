# UCITransit
Android app for UCI shuttle times.

## Note
A recreation of my <a href="https://github.com/tripleducke/Capstone-Project">Udacity capstone project</a>, using some popular libraries and frameworks specifically RxJava and Dagger 2. Since it will be rebuilt completely, I will be trying Realm to replace SQLite. Though Realm currently doesn't seem to support geospatial queries, I'm hoping it will either come out soon, or I can implement something to replicate sort-by-distance similary as done in SQLite.

## Description
An Android transit app for UC Irvine and Anteater Express.  Users can quickly view nearby stops for arrival times, or click into an individual route for additional info on stops.  Each route also has a dedicated route map, with updated bus and stop markers.

## Play Store Link
[![Get it on Google Play](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=com.robsterthelobster.ucibustracker&hl=en)

## Screenshots
<img src="/screenshots/device-2016-10-10-153217.png" width="400">
<img src="/screenshots/device-2016-10-10-153232.png" width="400">

## Data and Endpoints
The app is actually compatible with a large number of colleges and universities, all using Syncromatics tracking system.  UCI Transit polls the JSON files via Retrofit and parses them for use.  If you wish to use the bus data for another app or bus feed, the endpoints are as such:

* http://www.ucishuttles.com/Region/0/Routes
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Direction/0/Stops
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Stop/{STOP_ID}/Arrivals
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Vehicles

## Libraries & Frameworks

* Dagger 2
  * http://google.github.io/dagger/
  * Used for dependency injection and learning experience
* Retrofit & RxJava Adapter
  * http://square.github.io/retrofit/
  * Used to pull data from ucishuttles. Adapter used to convert the data into observables.
* Gson
  * https://github.com/google/gson
  * Used for retrofit to convert JSON into Java
* ButterKnife
  * http://jakewharton.github.io/butterknife/
  * Used to make the code prettier/neater. Also makes adding elements pretty easy.
* RxJava & RxAndroid
  * https://github.com/ReactiveX/RxJava
  * https://github.com/ReactiveX/RxAndroid
  * Used to convert and load data into views.
* RxPermissions
  * https://github.com/tbruyelle/RxPermissions
  * Ask for location permissions
* ReactiveLocation
  * https://github.com/mcharmas/Android-ReactiveLocation
  * Gets location while hiding Google API Client boilerplate code
* GoogleMaps & LocationServices
  * https://developers.google.com/maps/
  * Maps used to display the stops and bus data on a map
  * Location used to filter out applicable and useful data to the user
* Realm
  * https://github.com/realm/realm-java
  * Alternative to SQLite for database purposes.
* RealmRecyclerView
  * https://github.com/thorbenprimke/realm-recyclerview
  * Automates the loading of Realm data into a recyclerview
* RealmFieldNamesHelper
  * https://github.com/cmelchior/realmfieldnameshelper
  * Automatically generates name fields for Realm Objects
* Scoops
  * https://github.com/52inc/Scoops
  * Themer with some built-in themes and theme selection activity
  * Makes swapping themes pretty easy
  
##License:

UCI Transit is released under the <a href="https://github.com/tripleducke/UCITransit/blob/master/LICENSE">Apache 2.0 License</a>.

Copyright 2016 Robin Chen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
