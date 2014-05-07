package com.tradehero.th.fragments.settings.photo;

import android.content.Context;
import android.content.pm.PackageManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ChooseImageFromDTOFactory
{
    @Inject public ChooseImageFromDTOFactory()
    {
        super();
    }

    public List<ChooseImageFromDTO> getAll(Context context)
    {
        List<ChooseImageFromDTO> all = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();

        // if device support camera?
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            all.add(new ChooseImageFromCameraDTO());
        }
        all.add(new ChooseImageFromLibraryDTO());
        return all;
    }
}
