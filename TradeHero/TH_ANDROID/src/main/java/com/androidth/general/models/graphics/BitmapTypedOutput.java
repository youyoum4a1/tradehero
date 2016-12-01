package com.androidth.general.models.graphics;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class BitmapTypedOutput extends RequestBody
{
    public static final String TYPE_JPEG = "jpeg";

    @NonNull private final String fileName;

    //<editor-fold desc="Constructors">
    public BitmapTypedOutput(@NonNull String type, @NonNull Bitmap bitmap, @NonNull String fileName, int compressQuality)
    {
        super.create(getMimeType(type), makeByteArray(type, bitmap, compressQuality));
        this.fileName = fileName;
    }
    //</editor-fold>

    @NonNull private static byte[] makeByteArray(@NonNull String type, @NonNull Bitmap bitmap, int compressQuality)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(getCompressType(type), compressQuality, bos);
        return bos.toByteArray();
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("image/jpeg");
    }

    @NonNull private static MediaType getMimeType(@NonNull String type)
    {
        switch(type)
        {
            case TYPE_JPEG:
                return MediaType.parse("image/jpeg");
        }
        throw new IllegalArgumentException("Unhandled type " + type);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

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
