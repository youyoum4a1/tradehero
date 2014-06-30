package com.tradehero.th.models.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import com.tradehero.thm.R;
import com.tradehero.th.utils.BitmapForProfileFactory;
import javax.inject.Inject;

public class BitmapTypedOutputFactory
{
    @Inject public BitmapTypedOutputFactory()
    {
        super();
    }

    public BitmapTypedOutput createForProfilePhoto(
            Resources resources,
            BitmapForProfileFactory bitmapForProfileFactory,
            String profilePicturePath)
    {
        BitmapTypedOutput output = null;
        Bitmap bitmap = bitmapForProfileFactory.decodeBitmapForProfile(resources, profilePicturePath);
        if (bitmap != null)
        {
            output = new BitmapTypedOutput(
                    BitmapTypedOutput.TYPE_JPEG,
                    bitmapForProfileFactory.decodeBitmapForProfile(resources, profilePicturePath),
                    profilePicturePath,
                    resources.getInteger(R.integer.user_profile_photo_compress_quality));
        }
        return output;
    }
}
