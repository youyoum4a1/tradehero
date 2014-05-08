package com.tradehero.th.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import com.tradehero.common.graphics.RotateTransformation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Inject;

public class GraphicUtil
{
    @Inject public GraphicUtil()
    {
        super();
    }

    public Integer getOrientationCode(String imagePath)
    {
        return getOrientationCode(new File(imagePath));
    }

    /**
     * ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_ROTATE_180,
     * ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_NORMAL, null when unsure
     */
    public Integer getOrientationCode(File imageFile)
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

    public int getRotationDegree(String imagePath)
    {
        return getRotationDegree(new File(imagePath));
    }

    public int getRotationDegree(File imageFile)
    {
        return getRotationDegree(getOrientationCode(imageFile));
    }

    public int getRotationDegree(Integer orientationCode)
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

    public Bitmap decodeFileForDisplay(Context context, File f)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return decodeFileWithinSize(f, metrics.widthPixels, metrics.heightPixels);
    }

    public Bitmap decodeFileWithinSize(File f, int width, int height)
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

    public Bitmap decodeFileWithOrientation(File f, int rotationDegree,
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
