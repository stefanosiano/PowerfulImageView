PowerfulImageView
=================

Custom Android ImageView with several added features.  
1) Progress indicator: circular, horizontal, disabled  
2) Shapes: normal, rectangle, square, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle. It supports all scale types, plus additional custom scale types, like top_crop.  
  
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
  
  
  
  
  
Gradle
------
  
```
repositories {
  jcenter() // used as default in Android Studio. Put this line into project's build.gradle file, if not already present.
}
```

To use **Powerful**ImageView, which extends AppcompatImageView and supports vector drawables and tinting on older apis, but brings in the whole AppCompat library:  
```
dependencies {
    compile 'com.stefanosiano:powerfulimageview:0.2.2'
}
```
To use **Powerless**ImageView, which extends ImageView and doesn't have AppCompat features:  
```
dependencies {
    compile 'com.stefanosiano:powerlessimageview:0.2.2'
}
```
  
  
  
  
Usage
-----
  
The library is highly customizable. However, for very basic integration, you may need just:  
  
Via xml:  
```
app:piv_progress_mode="circular"
app:piv_shape_mode="circle"
```
Via Java:  
```
progressImageView.changeProgressMode(PivProgressMode.CIRCULAR);
progressImageView.changeShapeMode(PivShapeMode.CIRCLE);
```
  
  
  
  
  
List of all XML attributes
--------------------------
  
**Progress options**  
  
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|piv_progress_border_width|dimension|-1|Width of the progress indicator. Overrides piv_progress_border_width_percent.|
|piv_progress_border_width_percent|float|10|Width of the progress indicator as percentage of the progress indicator size. It's used only if piv_progress_border_width is less than 0. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is ignored|
|piv_progress_size|dimension|-1|Size of the progress indicator. Overrides piv_progress_size_percent. It's less than 0, it is ignored.|
|piv_progress_size_percent|float|40|Set the size of the progress indicator. It's used only if piv_progress_size is less than 0. If the percentage is higher than 100, it is treated as (value % 100).|
|piv_progress_shadow_padding|dimension|-1|Set the padding of the progress indicator relative to its shadow. Overrides piv_progress_shadow_padding_percent.|
|piv_progress_shadow_padding_percent|float|10|Set the padding of the progress indicator relative to its shadow. It's used only if piv_progress_shadow_padding is less than 0. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is ignored.|
|piv_progress_padding|dimension|2dp|Set the padding of the progress indicator.|
|piv_progress_shadow_border_width|dimension|1dp|Set the width of the shadow border.|
|piv_progress_value_percent|float|0|Percentage value of the progress indicator, used by determinate progress. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is treated as 0. If the progress is indeterminate it's ignored.|
|piv_progress_front_color|color|#00A000|Set the front color of the indicator. If the drawer is indeterminate it's ignored.|
|piv_progress_back_color|color|#CCCCCC|Set the back color of the indicator. If the drawer is indeterminate it's ignored.|
|piv_progress_shadow_border_color|color|#000000|Set the color of the progress indicator shadow border.|
|piv_progress_shadow_color|color|#FFFFFF|Set the shadow color of the indicator.|
|piv_progress_indeterminate_color|color|#A0A0A0|Set the indeterminate color of the indicator. If the drawer is determinate it's ignored.|
|piv_progress_determinate_animation_enabled|boolean|true|Set whether the progress should update with an animation. If the progress is indeterminate it's ignored. If true it updates its progress with an animation, otherwise it will update instantly|
|piv_progress_rtl_disabled|boolean|language-based|Set whether the view should use right to left layout. If true, start will always be treated as left and end as right. If false, on api 17+, gravity will be treated accordingly to rtl rules.|
|piv_progress_indeterminate|boolean|true|Set whether the view should show an indeterminate progress indicator.|
|piv_progress_draw_wedge|boolean|false|Set whether to show a wedge or a circle, used by circular progress. If the progress is not circular it's ignored. If true, a wedge is drawn, otherwise a circle will be drawn.|
|piv_progress_shadow_enabled|boolean|true|Set whether to show a progress shadow, used by drawers. If true, the shadow is drawn.|
|piv_progress_reversed|boolean|false|Set whether the progress should be reversed.|
|piv_progress_gravity|enum|center|Set the gravity of the indicator. It will follow the rtl layout (on api 17+), if not disabled. Values are: center, start, end, top, top_start, top_end, bottom, bottom_start, bottom_end|
|piv_progress_mode|enum|disabled|Progress mode of the indicator. Values are: disabled, circular, horizontal|
  
  
**Shape options**  
  
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|piv_shape_background_color|color|transparent|Set the background color of the image, using the shape.|
|piv_shape_frontground_color|color|transparent|Set the frontground color of the image, using the shape.|
|piv_shape_inner_padding|dimension|-1|Set the inner padding of the image relative to the view. Overrides piv_shape_inner_padding_percent.|
|piv_shape_inner_padding_percent|float|0|Set the inner padding of the image relative to the view. It's used only if piv_shape_inner_padding is less than 0. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is ignored.|
|piv_shape_border_overlay|boolean|false|Set whether the border should be paint over the image.|
|piv_shape_border_width|dimension|0dp|Set the border width of the image.|
|piv_shape_border_color|color|transparent|Set the border color of the image.|
|piv_shape_ratio|float|based on view size|Set the ratio of the image. Doing so, width is calculated as height * ratio|
|piv_shape_radius_x|float|1|Set the x radius of the image. Used in rounded rectangles|
|piv_shape_radius_y|float|1|Set the y radius of the image. Used in rounded rectangles|
|piv_shape_solid_color|color|#FFFFFF|Set the solid color used by solid shapes|
|piv_shape_mode|enum|normal|Progress mode of the indicator. Values are: normal, circle, square, rectangle, oval, rounded_rectangle, solid_circle, solid_oval, solid_rounded_rectangle.|
|piv_shape_scaleType|enum|default view scale type|Custom scale type of the indicator. It overrides scaleType. Values are: matrix, fitXY, fitStart, fitCenter, fitEnd, center, centerCrop, centerInside, topCrop|
  
