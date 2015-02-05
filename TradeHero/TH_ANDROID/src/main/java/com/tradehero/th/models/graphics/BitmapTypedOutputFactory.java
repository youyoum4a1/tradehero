package com.tradehero.th.models.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.utils.GraphicUtil;
import javax.inject.Inject;

public class BitmapTypedOutputFactory
{
    @Nullable public static BitmapTypedOutput createForProfilePhoto(
            @NonNull Resources resources,
            @NonNull String profilePicturePath)
    {
        BitmapTypedOutput output = null;
        Bitmap bitmap = GraphicUtil.decodeBitmapForProfile(resources, profilePicturePath);
        if (bitmap != null)
        {
            Bitmap decoded = GraphicUtil.decodeBitmapForProfile(resources, profilePicturePath);
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
