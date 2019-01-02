
# Beerio - Beer browser for Android

Beerio, simply put, is an app that allows you to store all of the information about your favourite beers in one place: this app. Beerio allows you to browse an online database of beers (BrewereyDB) and save these individual beers to your local library. On the other hand if a beer is not available in the database, you are able to 'create' a beer yourself and store it in your personal library. After adding some beers to your personal beer library, you can place notes on them.

Also check out the IOS version of this application, [Beerio - Beer browser for IOS](https://github.com/CasperVerswijvelt/Beerio).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them

```
Android Studio
```

### Installing

Clone the repository

```
git clone https://github.com/HoGentTIN/native-apps-1-android-creative-app-CasperVerswijvelt.git
```

Open the project in Android Studio


Build the application

Done!

## Running the tests

To run the tests for this application, simple type this into the terminal while in the root of the project, and press enter.

```
./gradlew connectedAndroidTest
```

This command will run automated espresso UI tests, on a connected android device or on the simulator.


## Built With

* [Gradle](https://gradle.org) - Dependency Management
* [Fuel](https://github.com/kittinunf/Fuel) - Used as networking library
* [Moshi](https://github.com/square/moshi) - JSON parser
* [Joda](https://github.com/JodaOrg/joda-time) - Alternative to Java data & time
* [Picasso](http://square.github.io/picasso/) - Library used to display and download images easily
* [PhotoView](https://github.com/chrisbanes/PhotoView) - Library to create a robust zoomable image
* [Room](https://developer.android.com/topic/libraries/architecture/room) - Persistence library for local database
* [Anko](https://github.com/Kotlin/anko) - Anko Commons: Used for async calling of methods in backgroud thread
* [Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database) - Library used for debugging Room database
* [PixImagePicker](https://github.com/akshay2211/PixImagePicker) - Simple and clean image picker library

## Contributing

Contributions are more than welcome! 
If you found a bug in our code or wish to suggest a feature, choose one of these options.

 - Fork this repository, make changes, open a pull request
 - Open an issue, give a good title and description
 
 I'll review these as fast as possible
 Thanks for your cooperation!

## Versioning

I used [GitHub](http://github.com/) for versioning. 

## Authors

* **Casper Verswijvelt** - *All of the work* - [CasperVerswijvelt](https://github.com/CasperVerswijvelt)


## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Thanks to BreweryDB for offering free sandbox licenses for their beer database API
