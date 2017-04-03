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

Determinate circular and horizontal progress with shadow and different gravity:  

![Determinate progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/screen%20determinate%20gravity.png) 
![Determinate horizontal progress with shadow and different gravity](https://raw.githubusercontent.com/stefanosiano/PowerfulImageView/master/screen%20determinate%20horizontal%20gravity.png)


Motivations
-----------

Most applications use images loaded from the internet. There are a lot of great libraries to handle these cases, like [Picasso](https://github.com/square/picasso) or [Glide](https://github.com/bumptech/glide). These libraries allows you to show placeholders in your imageViews while downloading/processing the image, too.  
While this helps a lot, the placeholder doesn't provide any feedback or information to the user. So I created this library to show a progress indicator directly into the Image View, imitating the Android material circular progress bar, to show the current download (or an indeterminate progress bar when the image is downloaded, but processing).  
This is flexible enough to let you use the progress in other ways, like tracking time passing, current achievements or whatever you want.





Gradle
------
```
dependencies {
    compile 'com.stefanosiano:powerfulimageview:0.1.2'
}
```
  
  
To do:  
  
Create a PowerlessImageView without appcompat support?  
Create code diagram?  
  
Draw dark transparent frontground?  
