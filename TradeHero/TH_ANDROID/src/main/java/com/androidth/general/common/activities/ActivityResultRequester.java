package com.androidth.general.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

public interface ActivityResultRequester
{
    // TODO consider using ActivityResultDTO ?
    void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data);
}
