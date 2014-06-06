package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

public interface GetPositionsDTOKey extends DTOKey
{
    Bundle getArgs();
    boolean isValid(); // TODO remove?
}
