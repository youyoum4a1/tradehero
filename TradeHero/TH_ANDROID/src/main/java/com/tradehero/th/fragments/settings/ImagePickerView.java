package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;

public class ImagePickerView extends LinearLayout
{
    //java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    public static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);
    public static final int REQUEST_CAMERA = new Random(new Date().getTime() + 1).nextInt(Short.MAX_VALUE);

    @Inject Activity activity;

    public ImagePickerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @OnClick(R.id.request_image_from_camera) void onImageFromCameraRequested()
    {
        PackageManager pm = activity
                .getApplicationContext()
                .getPackageManager();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> handlerActivities = pm.queryIntentActivities(cameraIntent, 0);
        if (handlerActivities.size() > 0)
        {
            //cameraIntent.setType("image/jpeg");
            activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
        else
        {
            THToast.show(R.string.device_no_camera);
        }
    }

    @OnClick(R.id.request_image_from_library) void onImageFromLibraryRequested()
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        activity.startActivityForResult(libraryIntent, REQUEST_GALLERY);
    }
}
