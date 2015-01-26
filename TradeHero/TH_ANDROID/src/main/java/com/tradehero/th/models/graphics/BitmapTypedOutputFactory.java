package com.tradehero.th.models.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.utils.BitmapForProfileFactory;
import javax.inject.Inject;

public class BitmapTypedOutputFactory
{
    //<editor-fold desc="Constructors">
    @Inject public BitmapTypedOutputFactory()
    {
        super();
    }
    //</editor-fold>

    @Nullable public BitmapTypedOutput createForProfilePhoto(
            @NonNull Resources resources,
            @NonNull BitmapForProfileFactory bitmapForProfileFactory,
            @NonNull String profilePicturePath)
    {
        BitmapTypedOutput output = null;
        Bitmap bitmap = bitmapForProfileFactory.decodeBitmapForProfile(resources, profilePicturePath);
        if (bitmap != null)
        {
            Bitmap decoded = bitmapForProfileFactory.decodeBitmapForProfile(resources, profilePicturePath);
            if (decoded != null)
            {
                output = new BitmapTypedOutput(
                        BitmapTypedOutput.TYPE_JPEG,
                        decoded,
                        profilePicturePath,
                        resources.getInteger(R.integer.user_profile_photo_compress_quality));
            }
        }
        return output;
    }
}
