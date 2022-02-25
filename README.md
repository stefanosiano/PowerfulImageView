PowerfulImageView
=================

Custom Android ImageView with several added features.  
1) Progress indicator: circular, horizontal, disabled;  
2) Shapes: normal, rectangle, square, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle. It supports all scale types, plus additional custom scale types, like topCrop and bottomCrop;  
3) Blur: Blurs the image with gaussian, box or stack algorithm. It can use either Renderscript or Java methods, and fallback in case of errors.  
It extends AppcompatImageView, so it supports vector drawables and uses support version of renderscript
  
  
Several other features will be added, and all the sections of the readme will be updated accordingly.  
All the features can be used together, alone or combined in any way (like using only progress and blur).  
  
Renderscript is not included in the main module, but it's included in imageview_rs. In this way you can use imageview if you don't need Renderscript to not have any native code included in your app. If you want to use Rendercript for blurring, you should use imageview_rs instead.
  It supports Android data binding, too.
  
  
  
**Progress:**  
![Progress styles](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/progress.png) 
![Full load effect](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/full_load.gif) 
  
**Shapes:**  
![Shapes](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/shapes.png) 
  
**Blur:**  
![Blur1](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/blur1.png) 
![Blur2](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/blur2.png) 
  
  
  
  
Focus
-----
  
This library focuses on 4 points, in the following order:
1) Performance,
2) Reliability,
3) Customizability,
4) Ease of use.
  
To know more about these, read each feature page (links below).  
  
  
  
Background
----------
  
**[Progress](https://github.com/stefanosiano/PowerfulImageView/blob/master/Progress.md):**  
Shows a progress indicator over the image. It can be used to show the download or processing status of the image.
Default progress mode imitates the Android material circular progress bar.
This is flexible enough to let you use the progress in other ways, like tracking time passing, current achievements or whatever you want.
  
  
  
**[Shapes](https://github.com/stefanosiano/PowerfulImageView/blob/master/Shapes.md):**  
Most applications need some kind of shape. All provided shapes are compatible with any kind of drawable. Also, all scale types are supported, and some additional scale type were added, too.
Shapes are divided into 3 types:  
1) Rectangular (normal, square, rectangle): These shapes should be as efficient as normal ImageViews. Only dimensions are affected;
2) Rounded (circle, oval, rounded rectangle): These shapes are  achieved following techniques [recommended by Romain Guy](http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/). Background and foreground drawables will be resized, but rounded shape will not be applied to them.
3) Solid shapes consist in a solid color drawn over them. They are very efficient and support any animation and image loader. They completely cover the background, in contrast with rounded shapes, which allow users to see anything behind them.
  
  
  
**[Blur](https://github.com/stefanosiano/PowerfulImageView/blob/master/Blur.md):**  
Some applications may need to blur an image. While blurring an image sounds easy at first, it's much harder then it should be.
Blur algorithms are divided into 2 categories:  
1) Renderscript algorithms: they use renderscript under the hood, falling back to the corresponding Java algorithm in case of error, if enabled by its option;
2) Java algorithms: they use java and are slower, even if all Java algorithms are multi-threaded to increase the performance (an option can limit the threads to use).
  
Note: To use Renderscript for blurring (recommended, but optional) you should imageview_rs instead of imageview.  
  
  
  
  
  
Gradle
------
  
```
dependencies {
    implementation 'io.github.stefanosiano.powerful_libraries:imageview:1.0.22' //Use this if you don't need renderscript
    implementation 'io.github.stefanosiano.powerful_libraries:imageview_rs:1.0.22' //Use this if you need renderscript
}
```
  
  
  
  
Usage
-----
  
The library is highly customizable. However, for very basic integration, you may need just:  
  
Via xml:  
```
    <com.stefanosiano.powerfullibraries.imageview.PowerfulImageView
        android:id="@+id/piv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:pivProgressMode="circular"
        app:pivShapeMode="circle"
        app:pivBlurMode="gaussian5x5"
        app:pivBlurRadius="1" />
```
  
Via Java:  
```
powerfulImageView.setProgressMode(PivProgressMode.CIRCULAR);
powerfulImageView.setShapeMode(PivShapeMode.CIRCLE);
powerfulImageView.setBlurMode(PivBlurMode.GAUSSIAN5X5, 1);
```
  
  
  
You can find more information about the [Progress](https://github.com/stefanosiano/PowerfulImageView/blob/master/Progress.md), [Shapes](https://github.com/stefanosiano/PowerfulImageView/blob/master/Shapes.md)
and [Blur](https://github.com/stefanosiano/PowerfulImageView/blob/master/Blur.md) features on their pages.
  
  
  
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
|setProgressMode|PivProgressMode|Changes the progress mode.|
|setProgressValue|float|Sets the progress of the current indicator. It will change its mode to determinate, if necessary.|
|setProgressIndeterminate|boolean|Whether the progress indicator is indeterminate.|
|getProgressMode| |Get the current progress mode selected.|
|setShapeMode|PivShapeMode|Changes the shape of the image.|
|setScaleType|PivShapeScaleType|Set the scale type to use for the image (some additional custom scale types were added).|
|getShapeMode| |Get the current shape mode selected.|
|setBlurMode|PivBlurMode, int|Changes the blur mode and the radius to blur the image.|
|setBlurRadius|int|Changes the blur radius to blur the image.|
|getBlurMode| |Get the selected shape mode|
|getBlurRadius| |Get the selected radius used for blurring|
|getBlurBlurredBitmap| |Get the last blurred bitmap. It will blur the image, if necessary. Don't use this method if you didn't enable blur!|
|getBlurOriginalBitmap| |Returns the original bitmap used to blur. Don't use this method if you didn't enable blur!|
  
  
  
  
  
  
Proguard
--------
No steps are required, since configuration is already included.  
  
  
  
  
  
Notes
-----
  
PowerfulImageView requires a minimum API level of 14 (same as AppCompatLibrary).
  
  
  
  
  
  
Roadmap
-------
Support zoom, with attention to smooth scroll and Pager integration. Will have to care about integration with shapes, too.  
Support panoramas, caring about integration with shapes.  
Will probably have to rewrite progress, to allow custom animations and drawables to be used.  

