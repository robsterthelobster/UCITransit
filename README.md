# UCITransit
Android app for UCI shuttle times.

## Note
A recreation of my <a href="https://github.com/tripleducke/Capstone-Project">Udacity capstone project</a>, using some popular libraries and frameworks specifically RxJava and Dagger 2. Since it will be rebuilt completely, I will be trying Realm to replace SQLite. Though Realm currently doesn't seem to support geospatial queries, I'm hoping it will either come out soon, or I can implement something to replicate sort-by-distance similary as done in SQLite.

## Description
An Android transit app for UC Irvine and Anteater Express.  Users can quickly view nearby stops for arrival times, or click into an individual route for additional info on stops.  Each route also has a dedicated route map, with updated bus and stop markers.

## Play Store Link
https://play.google.com/store/apps/details?id=com.robsterthelobster.ucibustracker&hl=en

## Screenshots
![Nearby](/screenshots/device-2016-10-10-153217.png?raw=true "Nearby screen")
![Navigation](/screenshots/device-2016-10-10-153232.png?raw=true "Navigation Drawer")

## Data and Endpoints
The app is actually compatible with a large number of colleges and universities, all using Syncromatics tracking system.  UCI Transit polls the JSON files via Retrofit and parses them for use.  If you wish to use the bus data for another app or bus feed, the endpoints are as such:

* http://www.ucishuttles.com/Region/0/Routes
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Direction/0/Stops
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Stop/{STOP_ID}/Arrivals
* http://www.ucishuttles.com/Route/{ROUTE_ID}/Vehicles

## Libraries & Frameworks

* Dagger 2
* Retrofit & RxJava Adapter
* Gson
* ButterKnife
* RxJava & RxAndroid
* RxPermissions
* ReactiveLocation
* GoogleMaps & LocationServices
* Realm
* RealmRecyclerView

##License:

UCI Transit is released under the <a href="https://github.com/tripleducke/UCITransit/blob/master/LICENSE">MIT License</a>.
