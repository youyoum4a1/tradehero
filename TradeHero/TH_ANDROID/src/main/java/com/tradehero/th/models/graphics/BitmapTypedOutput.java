package com.ayondo.academy.models.graphics;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import retrofit.mime.TypedByteArray;

public class BitmapTypedOutput extends TypedByteArray
{
    public static final String TYPE_JPEG = "jpeg";

    @NonNull private final String fileName;

    //<editor-fold desc="Constructors">
    public BitmapTypedOutput(@NonNull String type, @NonNull Bitmap bitmap, @NonNull String fileName, int compressQuality)
    {
        super(getMimeType(type), makeByteArray(type, bitmap, compressQuality));
        this.fileName = fileName;
    }
    //</editor-fold>

    @NonNull private static byte[] makeByteArray(@NonNull String type, @NonNull Bitmap bitmap, int compressQuality)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(getCompressType(type), compressQuality, bos);
        return bos.toByteArray();
    }

    @Override @NonNull public String fileName()
    {
        return this.fileName;
    }

    @NonNull private static String getMimeType(@NonNull String type)
    {
        switch(type)
        {
            case TYPE_JPEG:
                return "image/jpeg";
        }
        throw new IllegalArgumentException("Unhandled type " + type);
    }

    @NonNull private static Bitmap.CompressFormat getCompressType(@NonNull String type)
    {
        switch(type)
        {
            case TYPE_JPEG:
                return Bitmap.CompressFormat.JPEG;
        }
        throw new IllegalArgumentException("Unhandled type " + type);
    }
}
