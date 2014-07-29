package com.tradehero.th.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import com.tradehero.common.utils.SDKUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.tradehero.common.graphics.RotateTransformation;
import com.tradehero.th.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Inject;

public class GraphicUtil implements BitmapForProfileFactory
{
    @Inject public GraphicUtil()
    {
        super();
    }

    //<editor-fold desc="EXIF Rotation">
    public Integer getOrientationCode(String imagePath)
    {
        return getOrientationCode(new File(imagePath));
    }

    /**
     * ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_ROTATE_180,
     * ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_NORMAL, null when unsure
     */
    @Nullable
    public Integer getOrientationCode(@NotNull File imageFile)
    {
        try
        {
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            return exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public int getRotationDegree(@NotNull String imagePath)
    {
        return getRotationDegree(new File(imagePath));
    }

    public int getRotationDegree(@NotNull File imageFile)
    {
        return getRotationDegree(getOrientationCode(imageFile));
    }

    public int getRotationDegree(@Nullable Integer orientationCode)
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

    @Nullable
    @Override public Bitmap decodeBitmapForProfile(Resources resources, @NotNull String selectedPath)
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
    public Bitmap decodeFileForDisplay(@NotNull Context context, @NotNull File f)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return decodeFileWithinSize(f, metrics.widthPixels, metrics.heightPixels);
    }

    @Nullable
    public Bitmap decodeFileWithinSize(@NotNull File f, int width, int height)
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
        }
        catch (FileNotFoundException e)
        {
        }
        return null;
    }

    @Nullable
    public Bitmap decodeFileWithOrientation(@NotNull File f, int rotationDegree,
            BitmapFactory.Options options)
    {
        try
        {
            Bitmap scaledPhoto = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            scaledPhoto = new RotateTransformation(rotationDegree).transform(scaledPhoto);
            return scaledPhoto;
        }
        catch (FileNotFoundException e)
        {
        }
        return null;
    }

    public int parseColor(@Nullable String argbHexColorString)
    {
        return parseColor(argbHexColorString, Color.WHITE);
    }

    public int parseColor(@Nullable String argbHexColorString, int defaultColor)
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

    public int getContrastingColor(int color)
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

    private boolean isBright(int color)
    {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (a < 0.5)
        {
            return true;
        }
        return false;
    }

    public int getLighterColor(int color)
    {
        float hsvVals[] = new float[3];
        Color.colorToHSV(color, hsvVals);
        hsvVals[2] = 0.5f * (1f + hsvVals[2]);
        return Color.HSVToColor(hsvVals);
    }

    public int getDarkerColor(int color)
    {
        float hsvVals[] = new float[3];
        Color.colorToHSV(color, hsvVals);
        hsvVals[2] = 0.5f * hsvVals[2];
        return Color.HSVToColor(hsvVals);
    }

    public StateListDrawable createStateListDrawable(@NotNull Context context, int normal)
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

    public StateListDrawable createStateListDrawable(@NotNull Context context, int normal, int pressed)
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

    public StateListDrawable createStateListDrawable(@NotNull Context context, int normal, int pressed, int focused)
    {
        StateListDrawable states = new StateListDrawable();
        states.setExitFadeDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        states.addState(new int[] {android.R.attr.state_pressed}, new ColorDrawable(pressed)); //Pressed
        states.addState(new int[] {android.R.attr.state_focused}, new ColorDrawable(focused)); //Focused
        states.addState(new int[] {}, new ColorDrawable(normal)); //normal
        return states;
    }

    @SuppressLint("NewApi")
    public void setBackground(@NotNull View view, Drawable drawable)
    {
        if(SDKUtils.isJellyBeanOrHigher())
        {
            view.setBackground(drawable);
        }
        else
        {
            view.setBackgroundDrawable(drawable);
        }
    }
}
