Sharp
===========

Sharp is a Scalable Vector Graphics (SVG) implementation for Android. It facilitates loading vector graphics as SharpDrawables, and can effectively be used wherever a conventional image would be displayed, whether it be as a background, ImageView source, inside a StateListDrawable or used as composites in a TextView.

Forked from:  
https://github.com/pents90/svg-android

Merged changes from forks:  
https://github.com/b2renger/svg-android  
https://github.com/mindon/svg-android  
https://github.com/josefpavlik/svg-android

## Download

Download [the latest AAR][download] or grab from Bintray using Gradle:

    dependencies {
        implementation 'com.pixplicity.sharp:library:[VERSION_HERE]@aar'
    }

[![Download](https://api.bintray.com/packages/pixplicity/android/Sharp/images/download.svg)](https://bintray.com/pixplicity/android/Sharp/_latestVersion)

**Important:** Specify the latest version from Bintray as `[VERSION_HERE]`.

[download]: https://bintray.com/vonlisboa/pixplicity/sharp

## Sample

[A sample](https://github.com/Pixplicity/svg-android/tree/master/svgdemo) is included in this repository.

It's easy to load an SVG:

    Sharp.loadResource(getResources(), R.raw.cartman)
         .into(mImageView);

<img src="https://raw.githubusercontent.com/Pixplicity/sharp/master/sample-imageview/screenshots/cartman1.png" width="220" alt="Sample screenshot" />
<img src="https://raw.githubusercontent.com/Pixplicity/sharp/master/sample-imageview/screenshots/cartman2.png" width="220" alt="Sample screenshot" />
<img src="https://raw.githubusercontent.com/Pixplicity/sharp/master/sample-imageview/screenshots/cartman3.png" width="220" alt="Sample screenshot" />

SVGs can be loaded from various sources:

- `loadAsset(AssetManager, String)` loads SVG data from an Android application asset;
- `loadResource(Resources, int)` loads SVG data from an Android application resource;
- `loadString(String)` loads SVG data directly from a String;
- `loadInputStream(InputStream)` loads SVG data from an InputStream (but it's your responsibility to close it afterwards);
- `loadFile(File)` loads SVG data from a File, internally opening and closing a FileInputStream to do so.
- `loadPath(String)` loads SVG data directly from a String (but uses a lot of memory doing so).

Sharp facilitates the application of the resulting drawable as well, through the following methods:

- `into(View)` takes care of loading the SVG into the source of the ImageView, or setting the background if the view is not an ImageView;
- `getDrawable(View)` generates a `SharpDrawable`, which is a subclass of `PictureDrawable` that respects the `SharpPicture` boundaries.
- `getDrawable(View, DrawableCallback)` does the same, but on a background thread with callbacks of `DrawableCallback`;
- `getSharpPicture()` generates a `SharpPicture`, a wrapper containing a `Picture` and the SVG bounds and limits;
- `getSharpPicture(PictureCallback)` does the same, but on a background thread with callbacks of `PictureCallback`;
- `withAssets(AssetManager)` provides access to your application's assets, allowing Sharp to read typefaces;

It's recommended to use `into(View)` or `getDrawable(View)`, as the View parameter takes care of setting the view's layer type to `View.LAYER_TYPE_SOFTWARE`.

## Typefaces

By loading an SVG from the assets directory using `loadAsset(AssetManager, String)`, or by loading it through a different mechanism and providing access to your application's assets using `withAssets(AssetManager)`, Sharp can read typeface files automatically from the assets directory.

Sharp expects the typeface to be present as:

    /assets/fonts/[typeface name].ttf

If your font does not appear, check Logcat for any insightful error messages.

## Why isn't my SVG appearing?

If you're setting your view's drawable manually, instead of using `into()` or `intoBackground()`, be sure to set the view's layer type to `View.LAYER_TYPE_SOFTWARE`.

## Why no hardware acceleration?

Excellent question! Aside from the fact that PictureDrawable doesn't render correctly, paths do not efficiently render in hardware acceleration. Even if it worked, it would have poor performance. [Read this excellent discussion](http://stackoverflow.com/questions/15039829/drawing-paths-and-hardware-acceleration) about why this is, if you're interested.

You don't need to disable hardware acceleration on your entire application. Only *individual views* need to have the layer type changed, and providing your view into `SharpPicture.getDrawable(View)` takes care of this for you.

## Known issues

1. Text size and position isn't accurate. It's recommended to convert all text to paths in order for it to appear pixel-perfect.
2. Android's Gradle plugin 2.0.0 (in the preview channel as of January 2016) no longer allows you to place SVG files in `/res/drawable`. We recommend placing them in `/res/raw` (or `/assets` and using `loadAsset()`) instead.

## License

Licensed under the Apache license 2.0.
