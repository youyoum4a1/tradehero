package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import timber.log.Timber;

public class WhiteToTransparentTransformation implements com.squareup.picasso.Transformation
{
    public static final int DEFAULT_TOLERANCE = 5;
    public final int tolerance;

    public WhiteToTransparentTransformation()
    {
        super();
        this.tolerance = DEFAULT_TOLERANCE;
    }

    public WhiteToTransparentTransformation(int tolerance)
    {
        this.tolerance = tolerance;
    }

    @Override public Bitmap transform(Bitmap bitmapIn)
    {
        Bitmap bitmapOut = null;
        try
        {
            //this is the file going to use temporarily to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = File.createTempFile("ImageUtils.", ".tmp.bmp", Environment.getExternalStorageDirectory());

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = bitmapIn.getWidth();
            int height = bitmapIn.getHeight();
            Bitmap.Config type = bitmapIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, bitmapIn.getRowBytes() * height);
            bitmapIn.copyPixelsToBuffer(map);

            //System.gc();// try to force the bytes from the bitmapIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            bitmapOut = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            bitmapOut.copyPixelsFromBuffer(map);

            //Remove background white color
            int iWidth = bitmapOut.getWidth();
            int iHeight = bitmapOut.getHeight();
            int pixelSize = iWidth * iHeight;
            int[] imagePixels = new int[pixelSize];
            bitmapOut.getPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);
            int lowerBound = 255 - tolerance;
            int alpha, red, green, blue;
            for (int i = 0; i < pixelSize; i++)
            {

                alpha = Color.alpha(imagePixels[i]);
                red = Color.red(imagePixels[i]);
                green = Color.green(imagePixels[i]);
                blue = Color.blue(imagePixels[i]);

                if (((alpha >= lowerBound) && (alpha <= 255)) &&
                        ((red >= lowerBound) && (red <= 255)) &&
                        ((green >= lowerBound) && (green <= 255)) &&
                        ((blue >= lowerBound) && (blue <= 255)))
                {

                    imagePixels[i] = Color.TRANSPARENT;
                }
            }
            bitmapOut.setPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);

            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();
        }
        catch (/*FileNotFoundException | IOException |*/ Exception e)
        {
            Timber.d("White to transparent problem: %d", e.getMessage());
        }
        finally
        {
            if (bitmapIn != bitmapOut)
            {
                // recycle the source bitmap, this will be no longer used.
                bitmapIn.recycle();
            }
        }

        return bitmapOut;
    }

    @Override public String key()
    {
        return "whiteToTransparent(" + tolerance + ")";
    }
}
