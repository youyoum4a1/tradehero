package com.ayondo.academyapp.models.fastfill;

import android.support.annotation.NonNull;
import com.androidth.general.models.fastfill.jumio.NetverifyScanReference;

public interface ScanImageKey
{
    @NonNull NetverifyScanReference getScanReference();
}
