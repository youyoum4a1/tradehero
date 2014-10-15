package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import java.util.List;
import javax.inject.Inject;

public class ImagePickerView extends LinearLayout
{
    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;

    @Inject Activity activity;
    @Inject DashboardNavigator dashboardNavigator;

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
            Fragment currentFragment = dashboardNavigator.getCurrentFragment();
            if (currentFragment != null)
            {
                currentFragment.startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
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
        Fragment currentFragment = dashboardNavigator.getCurrentFragment();
        if (currentFragment != null)
        {
            currentFragment.startActivityForResult(libraryIntent, REQUEST_GALLERY);
        }
    }
}
