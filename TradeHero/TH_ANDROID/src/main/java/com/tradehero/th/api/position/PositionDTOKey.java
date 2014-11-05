package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import android.support.annotation.NonNull;

public interface PositionDTOKey extends DTOKey
{
    @NonNull Bundle getArgs();
}
