package com.androidth.general.common.billing;

import com.androidth.general.common.activities.ActivityResultRequester;

public interface RequestCodeHolder extends ActivityResultRequester
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    void onDestroy();
}
