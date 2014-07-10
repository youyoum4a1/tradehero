package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import org.jetbrains.annotations.NotNull;

public interface GetPositionsDTOKey extends DTOKey
{
    @NotNull Bundle getArgs();
    boolean isValid(); // TODO remove?
}
