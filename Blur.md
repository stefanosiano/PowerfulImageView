PowerfulImageView Blur
======================

This page contains all detailed info and tips about the blur feature of PowerfulImageView.  
  
  
Some applications may need to blur an image. While blurring an image sounds easy at first, there are a lot of things to care about, like renderscript contexts, downscaling image, performance optimizations to not lock the UI thread.  
  
  
Blur algorithms are divided into 2 categories:  
1) **Renderscript** algorithms: they use renderscript under the hood (support version in PowerfulImageView and normal version in PowerlessImageView);
2) **Java** algorithms: they use java and, while being slower, they support any version and any device. In case of error the original bitmap will be returned.
  
  
Notes
-----
  
If you want to support live blur, you should use any Renderscript method. If you can't, for any reason, the fastest java algorithm for this purpose is stack.  
If you want static blur (blur only once), then you can use any method is more suitable to you.  
  
  
Focus
-----
  
**Performance**  
Supports renderscript methods, which is naturally very fast. Also, all Java methods are multithreaded.  
Renderscript contexts are retained until necessary and shared across all instances of PowerfulImageView. They are released when the image is blurred (when static blurring) or when the View is removed from the activity (when live blurring).  
The original bitmap is retained when live blurring and released automatically when static blurring.  
**Reliability**  
In case of error of a Renderscript method, a corresponding Java method will be used. In case of error of a Java method, the original bitmap is returned.  
**Customizability**  
All aspects can be customized. Even the number of threads to use.  
**Ease of use**  
Basic integration needs only `piv_blur_mode` and `piv_blur_radius` to the xml element.  
  
  
  
    
List of all XML attributes
--------------------------
  
| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
|piv_blur_mode|enum|disabled|Set the mode of the blur to use. Values are: disabled, gaussian5x5, gaussian5x5_rs, gaussian3x3, gaussian3x3_rs, gaussian, gaussian_rs, box3x3, box3x3_rs, box5x5, box5x5_rs, stack, stack_rs|
|piv_blur_radius|int|0|Set the blur radius used (indicates clur strength)|
|piv_blur_down_sampling_rate|int|4|Rate to downSample the image width and height, based on the view size. The bitmap is downsampled to be no more than the view size divided by this rate.|
|piv_blur_static|boolean|false|Whether the original bitmap should be blurred only once. If so, several optimizations occur|
|piv_blur_use_rs_fallback|boolean|true|Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs|
|piv_blur_num_threads|int|0|Number of threads to use to blur the image (no more than available). If it's less than 1, all available cores are used|
  
  
  
  
  
**Java methods**
  
All options are available via `progressImageView.getBlurOptions().set...`
  
  
  
  
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
  
  
  
  
