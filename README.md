PowerfulImageView
=================

Custom Android ImageView with several added features.  
1) Progress indicator: circular, horizontal, disabled  
2) Shapes: normal, rectangle, square, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle.  
  
Several other features will be added, and all the sections of the readme will be updated accordingly.  
  
Planned features (for now):  
* Blur support
* Pinch in/out to zoom
* Panoramas
* Custom target for Picasso/Glide
  
  
Full load effect:  
![Full load effect](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/full_load.gif) 
  
Shapes:  
![Shapes](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/shapes.png) 
  
  
  
Motivations
-----------
  
1) Progress:  
Most applications use images loaded from the internet. There are a lot of great libraries to handle these cases, like [Picasso](https://github.com/square/picasso) or [Glide](https://github.com/bumptech/glide). These libraries allows you to show placeholders in your imageViews while downloading/processing the image, too.  
While this helps a lot, the placeholder doesn't provide any feedback or information to the user. So I created this library to show a progress indicator directly into the Image View, imitating the Android material circular progress bar, to show the current download (or an indeterminate progress bar when the image is downloaded, but processing).  
This is flexible enough to let you use the progress in other ways, like tracking time passing, current achievements or whatever you want.
  
2) Shapes:  
Most applications need some kind of shape. Since the library wants to show a progress indicator over the image, it makes sense to support many shapes. Circle, oval and rounded rectangle shapes are achieved following techniques [recommended by Romain Guy](http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/).  
  
  
  
Instructions
------------
  
The library is highly customizable. However, for very basic integration, you may need just:  

1) Progress  

Via xml:  
```
app:piv_progress_mode="circular"
```
Via Java:  
```
progressImageView.changeProgressMode(PivProgressMode.CIRCULAR);
```
  
2) Shapes  

Via xml:  
```
app:piv_shape_mode="circle"
```
Via Java:  
```
progressImageView.changeShapeMode(PivShapeMode.CIRCLE);
```
  
  
  
  
Gradle
------
  
```
repositories {
  jcenter() // used as default in Android Studio. Put this line into project's build.gradle file, if not already present.
}
```

To use **Powerful**ImageView, which supports vector drawables and tinting on older apis, but brings in the whole AppCompat library:  
```
dependencies {
    compile 'com.stefanosiano:powerfulimageview:0.2.1'
}
```
To use **Powerless**ImageView, which is a slick variant without AppCompat features, like vector drawables support or tinting on older apis:  
```
dependencies {
    compile 'com.stefanosiano:powerlessimageview:0.2.1'
}
```
If you already use AppCompat library, or you want to use vector drawables, go with **Powerful**ImageView. Otherwise use **Powerless**ImageView.  

  
  
Proguard
--------
No steps are required, since configuration is already included.  
  

Notes
-----
  
1) PowerfulImageView requires a minimum API level of 12.  
2) Indeterminate animations automatically stop when power saving mode is enabled. This is by design in Android with ObjectAnimator, used for animations in this library.
3) The progress indicator automatically supports rtl (unless disabled by its flag). This means that progress gravity follows the start/end behaviour, and that the progress is reversed with rtl languages.
4) Due to how rounded rectangles are drawn, they will show some space between the image and the border (if piv_shape_border_overlay=false). Also, solid rounded rectangle suffers of the same problem when using a border (with or without overlay).  
  
  
Roadmap
-------
Create code diagram?  
