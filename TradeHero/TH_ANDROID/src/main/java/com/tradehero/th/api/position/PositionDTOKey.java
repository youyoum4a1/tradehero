package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import org.jetbrains.annotations.NotNull;

public interface PositionDTOKey extends DTOKey
{
    @NotNull Bundle getArgs();
}