**Java methods**  

All progress and shape options are available via  

```
progressImageView.getProgressOptions().set...
progressImageView.getShapeOptions().set...
```
  
Convenience methods are provided for:  
  
| Name | Param | Description |
|:----:|:-----:|:-----------:|
|changeProgressMode|PivProgressMode|Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate). It also starts animation of indeterminate progress indicator.|
|setProgress|float|Sets the progress of the current indicator. If the drawer is indeterminate, it will change its state and make it determinate.|
|setProgressIndeterminate|boolean|Whether the progress indicator is indeterminate or not|
|changeShapeMode|PivShapeMode|Changes the shape of the image.|
|setScaleType|PivShapeScaleType|Controls how the image should be resized or moved to match the size of this ImageView. Added to provide additional custom scale types. Overrides ImageView's setScaleType(ImageView.ScaleType) method.|
  
  
  
  
  
  
  
Proguard
--------
No steps are required, since configuration is already included.  
  
  
  
  
  
Notes
-----
  
1) PowerfulImageView requires a minimum API level of 12.  
2) Indeterminate animations automatically stop when power saving mode is enabled. This is by design in Android with ObjectAnimator, used for animations in this library.
3) The progress indicator automatically supports rtl (unless disabled by its flag). This means that progress gravity follows the start/end behaviour, and that the progress is reversed with rtl languages.
4) Solid shapes are simple shapes with a solid color drawn over them. They support out of the box any animation and image loader library, and cover background, too. Non-solid shapes don't cover background, but they provide real shapes, allowing users to see anything behind the shape.
5) Due to how rounded rectangles are drawn, they will show some space between the image and the border (if piv_shape_border_overlay=false). Also, solid rounded rectangle suffers of the same problem when using a border (with or without overlay).  
  
  
  
  
  
  
  
  
Tips
----
  
Options may be overwheilming. Also, you should have a consistent UI. So, you should define a custom style for the progress options, configuring all the aspects of the PowerfulImageView progress, and then use your own style anywhere you need. This is good for theming, too!  
You can then apply custom styles for shapes, if you want. To do so, you just need to add your configuration in your styles.xml file like this:  
  
```
    <style name="MyPivStyle">
        <item name="piv_progress_indeterminate_color">@color/colorAccent</item>
        <item name="piv_progress_front_color">@color/colorPrimary</item>
        <item name="piv_progress_back_color">@color/colorAccent</item>
        <item name="piv_progress_indeterminate">false</item>
        <item name="piv_progress_mode">circular</item>
        ...
    </style>
```
  
Then apply it like this:  
  
```
    <com.stefanosiano.powerfulimageview.PowerfulImageView
        android:id="@+id/piv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@style/MyPivStyle"/>  
```
  
  
  
  
  
  
Roadmap
-------
Create code diagram  
Study blurring methods  




