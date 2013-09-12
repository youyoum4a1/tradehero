package com.tradehero.common.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.fedorvlasov.lazylist.FileCache;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 4:13 PM To change this template use File | Settings | File Templates. */
public class BitmapFiler
{
    private static final String TAG = BitmapFiler.class.getSimpleName();
    private static MessageDigest md5;

    private static MessageDigest getMd5()
    {
        if (md5 == null)
        {
            try
            {
                md5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new IllegalArgumentException(e);
            }
        }
        return md5;
    }

    public static String getFileName(String url, Transformation transformation)
    {
        return getFileName(url) + transformation.key();
    }

    public static String getFileName(String url)
    {
        return Base64.encodeToString(getMd5().digest(url.getBytes()), Base64.NO_WRAP).replace('=', '-').replace('/', '-');
    }

    synchronized public static void cache(File file, Bitmap bitmap) throws IOException
    {
        OutputStream os = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        os.flush();
        os.close();
        if (!file.exists())
        {
            THLog.e(TAG, "File " + file.getPath() + " was not saved", new Exception(""));
        }
        else
        {
            THLog.d(TAG, "File " + file.getPath() + " was saved");
        }
    }

    synchronized public static Bitmap decode(File f) throws IOException
    {
        THLog.d(TAG, "Decoding ");
        return BitmapFactory.decodeFile(f.getPath());
    }

    synchronized public static Bitmap decodeAutoScale(File f) throws IOException
    {
        //try
        //{
            //decode image size
            BitmapFactory.Options options = justDecodeBounds(f);

            int scale = computeScalePowerOfTwo(options);

            //decode with inSampleSize
            return decodeFileAtScale(f, scale);
        //}
        //catch (FileNotFoundException e)
        //{
        //}
        //catch (IOException e)
        //{
        //    e.printStackTrace();
        //}
        //return null;
    }

    synchronized public static BitmapFactory.Options justDecodeBounds(File f) throws IOException
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        FileInputStream stream1 = new FileInputStream(f);
        BitmapFactory.decodeStream(stream1, null, options);
        stream1.close();
        return options;
    }

    /** Find the correct scale value. It should be the power of 2. */
    public static int computeScalePowerOfTwo(BitmapFactory.Options options)
    {
        final int REQUIRED_SIZE = 70;
        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        int scale = 1;
        while (true)
        {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
            {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        return scale;
    }

    synchronized public static Bitmap decodeFileAtScale(File f, int scale) throws IOException
    {
        return decodeStreamAtScale(new FileInputStream(f), scale);
    }

    /** It closes the stream */
    synchronized public static Bitmap decodeStreamAtScale(InputStream stream, int scale) throws IOException
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        stream.close();
        return bitmap;
    }}
