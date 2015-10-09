package com.tradehero.th.utils;

import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import com.tradehero.common.graphics.RotateTransformation;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphicUtil
{
    //<editor-fold desc="EXIF Rotation">
    public static Integer getOrientationCode(String imagePath)
    {
        return getOrientationCode(new File(imagePath));
    }

    /**
     * ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_ROTATE_90,
     * ExifInterface.ORIENTATION_NORMAL, null when unsure
     */
    @Nullable
    public static Integer getOrientationCode(@NonNull File imageFile)
    {
        try
        {
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            return exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e)
        {
            return null;
        }
    }

    public static int getRotationDegree(@NonNull String imagePath)
    {
        return getRotationDegree(new File(imagePath));
    }

    public static int getRotationDegree(@NonNull File imageFile)
    {
        return getRotationDegree(getOrientationCode(imageFile));
    }

    public static int getRotationDegree(@Nullable Integer orientationCode)
    {
        int rotation = 0;
        if (orientationCode != null)
        {
            switch (orientationCode)
            {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
            }
        }
        return rotation;
    }
    //</editor-fold>

    @Nullable public static Bitmap decodeBitmapForProfile(
            @NonNull Resources resources,
            @NonNull String selectedPath)
    {
        File imageFile = new File(selectedPath);
        BitmapFactory.Options options;
        // TODO limit the size of the image
        options = new BitmapFactory.Options();
        if (selectedPath.length() > 1000000)
        {
            options.inSampleSize = 4;
        }
        else
        {
            options.inSampleSize = 2;
        }

        int maxEdgePixel = resources.getInteger(R.integer.user_profile_photo_max_edge_pixel);
        return decodeFileWithinSize(imageFile, maxEdgePixel, maxEdgePixel);
    }

    @Nullable
    public static Bitmap decodeFileForDisplay(@NonNull Context context, @NonNull File f)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return decodeFileWithinSize(f, metrics.widthPixels, metrics.heightPixels);
    }

    @Nullable
    public static Bitmap decodeFileWithinSize(@NonNull File f, int width, int height)
    {
        try
        {
            //Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            int scaleW = options.outWidth / width;
            int scaleH = options.outHeight / height;
            int scale = Math.max(1, Math.max(scaleW, scaleH));
            //Log.d("Scale Factor:"+scale);
            //Find the correct scale value. It should be the power of 2.

            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;

            int rotationDegree = getRotationDegree(f);
            double rotationRad = Math.toRadians(rotationDegree);
            int expectedW = options.outWidth / scale;
            int expectedH = options.outHeight / scale;
            options2.outWidth = (int) (Math.abs(expectedW * Math.cos(rotationRad)) + Math.abs(
                    expectedH * Math.sin(rotationRad)));
            options2.outHeight = (int) (Math.abs(expectedH * Math.cos(rotationRad)) + Math.abs(
                    expectedW * Math.sin(rotationRad)));

            return decodeFileWithOrientation(f, rotationDegree, options2);
        } catch (FileNotFoundException e)
        {
        }
        return null;
    }

    @Nullable
    public static Bitmap decodeFileWithOrientation(@NonNull File f, int rotationDegree,
            BitmapFactory.Options options)
    {
        try
        {
            Bitmap scaledPhoto = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            scaledPhoto = new RotateTransformation(rotationDegree).transform(scaledPhoto);
            return scaledPhoto;
        } catch (FileNotFoundException e)
        {
        }
        return null;
    }

    public static int parseColor(@Nullable String argbHexColorString)
    {
        return parseColor(argbHexColorString, Color.WHITE);
    }

    public static int parseColor(@Nullable String argbHexColorString, int defaultColor)
    {
        if (argbHexColorString != null && !argbHexColorString.startsWith("#"))
        {
            argbHexColorString = "#" + argbHexColorString;
        }

        int color;
        try
        {
            color = Color.parseColor(argbHexColorString);
        } catch (Exception e)
        {
            color = defaultColor;
        }
        return color;
    }

    public static int getContrastingColor(int color)
    {
        //Reference http://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color

        int d = 0;

        if (isBright(color))
        {
            d = 0; // bright colors - return black
        }
        else
        {
            d = 255; // dark colors - return white
        }

        return Color.rgb(d, d, d);
    }

    private static boolean isBright(int color)
    {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5;
    }

    public static int getLighterColor(int color)
    {
        float hsvVals[] = new float[3];
        Color.colorToHSV(color, hsvVals);
        hsvVals[2] = 0.5f * (1f + hsvVals[2]);
        return Color.HSVToColor(hsvVals);
    }

    public static int getDarkerColor(int color)
    {
        float hsvVals[] = new float[3];
        Color.colorToHSV(color, hsvVals);
        hsvVals[2] = 0.8f * hsvVals[2];
        return Color.HSVToColor(hsvVals);
    }

    //<editor-fold desc="Color Filter">
    public static void applyColorFilter(@NonNull ImageView[] imageViews, int color)
    {
        for (ImageView imageView : imageViews)
        {
            applyColorFilter(imageView, color);
        }
    }

    public static void applyColorFilter(@NonNull Collection<? extends ImageView> imageViews, int color)
    {
        for (ImageView imageView : imageViews)
        {
            applyColorFilter(imageView, color);
        }
    }

    public static void applyColorFilter(@NonNull ImageView imageView, int color)
    {
        applyColorFilter(imageView.getDrawable(), color);
    }

    public static void applyColorFilter(@NonNull Drawable d, int color)
    {
        d.clearColorFilter();
        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public static void removeColorFilter(@NonNull ImageView[] imageViews)
    {
        for (ImageView imageView : imageViews)
        {
            removeColorFilter(imageView);
        }
    }

    public static void removeColorFilter(@NonNull Collection<? extends ImageView> imageViews)
    {
        for (ImageView imageView : imageViews)
        {
            removeColorFilter(imageView);
        }
    }

    public static void removeColorFilter(@NonNull ImageView imageView)
    {
        removeColorFilter(imageView.getDrawable());
    }

    public static void removeColorFilter(@NonNull Drawable d)
    {
        d.clearColorFilter();
    }
    //</editor-fold>

    public static StateListDrawable createStateListDrawable(@NonNull Context context, int normal)
    {
        int pressed;
        if (isBright(normal))
        {
            pressed = getDarkerColor(normal);
        }
        else
        {
            pressed = getLighterColor(normal);
        }
        return createStateListDrawable(context, normal, pressed);
    }

    public static StateListDrawable createStateListDrawable(@NonNull Context context, int normal, int pressed)
    {
        int focused;
        if (isBright(normal))
        {
            focused = getLighterColor(normal);
        }
        else
        {
            focused = getDarkerColor(normal);
        }
        return createStateListDrawable(context, normal, pressed, focused);
    }

    public static StateListDrawable createStateListDrawable(@NonNull Context context, int normal, int pressed, int focused)
    {
        StateListDrawable states = new StateListDrawable();
        states.setExitFadeDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(pressed)); //Pressed
        states.addState(new int[] {android.R.attr.state_focused}, new ColorDrawable(focused)); //Focused
        states.addState(new int[] {}, new ColorDrawable(normal)); //normal
        return states;
    }

    public static Drawable createStateListDrawableRes(@NonNull Context context, @DrawableRes int drawableResId)
    {
        int[] attrs = new int[] {
                R.attr.colorControlNormal,
                R.attr.colorAccent
        };

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs);

        ColorStateList list = new ColorStateList(new int[][] {
                new int[] {android.R.attr.state_pressed},
                new int[] {}
        }, new int[] {
                array.getColor(1, Color.BLACK),
                array.getColor(0, Color.BLACK)}
        );

        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        DrawableCompat.setTintList(drawable, list);
        array.recycle();
        return drawable;
    }

    @SuppressLint("NewApi")
    public static void setBackground(@NonNull View view, Drawable drawable)
    {
        if (SDKUtils.isJellyBeanOrHigher())
        {
            view.setBackground(drawable);
        }
        else
        {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void setBackgroundColorFromAttribute(View view, int attribute)
    {
        TypedValue typedValue = new TypedValue();
        int[] attrs = new int[] {attribute};
        TypedArray array = view.getContext().obtainStyledAttributes(typedValue.data, attrs);
        int color = array.getColor(0, Color.TRANSPARENT);
        view.setBackgroundColor(color);
        array.recycle();
    }

    public static void setEvenOddBackground(int position, View toSet)
    {
        if (position % 2 == 0)
        {
            toSet.setBackgroundResource(R.drawable.basic_white_selector);
        }
        else
        {
            toSet.setBackgroundResource(R.drawable.basic_light_grey_selector);
        }
    }

    public static List<PropertyValuesHolder> wiggleWiggle(float shakeFactor)
    {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        ArrayList<PropertyValuesHolder> propertyValuesHolders = new ArrayList<>();
        propertyValuesHolders.add(pvhScaleX);
        propertyValuesHolders.add(pvhScaleY);
        propertyValuesHolders.add(pvhRotate);

        return propertyValuesHolders;
    }

    public static BitmapTypedOutput fromFile(@NonNull File f)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        return new BitmapTypedOutput(BitmapTypedOutput.TYPE_JPEG, bitmap, f.getAbsolutePath(), 75);
    }
}
