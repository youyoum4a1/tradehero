package com.tradehero.th.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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
}
