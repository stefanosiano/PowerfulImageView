PowerfulImageView Shapes
========================

This page contains all detailed info and tips about the shapes feature of PowerfulImageView.



Most applications need some kind of shape. Since the library wants to show a progress indicator over the image, it makes sense to support many shapes natively to provide indicator over the shapes. All shapes are compatible with any kind of drawable, and they provide support for shaping background and foreground drawables, too. Also, all scale types are supported, and some additional scale type were added, like topCrop and bottomCrop.
Shapes are divided into 3 types:
1) Rectangular (normal, square, rectangle): These shapes should be as efficient as normal ImageViews. Only dimensions are affected;
2) Rounded (circle, oval, rounded rectangle): These shapes are  achieved following techniques [recommended by Romain Guy](http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/). Transformation and animations applied by image loaders like Glide or Picasso may cause issues. To avoid issues, you should disable any animation. For Picasso use the `noFade()` option, for Glide use `dontAnimate()`. Background and foreground drawables will be resized, but rounded shape will not be applied to them, as it happens for other shapes.
3) Solid shapes are simple shapes with a solid color drawn over them. They are very efficient and support out of the box any animation and image loader library. They completely cover the background, too, in contrast with non-solid shapes, which allow users to see anything behind them.



Due to how rounded rectangles are drawn, they will show some space between the image and the border (if piv_shape_border_overlay=false).
Also, solid rounded rectangle suffers of the same problem when using a border (with or without overlay).



List of all XML attributes
--------------------------

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|piv_shape_mode|enum|normal|Progress mode of the indicator. Values are: normal, circle, square, rectangle, oval, rounded_rectangle, solid_circle, solid_oval, solid_rounded_rectangle.|
|piv_shape_scaleType|enum|default view scale type|Custom scale type of the indicator. It overrides scaleType. Values are: matrix, fitXY, fitStart, fitCenter, fitEnd, center, centerCrop, centerInside, topCrop, bottomCrop|
|piv_shape_inner_padding|dimension-fraction|0|Set the inner padding of the image relative to the view, in a specific dimension or in percentage (20dp or 30%). If the specific dimension is less than 0, it is ignored. If the percentage is higher than 100, it is treated as (value % 100).|
|piv_shape_border_width|dimension|0dp|Set the border width of the image.|
|piv_shape_ratio|float|based on view size|Set the ratio of the image. Doing so, width is calculated as height * ratio|
|piv_shape_radius_x|float|1|Set the x radius of the image. Used in rounded rectangles|
|piv_shape_radius_y|float|1|Set the y radius of the image. Used in rounded rectangles|
|piv_shape_border_overlay|boolean|false|Set whether the border should be paint over the image.|
|piv_shape_solid_color|color|#FFFFFF|Set the solid color used by solid shapes|
|piv_shape_background_color|color|transparent|Set the background color of the image, using the shape.|
|piv_shape_foreground_color|color|transparent|Set the foreground color of the image, using the shape.|
|piv_shape_background|reference-color|null|Set the background drawable to draw under the image. Does not follow rounded shapes!|
|piv_shape_foreground|reference-color|null|Set the foreground drawable to draw over the image. Does not follow rounded shapes!|
|piv_shape_border_color|color|transparent|Set the border color of the image.|




**Java methods**

All options are available via `progressImageView.getShapeOptions().set...`




Convenience methods are provided for:

| Name | Param | Description |
|:----:|:-----:|:-----------:|
|setShapeMode|PivShapeMode|Changes the shape of the image.|
|setScaleType|PivShapeScaleType|Controls how the image should be resized or moved to match the size of this ImageView. Added to provide additional custom scale types. Overrides ImageView's setScaleType(ImageView.ScaleType) method.|
|getShapeMode| |Get the current shape mode selected. It can then be used to check whether the shape is rectangular, rounded or solid through its methods.|





