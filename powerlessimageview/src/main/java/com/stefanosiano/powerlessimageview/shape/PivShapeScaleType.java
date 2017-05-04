package com.stefanosiano.powerlessimageview.shape;

import android.graphics.Matrix;
import android.widget.ImageView;

/** Custom scale type of the image */
public enum PivShapeScaleType {
    /**
     * Scale using the image matrix when drawing. The image matrix can be set using
     * {@link ImageView#setImageMatrix(Matrix)}. From XML, use this syntax:
     * <code>app:piv_shape_scaleType="matrix"</code>.
     */
    MATRIX      (0),
    /**
     * Scale the image using {@link Matrix.ScaleToFit#FILL}.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="fitXY"</code>.
     */
    FIT_XY      (1),
    /**
     * Scale the image using {@link Matrix.ScaleToFit#START}.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="fitStart"</code>.
     */
    FIT_START   (2),
    /**
     * Scale the image using {@link Matrix.ScaleToFit#CENTER}.
     * From XML, use this syntax:
     * <code>app:piv_shape_scaleType="fitCenter"</code>.
     */
    FIT_CENTER  (3),
    /**
     * Scale the image using {@link Matrix.ScaleToFit#END}.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="fitEnd"</code>.
     */
    FIT_END     (4),
    /**
     * Center the image in the view, but perform no scaling.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="center"</code>.
     */
    CENTER      (5),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or larger than the corresponding dimension of the view
     * (minus padding). The image is then centered in the view.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="centerCrop"</code>.
     */
    CENTER_CROP (6),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or less than the corresponding dimension of the view
     * (minus padding). The image is then centered in the view.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="centerInside"</code>.
     */
    CENTER_INSIDE (7),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or larger than the corresponding dimension of the view
     * (minus padding). The image is then translated so that the top of the image is shown.
     * From XML, use this syntax: <code>app:piv_shape_scaleType="topCrop"</code>.
     */
    TOP_CROP (8);

    
    private final int value;
    PivShapeScaleType(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the custom scaleType from the android scaleType or CENTER if the scaleType passed is null */
    public static PivShapeScaleType getFromScaleType(ImageView.ScaleType scaleType){
        if (scaleType == null)
            return CENTER;

        switch (scaleType){
            default:
            case MATRIX:
                return MATRIX;
            case FIT_XY:
                return FIT_XY;
            case FIT_START:
                return FIT_START;
            case FIT_CENTER:
                return FIT_CENTER;
            case FIT_END:
                return FIT_END;
            case CENTER:
                return CENTER;
            case CENTER_CROP:
                return CENTER_CROP;
            case CENTER_INSIDE:
                return CENTER_INSIDE;
        }
    }

    /** Returns the scale type associated to the passed value, or CENTER if the value is invalid */
    public static PivShapeScaleType fromValue(int value){
        switch (value){
            case 0:
                return MATRIX;
            case 1:
                return FIT_XY;
            case 2:
                return FIT_START;
            case 3:
                return FIT_CENTER;
            case 4:
                return FIT_END;
            case 6:
                return CENTER_CROP;
            case 7:
                return CENTER_INSIDE;
            case 8:
                return TOP_CROP;
            default:
            case 5:
                return CENTER;
        }
    }

}
