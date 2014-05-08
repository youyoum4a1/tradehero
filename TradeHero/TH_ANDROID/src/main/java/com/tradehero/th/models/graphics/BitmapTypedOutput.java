package com.tradehero.th.models.graphics;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import retrofit.mime.TypedByteArray;

public class BitmapTypedOutput extends TypedByteArray
{
    public static final String TYPE_JPEG = "jpeg";

    private String fileName;

    public BitmapTypedOutput(String type, Bitmap bitmap, String fileName)
    {
        super(getMimeType(type), makeByteArray(type, bitmap));
        this.fileName = fileName;
    }

    private static byte[] makeByteArray(String type, Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(getCompressType(type), 75, bos);
        return bos.toByteArray();
    }

    @Override public String fileName()
    {
        return this.fileName;
    }

    private static String getMimeType(String type)
    {
        switch(type)
        {
            case TYPE_JPEG:
                return "image/jpeg";
        }
        throw new IllegalArgumentException("Unhandled type " + type);
    }

    private static Bitmap.CompressFormat getCompressType(String type)
    {
        switch(type)
        {
            case TYPE_JPEG:
                return Bitmap.CompressFormat.JPEG;
        }
        throw new IllegalArgumentException("Unhandled type " + type);
    }
}
