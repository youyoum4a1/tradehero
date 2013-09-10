package com.tradehero.common.cache;

import android.graphics.Bitmap;
import com.fedorvlasov.lazylist.FileCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 4:13 PM To change this template use File | Settings | File Templates. */
public class BitmapFiler
{
    public static void cache(File file, Bitmap bitmap)
    {
        try
        {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
