PowerfulImageView
=================

Custom Android ImageView with several added features.  
Right now it provides a progress indicator.  
Several other features will be added, and all the sections of the readme will be updated accordingly.  
  
Planned features (for now):  
* Shapes (Circle, fake circle, oval, fake oval, square, rounded rectangle, fake rounded rectangle, rectangle through ratio, default)
* Pinch in/out to zoom
* Panoramas
* Custom target for Picasso/Glide
  
  
Full load effect:  

![Load progress with shadow](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/load%20circular.gif) 
![Load horizontal progress with shadow](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/load%20horizontal.gif)

  
Determinate circular and horizontal progress with shadow and different gravity:  

![Determinate progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/screen%20determinate%20gravity.png) 
![Determinate horizontal progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/screen%20determinate%20horizontal%20gravity.png)
  
  
Indeterminate circular and horizontal progress with shadow and different gravity:  

![Indeterminate progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/indeterminate.gif) 
![Indeterminate horizontal progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/horizontal%20indeterminate.gif)


Motivations
-----------

Most applications use images loaded from the internet. There are a lot of great libraries to handle these cases, like [Picasso](https://github.com/square/picasso) or [Glide](https://github.com/bumptech/glide). These libraries allows you to show placeholders in your imageViews while downloading/processing the image, too.  
While this helps a lot, the placeholder doesn't provide any feedback or information to the user. So I created this library to show a progress indicator directly into the Image View, imitating the Android material circular progress bar, to show the current download (or an indeterminate progress bar when the image is downloaded, but processing).  
This is flexible enough to let you use the progress in other ways, like tracking time passing, current achievements or whatever you want.




**PowerfulImageView requires a minimum API level of 12.**  

Gradle
------
  
```
repositories {
  jcenter() // used as default in Android Studio. Put this line into project's build.gradle file, if not already present.
}
```

To use **Powerful**ImageView:  
```
dependencies {
    compile 'com.stefanosiano:powerfulimageview:0.1.2'
}
```
To use **Powerless**ImageView:  
```
dependencies {
    coming
}
```
  
PowerfulImageView vs PowerlessImageView
---------------------------------------
**PowerfulImageView**  
It supports vector drawables and tinting on older apis, but brings in the whole AppCompat library.  
If you already use AppCompat library, or you want to use vector drawables, go with it.  
  
**PowerlessImageView**  
Slick variant without AppCompat features, like vector drawables support or tinting on older apis.  
If you don't want to use AppCompat library, and are fine without vector drawables, go with it.  
  
  
Proguard
--------
No steps are required, since configuration is already included.  
  
  
To do:  
  
Publish powerless variant  
  
Create code diagram?  
  
Draw dark transparent frontground on the whole view?  
