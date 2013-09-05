/**
 * ImageUtils.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 27, 2013
 */
package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import com.fedorvlasov.lazylist.ImageLoader;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class ImageUtils
{
    public static ImageLoader.BitmapProcessor createDefaultWhiteToTransparentProcessor()
    {
        return createWhiteToTransparentProcessor(5);
    }

    public static ImageLoader.BitmapProcessor createWhiteToTransparentProcessor(final int tolerance)
    {
        return new ImageLoader.BitmapProcessor()
        {
            @Override public Bitmap process(Bitmap bitmap)
            {
                return convertToMutableAndRemoveBackground(bitmap, tolerance);
            }
        };
    }

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @param tolerance - example 5 will tell white is between 255 and 255
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutableAndRemoveBackground(Bitmap imgIn, final int tolerance)
    {
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
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();

            //System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);

            //Remove background white color
            int iWidth = imgIn.getWidth();
            int iHeight = imgIn.getHeight();
            int pixelSize = iWidth * iHeight;
            int[] imagePixels = new int[pixelSize];
            imgIn.getPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);
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
            imgIn.setPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);

            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();
        } catch (/*FileNotFoundException | IOException |*/ Exception e)
        {
            e.printStackTrace();
        }

        return imgIn;
    }
}
