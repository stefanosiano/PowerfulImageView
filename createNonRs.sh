#!/bin/bash

get_directory(){
    SOURCE="${BASH_SOURCE[0]}"
    while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
      DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
      SOURCE="$(readlink "$SOURCE")"
      [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
    done
    #Get script directory
    dir="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    echo "$dir"
}

#need this to get real directory, not the one i'm calling this script from
dir=$(get_directory)
algDir=$dir/powerfulimageview/src/main/java/com/stefanosiano/powerful_libraries/imageview/blur/algorithms

rm -Rf $dir/powerfulimageview
cp -R $dir/powerfulimageview_rs $dir/powerfulimageview

sed -i 's/imageview_rs/imageview/g' $dir/powerfulimageview/build.gradle
sed -i 's/renderscriptSupportModeEnabled true//g' $dir/powerfulimageview/build.gradle

sed -i ':a;N;$!ba;s/\/\/RENDERSCRIPT.*//' $algDir/BoxAlgorithms.kt
sed -i 's/import androidx.renderscript.*//' $algDir/BoxAlgorithms.kt

sed -i ':a;N;$!ba;s/\/\/RENDERSCRIPT.*//' $algDir/GaussianAlgorithms.kt
sed -i 's/import androidx.renderscript.*//' $algDir/GaussianAlgorithms.kt

sed -i ':a;N;$!ba;s/BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {.*/BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {}/' $algDir/BaseConvolveRenderscriptBlurAlgorithm.kt
sed -i 's/import androidx.renderscript.*//' $algDir/BaseConvolveRenderscriptBlurAlgorithm.kt

sed -i 's/import androidx.renderscript.*//' $algDir/BlurAlgorithm.kt
sed -i 's/renderscript: RenderScript?/renderscript: Any?/' $algDir/BlurAlgorithm.kt

sed -i 's/RenderscriptBlurAlgorithm()/BlurAlgorithm()/' $algDir/BlurManager.kt

sed -i 's/import androidx.renderscript.*//' $algDir/SharedBlurManager.kt
sed -i 's/c.renderScript?.destroy()//' $algDir/SharedBlurManager.kt
sed -i 's/: RenderScript?/: Any?/g' $algDir/SharedBlurManager.kt
sed -i 's/RenderScript.create(applicationContext)/null/' $algDir/SharedBlurManager.kt

