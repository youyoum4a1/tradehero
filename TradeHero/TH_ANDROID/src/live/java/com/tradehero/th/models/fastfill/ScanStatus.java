package com.tradehero.th.models.fastfill;

import android.support.annotation.NonNull;
import java.util.Date;

public interface ScanStatus
{
    @NonNull ScanReference getScanReference();
    @NonNull Date getTimestamp();
    @NonNull DocumentCheckStatus getCheckStatus();
}
