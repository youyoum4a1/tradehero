package com.ayondo.academy.models.fastfill;

import android.support.annotation.NonNull;
import com.ayondo.academy.models.fastfill.jumio.NetverifyScanReference;

public interface ScanImageKey
{
    @NonNull NetverifyScanReference getScanReference();
}
