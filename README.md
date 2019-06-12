# IdeaPad

First of all, a few screenshots:

HomeScreen!          
:-------------------------:|
![Home](https://github.com/jashasweejena/IdeaPad/raw/master/screenshots/home-ss.png)  

Add Dialog          |  Canvas Screen
:-------------------------:|:-------------------------: 
![Add](https://github.com/jashasweejena/IdeaPad/raw/master/screenshots/Add-ss.png)  |  ![Canvas](https://github.com/jashasweejena/IdeaPad/raw/master/screenshots/canvas-ss.png) 


## What is this?

Presenting Ideapad : An app to record, track and edit ideas. It supports doodling your ideas as well as writing them in text.


## Dependencies

The focus of this project lies on making use of a NoSQL Database (Realm) to store ideas and perform CRUD on them.

- AppCompat AndroidX Library
- AndroidX Design Library
- Realm NoSQL Library 
- TypeWriterView Library
- Butterknife Library
- com.rm:freedrawview Library
- com.github.ajithvgiri:Canvas-Library Library
- HTextView library
- com.daimajia.androidanimations:library Library

## Supported devices

The template support every device with a SDK level of at least 23 (Android Android 6.0+).


## Quick walkthrough

### Gradle

Nothing special here. Please note that the scanLibrary is added locally using *settings.gradle*:

```xml
    implementation 'androidx.appcompat:appcompat:1.0.0'
    implementation 'com.google.android.material:material'
    implementation 'io.realm:realm-android'
    implementation 'in.codeshuffle:typewriterview'
    implementation 'com.jakewharton:butterknife:9.0.0-SNAPSHOT'
    implementation 'com.rm:freedrawview:1.1.2'
    implementation 'com.github.ajithvgiri:Canvas-Library:v1.1.1'
    implementation "com.hanks:htextview-base:$htextview_version:0.1.6"
    implementation 'com.daimajia.androidanimations:library:2.3@aar'  
```

### Manifest

Basically we have three Activities monitoring the jobs. 
* MainActivity
* CanvasActivity

### Activities

*MainActivity* is the class which handles 

* Initializing and setting up Recyclerview
* Adding, Removing and Updating data to Realm
    

*CanvasActivity* is the class which handles

* Using com.github.ajithvgiri:Canvas-Library to draw on the canvas
* Save the drawing to a file







 

