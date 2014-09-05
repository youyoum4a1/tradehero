package com.tradehero.common.billing;

public interface RequestCodeHolder
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    void onDestroy();
}
