PowerfulImageView
=================

Custom Android ImageView with several added features.  
1) Progress indicator: circular, horizontal, disabled;  
2) Shapes: normal, rectangle, square, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle. It supports all scale types, plus additional custom scale types, like topCrop and bottomCrop;  
3) Blur: Blurs the image with gaussian, box or stack algorithm. It can use either Renderscript or Java methods, and fallback in case of errors.  
  
Several other features will be added, and all the sections of the readme will be updated accordingly.
All the features can be used together, alone or combined in any way (like using only progress and blur).
  
Planned features (for now):  
* Pinch in/out to zoom
* Panoramas
  
  
Progress:  
![Full load effect](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/full_load.gif) 
  
Shapes:  
![Shapes](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/shapes.png) 
  
  
  
Background
----------
  
**Progress:**  
Most applications use images loaded from the internet. There are a lot of great libraries to handle these cases, like [Picasso](https://github.com/square/picasso) or [Glide](https://github.com/bumptech/glide). These libraries allows you to show placeholders in your imageViews while downloading/processing the image, too.  
While this helps a lot, the placeholder doesn't provide any feedback or information to the user. So I created this library to show a progress indicator directly into the Image View, imitating the Android material circular progress bar, to show the current download (or an indeterminate progress bar when the image is downloaded, but processing).  
This is flexible enough to let you use the progress in other ways, like tracking time passing, current achievements or whatever you want.
  
  
**Shapes:**  
Most applications need some kind of shape. Since the library wants to show a progress indicator over the image, it makes sense to support many shapes natively to provide indicator over the shapes. All shapes are compatible with any kind of drawable, and they provide support for shaping background and foreground drawables, too. Also, all scale types are supported, and some additional scale type were added, like topCrop and bottomCrop.
Shapes are divided into 3 types:  
1) Rectangular (normal, square, rectangle): These shapes should be as efficient as normal ImageViews. Only dimensions are affected;
2) Rounded (circle, oval, rounded rectangle): These shapes are  achieved following techniques [recommended by Romain Guy](http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/). Transformation and animations applied by image loaders like Glide or Picasso may cause issues. To avoid issues, you should disable any animation. For Picasso use the `noFade()` option, for Glide use `dontAnimate()`. Background and foreground drawables will be resized, but rounded shape will not be applied to them, as it happens for other shapes.
3) Solid shapes are simple shapes with a solid color drawn over them. They are very efficient and support out of the box any animation and image loader library. They completely cover the background, too, in contrast with non-solid shapes, which allow users to see anything behind them.
  
  
  
**Blur:**  
Some applications may need to blur an image. While blurring an image sounds easy at first, there are a lot of things to care about, like renderscript contexts, downscaling image, performance optimizations to not lock the UI thread. This library provides a very easy way to blur the image (as easy as adding `piv_blur_mode` and `piv_blur_radius` to the xml element). Also, it's designed to be as efficient and reliable as possible. This means that renderscript contexts are created and destroyed only when necessary, and they are shared through all instances of the PowerfulImageView, improving performances in lists.
Blur algorithms are divided into 2 categories:  
1) Renderscript algorithms: they use renderscript under the hood (support version in PowerfulImageView and normal version in PowerlessImageView). In case of error the corresponding Java algorithm will be used automatically, if enabled by its option, otherwise the original bitmap is returned;
2) Java algorithms: they use java and, while being slower, they support any version and any device. In case of error the original bitmap will be returned. All Java algorithms are multi-threaded to increase the performance (an option can limit the threads to use).
  

  
  
Gradle
------
  
```
repositories {
  jcenter() // used as default in Android Studio. Put this line into project's build.gradle file, if not already present.
}
```

To use **Powerful**ImageView, which extends AppcompatImageView, supports vector drawables and uses support version of renderscript:  
```
dependencies {
    compile 'com.stefanosiano:powerfulimageview:0.3.2' // Put this line into module's build.gradle
}
```
To use **Powerless**ImageView, which extends ImageView, doesn't depend on AppCompat library and uses normal Renderscript:  
```
dependencies {
    compile 'com.stefanosiano:powerlessimageview:0.3.2' // Put this line into module's build.gradle
}
```
  
  
  
  
Usage
-----
  
The library is highly customizable. However, for very basic integration, you may need just:  
  
Via xml:  
```
    <com.stefanosiano.powerfulimageview.PowerfulImageView
        android:id="@+id/piv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:piv_progress_mode="circular"
        app:piv_shape_mode="circle"
        app:piv_blur_mode="gaussian5x5"
        app:piv_blur_radius="1" />
```

Via Java:  
```
powerfulImageView.setProgressMode(PivProgressMode.CIRCULAR);
powerfulImageView.setShapeMode(PivShapeMode.CIRCLE);
powerfulImageView.setBlurMode(PivBlurMode.GAUSSIAN5X5, 1);
```
  


You can find more information about the ![Progress](https://github.com/stefanosiano/PowerfulImageView/blob/master/Progress.md), ![Shapes](https://github.com/stefanosiano/PowerfulImageView/blob/master/Shapes.md)
and ![Blur](https://github.com/stefanosiano/PowerfulImageView/blob/master/Blur.md) features on their pages.

  
  
**Java methods**
  
All options are available via  

```
progressImageView.getProgressOptions().set...
progressImageView.getShapeOptions().set...
progressImageView.getBlurOptions().set...
```
  
Convenience methods are provided for:  
  
| Name | Param | Description |
|:----:|:-----:|:-----------:|
|setProgressMode|PivProgressMode|Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate). It also starts animation of indeterminate progress indicator.|
|setProgressValue|float|Sets the progress of the current indicator. If the drawer is indeterminate, it will change its state and make it determinate.|
|setProgressIndeterminate|boolean|Whether the progress indicator is indeterminate or not|
|getProgressMode| |Get the current progress mode selected.|
|setShapeMode|PivShapeMode|Changes the shape of the image.|
|setScaleType|PivShapeScaleType|Controls how the image should be resized or moved to match the size of this ImageView. Added to provide additional custom scale types. Overrides ImageView's setScaleType(ImageView.ScaleType) method.|
|getShapeMode| |Get the current shape mode selected. It can then be used to check whether the shape is rectangular, rounded or solid through its methods.|
|setBlurMode|PivBlurMode, int|Changes the blur mode and the radius to blur the image.|
|setBlurRadius|int|Changes the blur radius to blur the image.|
|getBlurMode| |Get the selected shape mode|
|getBlurRadius| |Get the selected radius used for blurring|
|getBlurBlurredBitmap| |Get the last blurred bitmap. If the bitmap was never blurred, or blur options, mode or radius changed since the last blur, the bitmap will be blurred again (if static option is disabled). If any problem occurs, the original bitmap (nullable) will be returned. Don't use this method if you didn't enable blur!|
|getBlurOriginalBitmap| |Returns the original bitmap used to blur. If static blur option is enabled, this will be the same as the blurred one, since the original bitmap has been released. Don't use this method if you didn't enable blur!|
  
  
  
  
  
  
Proguard
--------
No steps are required, since configuration is already included.  
  
  
  
  
  
Notes
-----
  
PowerfulImageView requires a minimum API level of 14 (same as AppCompatLibrary). PowerlessImageView requires a minimum API level of 12 (18 to use renderscript).






Roadmap
-------
Test and fix any issue in current features.
Support zoom, with attention to smooth scroll and Pager integration. Will have to care about integration with shapes, too.
Support panoramas, caring about integration with shapes.  
Will probably have to rewrite progress, to allow custom animations and drawables to be used.  




