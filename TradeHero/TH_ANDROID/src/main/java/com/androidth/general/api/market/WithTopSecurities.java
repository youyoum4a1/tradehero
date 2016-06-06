package com.androidth.general.api.market;

import android.support.annotation.NonNull;

public interface WithTopSecurities
{
    @NonNull SecuritySuperCompactDTOList getTopSecurities();
}
