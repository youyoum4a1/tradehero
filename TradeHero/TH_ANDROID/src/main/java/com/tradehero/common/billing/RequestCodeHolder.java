package com.tradehero.common.billing;

import com.tradehero.common.activities.ActivityResultRequester;

public interface RequestCodeHolder extends ActivityResultRequester
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    void onDestroy();
}
