package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;

public interface WithTopSecurities
{
    @NonNull SecuritySuperCompactDTOList getTopSecurities();
}
