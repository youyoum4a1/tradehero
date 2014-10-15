package com.tradehero.th.activities;

import android.content.Intent;

public interface ActivityResultRequester
{
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
