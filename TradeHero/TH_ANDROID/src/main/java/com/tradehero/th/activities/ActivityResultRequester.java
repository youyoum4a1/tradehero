package com.tradehero.th.activities;

import android.content.Intent;

public interface ActivityResultRequester
{
    // TODO consider using ActivityResultDTO ?
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
