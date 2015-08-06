package com.tradehero.th.models.fastfill;

import android.support.annotation.NonNull;
import com.tradehero.th.models.fastfill.jumio.NetverifyScanReference;

public interface ScanImageKey
{
    @NonNull NetverifyScanReference getScanReference();
}
