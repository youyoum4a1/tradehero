package com.tradehero.th.models.graphics;

import android.content.res.Resources;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.utils.BitmapForProfileFactory;
import javax.inject.Inject;

public class BitmapTypedOutputFactory
{
    @Inject public BitmapTypedOutputFactory()
    {
        super();
    }

    public BitmapTypedOutput safeCreateForProfilePhoto(
            Resources resources,
            BitmapForProfileFactory bitmapForProfileFactory,
            UserFormDTO userFormDTO)
    {
        if (userFormDTO == null || userFormDTO.profilePicturePath == null)
        {
            return null;
        }
        return createForProfilePhoto(resources, bitmapForProfileFactory, userFormDTO);
    }

    public BitmapTypedOutput createForProfilePhoto(
            Resources resources,
            BitmapForProfileFactory bitmapForProfileFactory,
            UserFormDTO userFormDTO)
    {
        return createForProfilePhoto(resources, bitmapForProfileFactory, userFormDTO.profilePicturePath);
    }

    public BitmapTypedOutput createForProfilePhoto(
            Resources resources,
            BitmapForProfileFactory bitmapForProfileFactory,
            String profilePicturePath)
    {
        return new BitmapTypedOutput(
                BitmapTypedOutput.TYPE_JPEG,
                bitmapForProfileFactory.decodeBitmapForProfile(resources, profilePicturePath),
                profilePicturePath,
                resources.getInteger(R.integer.user_profile_photo_compress_quality));
    }
}
