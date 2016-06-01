package com.ayondo.academy.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKey;

public interface PositionDTOKey extends DTOKey
{
    @NonNull Bundle getArgs();
}
