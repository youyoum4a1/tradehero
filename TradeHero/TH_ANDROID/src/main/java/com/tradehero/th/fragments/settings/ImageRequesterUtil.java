package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class ImageRequesterUtil implements ActivityResultRequester
{
    private static final int REQUEST_GALLERY = 1309;
    private static final int REQUEST_CAMERA = 1310;
    private final static int REQUEST_PHOTO_ZOOM = 1311;

    @Nullable private final Integer cropAspectX;
    @Nullable private final Integer cropAspectY;
    @Nullable private final Integer cropSizeX;
    @Nullable private final Integer cropSizeY;
    private final BehaviorSubject<Bitmap> bitmapSubject;
    private File mCurrentPhotoFile;
    private File croppedPhotoFile;
    private int currentRequest = -1;

    public ImageRequesterUtil(
            @Nullable Integer cropAspectX,
            @Nullable Integer cropAspectY,
            @Nullable Integer cropSizeX,
            @Nullable Integer cropSizeY)
    {
        this.cropAspectX = cropAspectX;
        this.cropAspectY = cropAspectY;
        this.cropSizeX = cropSizeX;
        this.cropSizeY = cropSizeY;
        bitmapSubject = BehaviorSubject.create();
    }

    @NonNull public Observable<Bitmap> getBitmapObservable()
    {
        return bitmapSubject.asObservable();
    }

    public File getCroppedPhotoFile()
    {
        return croppedPhotoFile;
    }

    public void onImageFromCameraRequested(@NonNull Activity activity)
    {
        PackageManager pm = activity.getPackageManager();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> handlerActivities = pm.queryIntentActivities(cameraIntent, 0);
        if (handlerActivities.size() > 0)
        {
            mCurrentPhotoFile = createImageFile(activity);
            if (mCurrentPhotoFile == null)
            {
                THToast.show(R.string.error_save_image_in_external_storage);
                return;
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            activity.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
        else
        {
            THToast.show(R.string.device_no_camera);
        }
    }

    public void onImageFromLibraryRequested(@NonNull Activity activity)
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        try
        {
            activity.startActivityForResult(libraryIntent, REQUEST_GALLERY);
        } catch (ActivityNotFoundException e)
        {
            Timber.e(e, "Could not request gallery");
            THToast.show(R.string.error_launch_photo_library);
        }
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK
                && data != null)
        {
            currentRequest = REQUEST_GALLERY;
            startPhotoZoom(activity, data.getData());
        }
        else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK)
        {
            currentRequest = REQUEST_CAMERA;
            startPhotoZoom(activity, Uri.fromFile(mCurrentPhotoFile));
        }
        else if (requestCode == REQUEST_PHOTO_ZOOM && data != null)
        {
            Bundle bundle = data.getExtras();
            if (bundle != null)
            {
                Bitmap bitmap = bundle.getParcelable("data");
                if (bitmap == null || saveBitmapToFile(activity, bitmap))
                {
                    return;
                }

                if (currentRequest == REQUEST_CAMERA)
                {
                    currentRequest = -1;
                    bitmapSubject.onNext(bitmap);
                }
                else if (currentRequest == REQUEST_GALLERY)
                {
                    currentRequest = -1;
                    bitmapSubject.onNext(bitmap);
                }
            }
        }
    }

    private void startPhotoZoom(@NonNull Activity activity, Uri data)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        if (cropAspectX != null)
        {
            intent.putExtra("aspectX", cropAspectX);
        }
        if (cropAspectY != null)
        {
            intent.putExtra("aspectY", cropAspectY);
        }
        if (cropSizeX != null)
        {
            intent.putExtra("outputX", cropSizeX);
        }
        if (cropSizeY != null)
        {
            intent.putExtra("outputY", cropAspectY);
        }
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, REQUEST_PHOTO_ZOOM);
    }

    //TODO Maybe make this static, such that Bitmap from netVerify can be stored in the same file
    //And return the fileName
    private boolean saveBitmapToFile(@NonNull ContextWrapper contextWrapper, @NonNull Bitmap bitmap)
    {
        croppedPhotoFile = createImageFile(contextWrapper);
        if (croppedPhotoFile == null)
        {
            THToast.show(R.string.error_save_image_in_external_storage);
            return true;
        }
        FileOutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(croppedPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            outputStream.flush();
        } catch (Exception e)
        {
            THToast.show(R.string.error_save_image_in_external_storage);
            return true;
        } finally
        {
            if (outputStream != null)
            {
                try
                {
                    outputStream.close();
                } catch (IOException e)
                {
                    Timber.e(e, "Close");
                }
            }
        }
        return false;
    }

    @Nullable private File createImageFile(@NonNull ContextWrapper contextWrapper)
    {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image;
        try
        {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e)
        {
            Timber.e(e, "createImageFile");
            return null;
        }
        return image;
    }
}
