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
    compile 'com.stefanosiano:powerfulimageview:0.2.1'
}
```
To use **Powerless**ImageView, which extends ImageView and doesn't have AppCompat features:  
```
dependencies {
    compile 'com.stefanosiano:powerlessimageview:0.2.1'
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
  
List of all XML attributes:  
  
        <!-- PROGRESS OPTIONS -->
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|piv_progress_determinate_animation_enabled|boolean|true|Set whether the progress should update with an animation. If the progress is indeterminate it's ignored. If true it updates its progress with an animation, otherwise it will update instantly|
|piv_progress_border_width|dimension|-1|Width of the progress indicator. Overrides piv_progress_border_width_percent.|
|piv_progress_border_width_percent|float|10|Width of the progress indicator as percentage of the progress indicator size. It's used only if piv_progress_border_width is less than 0. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is ignored|
|piv_progress_size|dimension|-1|Size of the progress indicator. Overrides piv_progress_size_percent. It's less than 0, it is ignored.|
|piv_progress_size_percent|float|40|Set the size of the progress indicator. It's used only if piv_progress_size is less than 0. If the percentage is higher than 100, it is treated as (value % 100).|
|piv_progress_padding|dimension|2dp|Set the padding of the progress indicator.|
|piv_progress_shadow_border_width|dimension|1dp|Set the width of the shadow border.|
|piv_progress_value_percent|float|0|Percentage value of the progress indicator, used by determinate progress. If the percentage is higher than 100, it is treated as (value % 100). If the percentage is lower than 0, it is treated as 0. If the progress is indeterminate it's ignored.|
|piv_progress_front_color|color|#00A000|Set the front color of the indicator. If the drawer is indeterminate it's ignored.|
|piv_progress_back_color|color|#CCCCCC|Set the back color of the indicator. If the drawer is indeterminate it's ignored.|
|piv_progress_shadow_border_color|color|#000000|Set the color of the progress indicator shadow border.|
|piv_progress_indeterminate_color|color|#A0A0A0|Set the indeterminate color of the indicator. If the drawer is determinate it's ignored.|
|piv_progress_rtl_disabled|boolean|language-based|Set whether the view should use right to left layout. If true, start will always be treated as left and end as right. If false, on api 17+, gravity will be treated accordingly to rtl rules.|
|piv_progress_indeterminate|boolean|true|Set whether the view should show an indeterminate progress indicator.|




        <!-- Set whether to show a wedge or a circle, used by circular determinate drawer.
             If the drawer is not determinate it's ignored.
             If true, a wedge is drawn, otherwise a circle will be drawn -->
        <attr name="piv_progress_draw_wedge" format="boolean" />

        <!-- Set whether to show a progress shadow, used by drawers.
             If true, the shadow is drawn -->
        <attr name="piv_progress_shadow_enabled" format="boolean" />

        <!-- Set whether the progress should be reversed, used by drawers -->
        <attr name="piv_progress_reversed" format="boolean" />

        <!-- Set the shadow color of the indicator, used by drawers.
             Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
             premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
             See the Color class for more details. -->
        <attr name="piv_progress_shadow_color" format="color" />

        <!-- Set the padding of the progress indicator relative to its shadow.
             It's used only if piv_progress_shadow_padding is less than 0.
             If the percentage is higher than 100, it is treated as (value % 100).
             If the percentage is lower than 0, it is ignored. -->
        <attr name="piv_progress_shadow_padding_percent" format="float" />

        <!-- Set the padding of the progress indicator relative to its shadow.
             Overrides piv_progress_shadow_padding_percent. -->
        <attr name="piv_progress_shadow_padding" format="dimension" />

        <!-- Set the gravity of the indicator.
             It will follow the right to left layout (on api 17+), if not disabled. -->
        <attr name="piv_progress_gravity" format="enum" >
            <enum name="center" value="0"/>
            <enum name="start" value="1"/>
            <enum name="end" value="2"/>
            <enum name="top" value="3"/>
            <enum name="top_start" value="4"/>
            <enum name="top_end" value="5"/>
            <enum name="bottom" value="6"/>
            <enum name="bottom_start" value="7"/>
            <enum name="bottom_end" value="8"/>
        </attr>

        <!-- Progress mode of the indicator. -->
        <attr name="piv_progress_mode" format="enum" >
            <enum name="disabled" value="0"/>
            <enum name="circular" value="1"/>
            <enum name="horizontal" value="2"/>
        </attr>



        <!-- SHAPE OPTIONS -->

        <!-- Progress mode of the indicator -->
        <attr name="piv_shape_mode" format="enum" >
            <enum name="normal" value="0"/>
            <enum name="circle" value="1"/>
            <enum name="square" value="2"/>
            <enum name="rectangle" value="3"/>
            <enum name="oval" value="4"/>
            <enum name="rounded_rectangle" value="5"/>
            <enum name="solid_circle" value="6"/>
            <enum name="solid_oval" value="7"/>
            <enum name="solid_rounded_rectangle" value="8"/>
        </attr>

        <!-- Custom scale type of the indicator -->
        <attr name="piv_shape_scaleType" format="enum" >
            <enum name="matrix" value="0"/>
            <enum name="fitXY" value="1"/>
            <enum name="fitStart" value="2"/>
            <enum name="fitCenter" value="3"/>
            <enum name="fitEnd" value="4"/>
            <enum name="center" value="5"/>
            <enum name="centerCrop" value="6"/>
            <enum name="centerInside" value="7"/>
            <enum name="topCrop" value="8"/>
        </attr>

        <!-- Set the background color of the image, using the shape.
             Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
             premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
             See the Color class for more details. -->
        <attr name="piv_shape_background_color" format="color" />

        <!-- Set the frontground color of the image, using the shape.
             Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
             premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
             See the Color class for more details. -->
        <attr name="piv_shape_frontground_color" format="color" />

        <!-- Set the inner padding of the image relative to the view.
             It's used only if piv_shape_inner_padding is less than 0.
             If the percentage is higher than 100, it is treated as (value % 100).
             If the percentage is lower than 0, it is ignored. -->
        <attr name="piv_shape_inner_padding_percent" format="float" />

        <!-- Set the inner padding of the image relative to the view
             Overrides piv_shape_inner_padding_percent. -->
        <attr name="piv_shape_inner_padding" format="dimension" />

        <!-- Set whether the border should be paint over the image. -->
        <attr name="piv_shape_border_overlay" format="boolean" />

        <!-- Set the border width of the image. -->
        <attr name="piv_shape_border_width" format="dimension" />

        <!-- Set the border color of the image.
             Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
             premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
             See the Color class for more details. -->
        <attr name="piv_shape_border_color" format="color" />

        <!-- Set the ratio of the image. Doing so, width is calculated as height * ratio -->
        <attr name="piv_shape_ratio" format="float" />

        <!-- Set the x radius of the image. Used in rounded rectangles -->
        <attr name="piv_shape_radius_x" format="float" />

        <!-- Set the y radius of the image. Used in rounded rectangles -->
        <attr name="piv_shape_radius_y" format="float" />

        <!-- Set the solid color used by solid shapes -->
        <attr name="piv_shape_solid_color" format="color" />

  
  
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
List all available attributes  
Create code diagram  
Study blurring  
